package com.example.recipebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.recipebook.data.Recipe;
import com.example.recipebook.data.RecipeDatabase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private RecipeDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EditText etTitle, etIngredients, etInstructions;
    private ImageView ivRecipePreview;
    private Button btnSave, btnSelectImage;
    private Uri imageUri;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);


        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        etTitle = findViewById(R.id.etTitle);
        etIngredients = findViewById(R.id.etIngredients);
        etInstructions = findViewById(R.id.etInstructions);
        ivRecipePreview = findViewById(R.id.ivRecipePreview);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        db = RecipeDatabase.getInstance(this);

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveRecipe());
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            showImagePicker();
        }
    }

    private void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivRecipePreview.setImageURI(imageUri);
            ivRecipePreview.setVisibility(ImageView.VISIBLE);

            try {
                imagePath = saveImageToInternalStorage(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка сохранения изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) throws Exception {
        String fileName = "recipe_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file.getAbsolutePath();
    }

    private void saveRecipe() {
        String title = etTitle.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Введите название");
            return;
        }

        Recipe newRecipe = new Recipe(title, ingredients, instructions, imagePath != null ? imagePath : "");

        executor.execute(() -> {
            db.recipeDao().insert(newRecipe);
            runOnUiThread(() -> {
                Toast.makeText(AddRecipeActivity.this, "Рецепт сохранён!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePicker();
            } else {
                Toast.makeText(this, "Нужно разрешение на галерею", Toast.LENGTH_SHORT).show();
            }
        }
    }
}