package com.example.recipebook;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recipebook.data.Recipe;
import com.example.recipebook.data.RecipeDatabase;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {
    private RecipeDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView tvTitle, tvIngredients, tvInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvInstructions = findViewById(R.id.tvInstructions);

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnGoHome = findViewById(R.id.btnGoHome);

        db = RecipeDatabase.getInstance(this);
        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);


        btnBack.setOnClickListener(v -> finish());


        btnGoHome.setOnClickListener(v -> finish());

        executor.execute(() -> {
            Recipe recipe = db.recipeDao().getRecipeById(recipeId);
            runOnUiThread(() -> {
                if (recipe != null) {
                    tvTitle.setText(recipe.getTitle());
                    tvIngredients.setText(recipe.getIngredients());
                    tvInstructions.setText(recipe.getInstructions());
                } else {
                    tvTitle.setText("Рецепт не найден");
                }
            });
        });
    }
}