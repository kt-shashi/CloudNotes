package com.shashi.workaholic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextname;
    private Button buttonUpdate;
    private TextView textViewStatus;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    public static final String COLLECTION_NAME = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(COLLECTION_NAME);

        initviews();
    }

    private void initviews() {

        editTextname = findViewById(R.id.edit_text_name_view_profile_activity);
        buttonUpdate = findViewById(R.id.button_update_name_view_profile_activity);
        textViewStatus = findViewById(R.id.text_view_result_view_activity);

        setName();

        buttonUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        String name = editTextname.getText().toString();

        if (name.isEmpty()) {
            editTextname.setError("Cannot be empty");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);

        collectionReference
                .document(firebaseAuth.getCurrentUser().getUid())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        textViewStatus.setText("Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setName() {

        collectionReference.document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            String name = documentSnapshot.getString("name");
                            editTextname.setText(name);

                        } else {
                            Toast.makeText(ViewProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}