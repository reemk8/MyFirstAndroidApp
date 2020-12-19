package com.test.myfirstandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class First3Fragment extends Fragment {
    private Runnable autoUpdater;
    private boolean isLoadFromFirebase;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first3, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference userDb = firebaseDB.getReference().child("users");

        SQLiteDb sqlDb = new SQLiteDb(First3Fragment.super.getContext());

        TableLayout table = (TableLayout) view.findViewById(R.id.tbl_sql);

        try {
            Handler handler = new android.os.Handler();

            autoUpdater = new Runnable() {
                public void run() {
                    table.removeViews(1,table.getChildCount()-1);
                    // Fetch users from database
                    if(isLoadFromFirebase)
                        FetchUsersFromFirebase(userDb,table);
                    else
                        FetchUsers(sqlDb,table);

                    handler.postDelayed(autoUpdater, 10000);
                }
            };
            autoUpdater.run();
        }
        catch (Exception e){
            // Show toast message as Unknown error
            Toast.makeText(First3Fragment.super.getContext(),"Unknown error occurred while fetching users",Toast.LENGTH_LONG).show();
        }

        // Set click listener on 'new post' button
        view.findViewById(R.id.btn_sql_weather_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On click it navigates to add new form
                NavHostFragment.findNavController(First3Fragment.this)
                        .navigate(R.id.action_first3Fragment_to_second3Fragment);
            }
        });

        // Set click listener on 'new post' button
        view.findViewById(R.id.btn_main_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On click it navigates to add new form
                Intent intent = new Intent(First3Fragment.super.getContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        Button loadDataBtn= (Button)view.findViewById(R.id.btn_data_firebase);
        // Set click listener on 'new post' button
        loadDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    table.removeViews(1,table.getChildCount()-1);
                    if(!isLoadFromFirebase) {
                        // Get database refrence to users
                        FetchUsersFromFirebase(userDb, table);
                        isLoadFromFirebase = true;
                        loadDataBtn.setText("Load Users From SQLite");

                    }
                    else {
                        FetchUsers(sqlDb,table);
                        isLoadFromFirebase = false;
                        loadDataBtn.setText("Load Users From Firebase");
                    }
                    Toast.makeText(First3Fragment.super.getContext(),"Fetching Users from Firebase database...",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(First3Fragment.super.getContext(),"Unknown error occurred while fetching users",Toast.LENGTH_LONG).show();
                }

            }
        });

        }

    public void FetchUsers(SQLiteDb userDb,TableLayout table){
        Cursor users = userDb.GetAllUsers();
        if(users != null) {
            while (users.moveToNext()) {
                User user=new User(users.getInt(0),users.getString(1),users.getString(2),users.getString(3),users.getString(4));
                InsertRowInTable(table,user);
            }
        }
    }

    //Fetch Users from database
    public void FetchUsersFromFirebase(DatabaseReference userDb,TableLayout table){

        //Get complete snapshot of data from root data refrence in database
        userDb.addValueEventListener(new ValueEventListener() {
            // If refrence is valid to take snapshot
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Check if refrence has data
                if(dataSnapshot.exists()) {
                    // Go to each user node from data snapshot one by one
                    for(DataSnapshot userSnapshot :dataSnapshot.getChildren()) {
                        // Get user details from particular data snapshot refrence
                        User user = userSnapshot.getValue(User.class);
                        InsertRowInTable(table,user);
                    }
                }
            }

            // If refrence is invalid to take snapshot
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Show toast message as User not exist
                Toast.makeText(First3Fragment.super.getContext(),"Users couldn't fetched from database",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void InsertRowInTable(TableLayout table,User user){
        TableRow row = adjustTable(user);

        // Create cells by provided properties and append in row
        createCell(row,150,user.getEmailAddress(),Color.BLACK);
        createCell(row,60,user.getFirstName(),Color.BLACK);
        createCell(row,60,user.getLastName(),Color.BLACK);
        createCell(row,40,user.getPhoneNumber(),Color.BLACK);

        // Append row in table
        table.addView(row);
    }

    public void createCell(TableRow row,int width,String value,int color){
        TextView cell = new TextView(First3Fragment.super.getContext());
        cell.setId(getId());
        cell.setText(value);
        cell.setTextColor(color);
        row.addView(cell, width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public TableRow adjustTable(User user){
        // Create row
        TableRow row = new TableRow(First3Fragment.super.getContext());

        // Create Id column
        TextView id = new TextView(First3Fragment.super.getContext());
        id.setId(getId());
        id.setText("   "+user.getUserId());
        id.setTextColor(Color.BLUE);

        // Add click listener on id column
        id.setOnClickListener(new View.OnClickListener() {
            // On click it navigates to update form
            @Override
            public void onClick(View v) {
                //Create bundler
                Bundle args= new Bundle();
                // Add properties in bundler from fetched user from database
                String [] fetchedUser=new String[5];
                fetchedUser[0]=""+user.getUserId();
                fetchedUser[1]=user.getEmailAddress();
                fetchedUser[2]=user.getFirstName();
                fetchedUser[3]=user.getLastName();
                fetchedUser[4]=user.getPhoneNumber();

                // Pass bunddler as argument
                args.putStringArray("User",fetchedUser);

                NavHostFragment.findNavController(First3Fragment.this)
                        .navigate(R.id.action_first3Fragment_to_second3Fragment,args);
                    }
            });

        row.addView(id, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return row;
       }
}