package com.example.myapplication;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userCityEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    EditText phoneEditText;
    Spinner spinner;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if(secret_key != 99) finish();

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userCityEditText = findViewById(R.id.cityEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordAgainEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        spinner = findViewById(R.id.genderSpinner);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "Nem egyezik a két jelszó!");
            return;
        }

        String phone = phoneEditText.getText().toString();
        String gender = spinner.getSelectedItem().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "User created successfully");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userNameEditText.getText().toString().trim())
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Log.d(LOG_TAG, "User profile updated.");
                                            }
                                            start();
                                        });
                            }

                        } else {
                            Log.d(LOG_TAG, "User wasn't created successfully:", task.getException());
                            Toast.makeText(RegisterActivity.this, "User wasn't created successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void cancel(View view) {
        finish();
    }


    private void start(){
        Intent intent = new Intent(this, JobListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}