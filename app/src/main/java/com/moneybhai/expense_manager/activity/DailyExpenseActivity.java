package com.moneybhai.expense_manager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moneybhai.expense_manager.R;
import com.moneybhai.expense_manager.adapter.DailyExpenseAdapter;
import com.moneybhai.expense_manager.adapter.DateAdapter;
import com.moneybhai.expense_manager.model.DailyExpense;
import com.moneybhai.expense_manager.model.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DailyExpenseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView no_data_text,total_textView;
    private long childrenCount;
    List<DailyExpense> dailyExpenseList;
    DailyExpenseAdapter dailyExpenseAdapter;
    DatabaseReference dbTotal;
    Long ttl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_expense);
        init();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyExpenseList = new ArrayList<>();
        downloadData();
        String date = getIntent().getExtras().getString("DATE");
        dbTotal = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(date);
        dbTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ttl = (Long) dataSnapshot.child("Total").getValue();
                    total_textView.setText("Total Expense "+"\t"+String.valueOf(ttl));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(date);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(date).child("TodayData");

        //Get children count
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialoge();
            }

            private void showCustomDialoge() {
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(DailyExpenseActivity.this).inflate(R.layout.add_expence_layout,
                        viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(DailyExpenseActivity.this);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Button btn = alertDialog.findViewById(R.id.button_add_expense);
                final EditText amount,text;
                amount = alertDialog.findViewById(R.id.editText_money);
                text = alertDialog.findViewById(R.id.editText_text);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String TEXT = text.getText().toString();
                        final String AMOUNT = amount.getText().toString();
                        if (TEXT.isEmpty()){
                            text.setError("enter text");
                            text.requestFocus();
                            return;
                        }
                        if (AMOUNT.isEmpty()){
                            amount.setError("enter amount");
                            amount.requestFocus();
                            return;
                        }
                        String date = getIntent().getExtras().getString("DATE");
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(date).child("TodayData").child(UUID.randomUUID().toString());
                        db.child("Text").setValue(TEXT);
                        db.child("Amount").setValue(AMOUNT);
                        db.child("Index").setValue(childrenCount);
                        db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    amount.getText().clear();
                                    text.getText().clear();
                                    alertDialog.dismiss();
                                    dbTotal.child("Total").setValue(ttl+Long.valueOf(AMOUNT));

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DailyExpenseActivity.this, "Failed!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

                    }



        });

    }



    private void downloadData() {
        String date = getIntent().getExtras().getString("DATE");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(date).child("TodayData");
        db.orderByChild("Index").addValueEventListener(new ValueEventListener() {
            //put here ordered by key
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    dailyExpenseList.clear();

                    no_data_text.setVisibility(View.GONE);
                    for (DataSnapshot dailyExpenseSnapshot : dataSnapshot.getChildren()){
                        DailyExpense i = dailyExpenseSnapshot.getValue(DailyExpense.class);
                        dailyExpenseList.add(i);

                    }
                    dailyExpenseAdapter = new DailyExpenseAdapter(DailyExpenseActivity.this,dailyExpenseList);
                    recyclerView.setAdapter(dailyExpenseAdapter);
                }
                else{

                    progressBar.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DailyExpenseActivity.this, "error", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void init() {
        no_data_text = findViewById(R.id.no_data_daily);
        total_textView = findViewById(R.id.totalText);
       recyclerView = findViewById(R.id.daily_recyclerView);
       progressBar = findViewById(R.id.daily_progressBar);
       floatingActionButton = findViewById(R.id.daily_floatingButton);
    }
}

