package com.shashi.workaholic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public static final String COLLECTION_NAME = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        editTextName = findViewById(R.id.edit_text_name_retgister_activity);
        editTextEmail = findViewById(R.id.edit_text_email_retgister_activity);
        editTextPassword = findViewById(R.id.edit_text_password_register_activity);
        buttonRegister = findViewById(R.id.button_register_register_activity);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!isValid(name, email, password)) {
            return;
        }

        registerUser(name, email, password);
    }

    private void registerUser(final String name, final String email, final String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();

                            String userId = firebaseAuth.getCurrentUser().getUid();

                            DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME)
                                    .document(userId);

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);

                            documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            });

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            editTextEmail.setError("Email already in use");
                        }
                    }
                });

    }

    private boolean isValid(String name, String email, String password) {

        if (name.isEmpty()) {
            editTextName.setError("Cannot be Empty");
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Cannot be Empty");
            return false;
        }

        if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid Email");
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Cannot be Empty");
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be 6 character long");
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}