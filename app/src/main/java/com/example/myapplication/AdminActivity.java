package com.example.myapplication;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AdminActivity extends AppCompatActivity implements OnJobActionListener {

    private static final String LOG_TAG = AdminActivity.class.getName();
    private FirebaseFirestore mFirestore;
    private CollectionReference mJobsRef;

    private RecyclerView mRecyclerView;
    private JobItemAdapter mAdapter;
    private ArrayList<JobItem> mJobList;

    private EditText titleInput, companyInput, locationInput, salaryInput, typeInput;
    private Button addButton;

    private JobItem itemBeingEdited = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        titleInput = findViewById(R.id.editTextJobTitle);
        companyInput = findViewById(R.id.editTextCompany);
        locationInput = findViewById(R.id.editTextLocation);
        salaryInput = findViewById(R.id.editTextSalary);
        typeInput = findViewById(R.id.editTextWorkType);
        addButton = findViewById(R.id.buttonAddJob);

        mRecyclerView = findViewById(R.id.recyclerViewJobs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mJobList = new ArrayList<>();
        mAdapter = new JobItemAdapter(this, mJobList, true, this);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mJobsRef = mFirestore.collection("Jobs");

        addButton.setOnClickListener(view -> {
            if (itemBeingEdited != null) {
                updateExistingJob();
            } else {
                addNewJob();
            }
        });

        loadJobs();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadJobs() {
        mJobsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(LOG_TAG, "Firestore error: ", error);
                    return;
                }
                mJobList.clear();
                for (DocumentSnapshot doc : value) {
                    JobItem item = doc.toObject(JobItem.class);
                    item.setId(doc.getId());
                    mJobList.add(item);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addNewJob() {
        String title = titleInput.getText().toString().trim();
        String company = companyInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String salary = salaryInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();

        if (title.isEmpty() || company.isEmpty()) {
            Toast.makeText(this, "Töltse ki a kötelező mezőket!", Toast.LENGTH_SHORT).show();
            return;
        }

        JobItem job = new JobItem(null, title, company, location, salary, type);
        mJobsRef.add(job).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Állás hozzáadva!", Toast.LENGTH_SHORT).show();
            clearForm();
        });
    }

    private void clearForm() {
        titleInput.setText("");
        companyInput.setText("");
        locationInput.setText("");
        salaryInput.setText("");
        typeInput.setText("");
    }

    @Override
    public void onDeleteClick(JobItem job) {
        mJobsRef.document(job.getId()).delete()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Törölve", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onEditClick(JobItem job) {

        titleInput.setText(job.getTitle());
        companyInput.setText(job.getCompany());
        locationInput.setText(job.getLocation());
        salaryInput.setText(job.getSalary());
        typeInput.setText(job.getWorkType());

        itemBeingEdited = job;

        addButton.setText("Módosítás mentése");
    }

    private void updateExistingJob() {
        String title = titleInput.getText().toString().trim();
        String company = companyInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String salary = salaryInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();

        if (title.isEmpty() || company.isEmpty()) {
            Toast.makeText(this, "Töltsd ki a kötelező mezőket!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("title", title);
        update.put("company", company);
        update.put("location", location);
        update.put("salary", salary);
        update.put("workType", type);

        mJobsRef.document(itemBeingEdited.getId()).update(update)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Hirdetés frissítve!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    addButton.setText("Hirdetés mentése");
                    itemBeingEdited = null;
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
