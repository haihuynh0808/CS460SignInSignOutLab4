package com.example.cs460signinsignoutlab4.activity;

import static com.example.cs460signinsignoutlab4.utilities.Constants.KEY_NAME;
import static java.util.Base64.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import android.util.Patterns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cs460signinsignoutlab4.R;
import com.example.cs460signinsignoutlab4.databinding.ActivitySignUpBinding;
import com.example.cs460signinsignoutlab4.utilities.Constants;
import com.example.cs460signinsignoutlab4.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.util.Base64;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Displays the registration screen and let user choose a profile picture
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private PreferenceManager preferenceManager;

    private String encodeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    /**
     * Connects registration, sign-up validation, and photo selection to their action.
     */
    private void setListeners() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidateSignUpDetails()) {
                  SignUp();
            }
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    /**
     * Displays a short message to the user
     *
     * @param message feedback to display
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates a secure Firebase Authentication account, saves profile picture
     */
    private void SignUp() {
        // Check loading
        loading(true);

        // Post to Firebase
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME,binding.inputFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME,binding.inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());

        user.put(Constants.KEY_IMAGE,encodeImage);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodeImage);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    /**
     * Shrinks the image to 150 pixels wide and converts it to Base64 JPEG
     *
     * @param bitmap selected image to encode
     * @return Base64 JPEG string for the Firestore profile
     */
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodeImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    /**
     * Checks all required fields, photo selection, email format, and password matching
     *
     * @return true if all information is acceptable for registration
     */
    private boolean isValidateSignUpDetails() {
        if (encodeImage == null) {
            showToast("Please select your image");
            return false;
        } else if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your first name");
            return false;
        } else if (binding.inputLastName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your last name");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter your email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString().trim()).matches()) {
            showToast("Please enter a valid email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter your password");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Please confirm your password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Password & Confirm Password must match");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Toggles the registration button and progress bar
     *
     * @param isLoading true during Firebase request
     */
    private void loading (Boolean isLoading) {
        if(isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

}