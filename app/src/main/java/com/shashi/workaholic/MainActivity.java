package com.shashi.workaholic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private RecyclerView recyclerView;
    Button buttonAdd;

    private ProgressBar progressBar;

    private ArrayList<String> dataList;
    private CustomAdapter myAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firebaseFirestore;

    public static final String COLLECTION_NAME = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        myAdapter.notifyDataSetChanged();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        updateUi();

        getFireStoreData();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUi();
            }
        };

    }

    private void initViews() {

        progressBar = findViewById(R.id.progressBar_main_layout);
        textView = findViewById(R.id.text_view_heading_main_activity);

        recyclerView = findViewById(R.id.recycler_view_main_activity);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        myAdapter = new CustomAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(myAdapter);

        buttonAdd = findViewById(R.id.button_add_data_main_activity);
        buttonAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View layoutView = layoutInflater.inflate(R.layout.alert_dialog_layout, null);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).setView(layoutView);
        alertDialog.setCancelable(true);
        final AlertDialog testDialog = alertDialog.create();
        testDialog.show();

        final EditText editTextNewData = layoutView.findViewById(R.id.edit_text_alert_dialog_add);
        Button button = layoutView.findViewById(R.id.button_alert_dialog_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newTask = editTextNewData.getText().toString().trim();

                if (newTask.isEmpty()) {
                    editTextNewData.setError("cannot be empty");
                    return;
                }

                saveNewData(newTask);
                updateAdapterData(newTask);
                testDialog.dismiss();  // to dismiss

            }
        });

    }

    private void updateUi() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            textView.setText("no user");
            return;
        } else {

            firebaseFirestore.collection(COLLECTION_NAME)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            //Check if the document exists
                            if (documentSnapshot.exists()) {

                                String name = documentSnapshot.getString("name");

                                if (name.isEmpty()) {
                                    name = "";
                                }

                                textView.setText("Welcome " + name);

                            } else {
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void getFireStoreData() {

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("tasks");

        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            String data = snapshot.getString("task");
                            String useridFirestore = snapshot.getString("userid");

                            String userid = firebaseAuth.getCurrentUser().getUid();

                            if (userid.equals(useridFirestore)) {
                                dataList.add(data);
                            }

                        }

                        myAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void saveNewData(String newTask) {
        CollectionReference collectionReference = firebaseFirestore.collection("tasks");

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("task", newTask);
        taskMap.put("userid", firebaseAuth.getCurrentUser().getUid());

        collectionReference.add(taskMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapterData(String newTask) {
        dataList.add(newTask);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;
            case R.id.menu_profile:
                startActivity(new Intent(MainActivity.this, ViewProfileActivity.class));
                return true;
            case R.id.menu_developer_contact:
                Toast.makeText(this, "Email: coding.shashi@gmail.com", Toast.LENGTH_LONG).show();
                return true;
        }

        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }
}