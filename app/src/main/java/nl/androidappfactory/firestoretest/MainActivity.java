package nl.androidappfactory.firestoretest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import nl.androidappfactory.firestoretest.models.Article;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_ID = "id";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_DESCRIPTION = "description";
    private static final String COLLECTION_NAME = "Article";

    private EditText editTextId;
    private EditText editTextBrand;
    private EditText editTextDescription;
    private EditText editTextCategory;
    private TextView textViewData;

    private CollectionReference collectionReference;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection(COLLECTION_NAME);
        editTextId = findViewById(R.id.edit_text_id);
        editTextBrand = findViewById(R.id.edit_text_brand);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextCategory = findViewById((R.id.edit_text_category));
        textViewData = findViewById(R.id.text_view_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onEvent: " + error.getMessage());
                }
                textViewData.setText(createResultString(queryDocumentSnapshots));
            }
        });
    }

    // no need to remove the listener as we added "this" to the addSnapsotListener which will automatically
    // take care of removeing the listener when application stops
    //    @Override
    //    protected void onStop() {
    //        super.onStop();
    //        artilceListener.remove();
    //    }

    public void onAddNote(View view) {
        String brand = editTextBrand.getText().toString();
        String description = editTextDescription.getText().toString();
        String id = editTextId.getText().toString();
        if (editTextCategory.length() == 0) {
            editTextCategory.setText("0");
        }
        int category = Integer.parseInt(editTextCategory.getText().toString());

        Article article = new Article(brand, description, category);

        collectionReference.add(article)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: noteID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + article.toString());
                    }
                });
    }

    public void onLoadArticles(View view) {

        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        textViewData.setText(createResultString(queryDocumentSnapshots));
                    }
                });
    }

    public void onLoadByCategory(View view) {
        if (editTextCategory.length() == 0) {
            editTextCategory.setText("0");
        }
        int category = Integer.parseInt(editTextCategory.getText().toString());
        collectionReference
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        textViewData.setText(createResultString(queryDocumentSnapshots));
                    }
                });
    }

    public void onLoadFromCategory(View view) {
        if (editTextCategory.length() == 0) {
            editTextCategory.setText("0");
        }
        int category = Integer.parseInt(editTextCategory.getText().toString());
        collectionReference
                .whereGreaterThanOrEqualTo("category", category)
                .limit(1000)
                .orderBy("category", Query.Direction.ASCENDING)
                .orderBy("brand", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        textViewData.setText(createResultString(queryDocumentSnapshots));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    public void onUpdateDescription(View view) {
        String id = editTextId.getText().toString();
        String description = editTextDescription.getText().toString();

        collectionReference.document(id).update(KEY_DESCRIPTION, description)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "update description successfull, id: " + id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "update description failed, id: " + id);
                    }
                });
    }

    public void onLoad(View view) {
        String id = editTextId.getText().toString();
        collectionReference.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Article article = documentSnapshot.toObject(Article.class);
                            String brand = article.getBrand();
                            String description = article.getDescription();
                            textViewData.setText(brand + ": " + description);
                        } else {
                            textViewData.setText("Geen data gevonden");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textViewData.setText("Er is onverwacht iets fout gegaan.");
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    public void onDeleteDescription(View view) {
        String id = editTextId.getText().toString();
        collectionReference.document(id).update(KEY_DESCRIPTION, FieldValue.delete());
    }

    public void onDeleteNote(View view) {
        String id = editTextId.getText().toString();
        collectionReference.document(id).delete();
    }

    private String createResultString(QuerySnapshot queryDocumentSnapshots) {
        String data = "";
        if (queryDocumentSnapshots.isEmpty()) {
            data = "No articles found";
            Log.d(TAG, "No articles found");
        } else {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Article article = documentSnapshot.toObject(Article.class);
                article.setId(documentSnapshot.getId());
                Log.d(TAG, "onSuccess, id:" + documentSnapshot.getId() + ": " + article.toString());
                data += "Brand: " + article.getBrand() + "\nDescription: " + article.getDescription()
                        + "\nCategory: " + article.getCategory()
                        + "\n\n";
            }
        }
        return data;
    }
}