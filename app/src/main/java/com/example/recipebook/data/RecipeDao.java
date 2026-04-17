package com.example.recipebook.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY id DESC")
    LiveData<List<Recipe>> getRecipesByCategory(String category);

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY id DESC")
    LiveData<List<Recipe>> searchRecipes(String query);

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' AND category = :category ORDER BY id DESC")
    LiveData<List<Recipe>> searchRecipesByCategory(String query, String category);

    @Insert
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe getRecipeById(int id);
}