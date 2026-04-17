package com.example.recipebook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipebook.data.Recipe;
import com.example.recipebook.data.RecipeDatabase;
import com.example.recipebook.ui.RecipeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecipeDatabase db;
    private RecipeAdapter adapter;
    private EditText etSearch;
    private String currentCategory = "Все";


    private TextView chipAll, chipMain, chipDesserts, chipDrinks, chipSalads, chipSoups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        etSearch = findViewById(R.id.etSearch);
        ImageView ivFilter = findViewById(R.id.ivFilter);


        chipAll = findViewById(R.id.chipAll);
        chipMain = findViewById(R.id.chipMain);
        chipDesserts = findViewById(R.id.chipDesserts);
        chipDrinks = findViewById(R.id.chipDrinks);
        chipSalads = findViewById(R.id.chipSalads);
        chipSoups = findViewById(R.id.chipSoups);

        adapter = new RecipeAdapter();
        recyclerView.setAdapter(adapter);
        db = RecipeDatabase.getInstance(this);


        chipAll.setOnClickListener(v -> setCategory("Все", chipAll));
        chipMain.setOnClickListener(v -> setCategory("🥗 Основное", chipMain));
        chipDesserts.setOnClickListener(v -> setCategory("🍰 Десерты", chipDesserts));
        chipDrinks.setOnClickListener(v -> setCategory("🥤 Напитки", chipDrinks));
        chipSalads.setOnClickListener(v -> setCategory("🥗 Салаты", chipSalads));
        chipSoups.setOnClickListener(v -> setCategory("🍲 Супы", chipSoups));


        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadRecipes();
                } else {
                    if (currentCategory.equals("Все")) {
                        db.recipeDao().searchRecipes(query).observe(MainActivity.this, adapter::setRecipes);
                    } else {
                        db.recipeDao().searchRecipesByCategory(query, currentCategory)
                                .observe(MainActivity.this, adapter::setRecipes);
                    }
                }
            }
        });


        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // FAB
        fab.setOnClickListener(v -> {
            v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100)
                    .withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }).start();
        });


        ivFilter.setOnClickListener(v ->
                Toast.makeText(this, "Фильтры скоро!", Toast.LENGTH_SHORT).show());

        loadRecipes();
    }

    private void setCategory(String category, TextView selectedChip) {
        currentCategory = category;


        TextView[] chips = {chipAll, chipMain, chipDesserts, chipDrinks, chipSalads, chipSoups};
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.chip_inactive_background);
            chip.setTextColor(getResources().getColor(R.color.text_primary, null));
        }
        selectedChip.setBackgroundResource(R.drawable.chip_active_background);
        selectedChip.setTextColor(getResources().getColor(R.color.white, null));

        loadRecipes();
    }

    private void loadRecipes() {
        if (currentCategory.equals("Все")) {
            db.recipeDao().getAllRecipes().observe(this, adapter::setRecipes);
        } else {
            db.recipeDao().getRecipesByCategory(currentCategory).observe(this, adapter::setRecipes);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Завершить приложение?")
                .setPositiveButton("Да", (d, w) -> finish())
                .setNegativeButton("Нет", null)
                .show();
    }
}