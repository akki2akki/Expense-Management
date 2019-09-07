package com.moneybhai.expense_manager.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneybhai.expense_manager.R;
import com.moneybhai.expense_manager.adapter.DateAdapter;
import com.moneybhai.expense_manager.model.Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    List<Date> dateList;
    DateAdapter dateAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView no_data_text;
    //firebase firestore data
    private long childrenCount;
    private String dateTime;

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser==null){
            SendUserToLoginActivity();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        init();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dateList = new ArrayList<>();
        if (currentUser!=null) {
            downloadData();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        childrenCount = dataSnapshot.getChildrenCount();

                    } else {
                        childrenCount = 0;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                format.setTimeZone(c.getTimeZone());
                dateTime = format.format(c.getTime());
                showCustomeDialoge();



            }

            private void showCustomeDialoge() {
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_date_dialog,
                        viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                TextView tv = alertDialog.findViewById(R.id.date_dialoge);
                tv.setText(dateTime);
                Button btn = alertDialog.findViewById(R.id.button_add_dialoge);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        uploadTodayDate();
                        alertDialog.dismiss();
                    }
                });

            }
        });
    }

    private void downloadData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.keepSynced(true);
        db.orderByChild("Index").addValueEventListener(new ValueEventListener() {
            //put here ordered by key
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    dateList.clear();
                    no_data_text.setVisibility(View.GONE);
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()){
                        Date i = dateSnapshot.getValue(Date.class);
                        dateList.add(0,i);

                    }
                    dateAdapter = new DateAdapter(MainActivity.this,dateList);
                    recyclerView.setAdapter(dateAdapter);
                }
                else{

                    progressBar.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void uploadTodayDate() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(dateTime);
        databaseReference.child("Date").setValue(dateTime);
        databaseReference.child("Index").setValue(childrenCount);
        databaseReference.child("Total").setValue(0);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.share_app:
                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = "Hey, Track your Daily Expense with Money Bhai";
                String link= "https://play.google.com/store/apps/details?id=com.moneybhai.expense_manager";
                intent.putExtra(Intent.EXTRA_TEXT, text +" " + link   );
                startActivity(Intent.createChooser(intent,"Share Via"));

                return true;
            case R.id.rate_app:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.moneybhai.expense_manager"));
                startActivity(intent);
                return true;
            case R.id.logout_app:
                FirebaseAuth.getInstance().signOut();
                SendUserToLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        floatingActionButton = findViewById(R.id.floatingButton);
        no_data_text = findViewById(R.id.no_data);
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


}
