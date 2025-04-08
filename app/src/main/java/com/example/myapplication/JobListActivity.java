package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

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

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class JobListActivity extends AppCompatActivity {

    private static final String LOG_TAG = JobListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<JobItem> mItemsData;
    private JobItemAdapter mAdapter;
    private boolean viewRow = true;
    private int gridNumber = 1;

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

        initializeData();


    }

    private void initializeData() {

        mItemsData.clear();
        mItemsData.add(new JobItem("1", "Java fejlesztő", "ProgTech Kft.", "Budapest", "Br. 600.000 Ft", "Teljes munkaidő"));
        mItemsData.add(new JobItem("2", "UI/UX Designer", "DesignStúdió", "Debrecen", "Br. 520.000 Ft", "Részmunkaidő"));
        mItemsData.add(new JobItem("3", "Tesztmérnök", "Tesztelők Bt.", "Szeged", "Br. 580.000 Ft", "Home Office"));
        mItemsData.add(new JobItem("4", "Rendszergazda", "Infrasys Kft.", "Pécs", "Br. 620.000 Ft", "Teljes munkaidő"));

        mAdapter.notifyDataSetChanged();
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
                mAdapter.getFilter().filter(s);
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
            return true;
        } else if (id == R.id.settings_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            // TODO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
