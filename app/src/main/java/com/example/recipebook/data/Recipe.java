package com.example.recipebook.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String ingredients;
    private String instructions;
    private String imageUrl;
    private String category;

    public Recipe(String title, String ingredients, String instructions, String imageUrl, String category) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.category = category != null ? category : "Основное";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}