package com.shashi.workaholic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private Button buttonUpdate;
    private Button buttonDelete;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String COLLECTION_NAME = "tasks";
    private String oldTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        oldTask = getIntent().getStringExtra("keytask");

        initViews();

        editText.setText(oldTask);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initViews() {
        editText = findViewById(R.id.edit_text_task_update_activity);
        buttonUpdate = findViewById(R.id.button_save_data_update_activity);
        buttonDelete = findViewById(R.id.button_delete_data_update_activity);

        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_save_data_update_activity:
                updateData();
                break;
            case R.id.button_delete_data_update_activity:
                deleteData();
                break;
        }

    }

    private void updateData() {
        final String task = editText.getText().toString().trim();

        if (task.isEmpty()) {
            editText.setError("Cannot be empty");
            return;
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = firebaseFirestore.collection("tasks");
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            String data = snapshot.getString("task");
                            String useridFirestore = snapshot.getString("userid");
                            String userid = firebaseAuth.getCurrentUser().getUid();

                            if (userid.equals(useridFirestore) && data.equals(oldTask)) {
                                //update

                                collectionReference.document(snapshot.getId())
                                        .update("task", task)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdateDataActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpdateDataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    private void deleteData() {

        firebaseFirestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = firebaseFirestore.collection("tasks");
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            String data = snapshot.getString("task");
                            String useridFirestore = snapshot.getString("userid");
                            String userid = firebaseAuth.getCurrentUser().getUid();

                            if (userid.equals(useridFirestore) && data.equals(oldTask)) {

                                collectionReference.document(snapshot.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdateDataActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpdateDataActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }
}