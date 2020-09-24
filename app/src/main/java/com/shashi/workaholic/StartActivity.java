package com.shashi.workaholic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private Button buttonSignin;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initView();
    }

    private void initView() {
        buttonRegister = findViewById(R.id.button_register_main_activity);
        buttonSignin = findViewById(R.id.button_signin_main_activity);

        buttonRegister.setOnClickListener(this);
        buttonSignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register_main_activity:
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.button_signin_main_activity:
                startActivity(new Intent(StartActivity.this, SigninActivity.class));
                finish();
                break;
        }
    }
}