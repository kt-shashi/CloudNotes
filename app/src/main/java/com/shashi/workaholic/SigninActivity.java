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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initViews();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.edit_text_email_signin_activity);
        editTextPassword = findViewById(R.id.edit_text_password_signin_activity);
        buttonRegister = findViewById(R.id.button_signin_signin_activity);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!isValid(email, password)) {
            return;
        }

        loginUser(email, password);
    }

    private void loginUser(final String email, final String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Toast.makeText(SigninActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(SigninActivity.this, MainActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            editTextPassword.setError("Invalid Password");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            editTextEmail.setError("Email not in use");
                        }
                    }
                });
    }

    private boolean isValid(String email, String password) {

        if (email.isEmpty()) {
            editTextEmail.setError("Cannot be empty");
            return false;
        }

        if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid email");
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Cannot be empty");
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