package nl.androidappfactory.firestoretest.models;

import com.google.firebase.firestore.Exclude;

public class Article {

    private String id;
    private String brand;
    private String description;
    private int category;

    public Article() {
    }

    public Article(String brand, String description, int category) {
        this.brand = brand;
        this.description = description;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                '}';
    }
}
