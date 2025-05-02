package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class JobListActivity extends AppCompatActivity {

    private static final String LOG_TAG = JobListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<JobItem> mItemsData;
    private JobItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mJobs;
    private boolean viewRow = true;
    private int gridNumber = 1;

    private NotificationHandler mNotificationHandler;

    private AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemsData = new ArrayList<>();

        mAdapter = new JobItemAdapter(this, mItemsData);

        mRecyclerView.setAdapter(mAdapter);


        mFirestore = FirebaseFirestore.getInstance();
        mJobs = mFirestore.collection("Jobs");

        queryJobs();

        mNotificationHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setAlarmManager();


        Spinner sortSpinner = findViewById(R.id.sortSpinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        sortJobsByTitle();
                        break;
                    case 1:
                        sortJobsBySalary();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }

    private void initializeData() {
        Log.d(LOG_TAG, "initializeData() called");

        ArrayList<JobItem> jobs = new ArrayList<>();
        jobs.add(new JobItem("1", "Java fejlesztő", "ProgTech Kft.", "Budapest", "Br. 600.000 Ft", "Teljes munkaidő"));
        jobs.add(new JobItem("2", "UI/UX Designer", "DesignStúdió", "Debrecen", "Br. 520.000 Ft", "Részmunkaidő"));
        jobs.add(new JobItem("3", "Tesztmérnök", "Tesztelők Bt.", "Szeged", "Br. 580.000 Ft", "Home Office"));
        jobs.add(new JobItem("4", "Rendszergazda", "Infrasys Kft.", "Pécs", "Br. 620.000 Ft", "Teljes munkaidő"));

        for (JobItem job : jobs) {
            Log.d(LOG_TAG, "Adding job: " + job.getTitle());
            mJobs.add(job);
        }
    }




    private void queryJobs() {
        mItemsData.clear();
        mJobs.orderBy("title").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                JobItem job = doc.toObject(JobItem.class);
                mItemsData.add(job);
            }

            if (mItemsData.size() == 0) {
                initializeData();
                queryJobs();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    private void searchJobs(String searchText) {
        mItemsData.clear();
        mJobs
                .orderBy("title")  // ehhez index kell
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        JobItem job = doc.toObject(JobItem.class);
                        mItemsData.add(job);
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    private void sortJobsByTitle() {
        mItemsData.clear();
        mJobs
                .orderBy("title")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        JobItem job = doc.toObject(JobItem.class);
                        mItemsData.add(job);
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }


    private void sortJobsBySalary() {
        mItemsData.clear();
        mJobs
                .orderBy("title")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        JobItem job = doc.toObject(JobItem.class);
                        mItemsData.add(job);
                    }

                    Collections.sort(mItemsData, new Comparator<JobItem>() {
                        @Override
                        public int compare(JobItem j1, JobItem j2) {
                            return extractSalaryValue(j2.getSalary()) - extractSalaryValue(j1.getSalary());
                        }
                    });

                    mAdapter.notifyDataSetChanged();
                });
    }

    private int extractSalaryValue(String salary) {
        // Példa: "Br. 600.000 Ft" → 600000
        if (salary == null) return 0;

        String numeric = salary.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(numeric);
        } catch (NumberFormatException e) {
            return 0;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.job_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                searchJobs(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.log_out_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            finish();
            overridePendingTransition(0, R.anim.fade_out);
            return true;
        } else if (id == R.id.settings_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!user.getDisplayName().equals("")){
            Toast.makeText(this, "Üdv, " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
        }

    }

    private void setAlarmManager() {
        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);

    }

}
