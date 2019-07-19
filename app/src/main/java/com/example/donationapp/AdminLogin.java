package com.example.donationapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLogin extends AppCompatActivity {

    Button bt_login;
    EditText et_em, et_ps;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        et_em = findViewById(R.id.et_email);
        et_ps = findViewById(R.id.et_pass);

        bt_login = findViewById(R.id.btn_login);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validlogin();

            }
        });



        SharedPreferences prefs = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int r = prefs.getInt("flg", 0);
        String na=prefs.getString("Em",null);
        usern.username=na;

        if (r == 2) {
            String a=prefs.getString("acc type",null);

            if(a.length()==0){

            }
            else if (a.equals("Tailor")) {

                startActivity(new Intent(AdminLogin.this, Main2Activity.class));
            }
            else if(a.equals("User"))
            {
                startActivity(new Intent(AdminLogin.this, Main2Activity.class));
                pd.dismiss();
            }
        }else{
            Toast.makeText(this, "Login here", Toast.LENGTH_SHORT).show();
        }


    }

    public void validlogin() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("admins/"+et_em.getText().toString());
        pd = new ProgressDialog(AdminLogin.this);
        pd.setTitle("Loading....");
        pd.show();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String em = dataSnapshot.child("Email").getValue(String.class);
                String pass = dataSnapshot.child("Password").getValue(String.class);
                String type = dataSnapshot.child("acc type").getValue(String.class);

                if (et_em.getText().toString().equals(em)) {

                    if (et_ps.getText().toString().equals(pass)) {


                            SharedPreferences.Editor editor = getSharedPreferences("LOGIN", MODE_PRIVATE).edit();
                            editor.putInt("flg", 2);
                            editor.putString("Em", et_em.getText().toString());

                            editor.putString("acc type",type);
                            editor.apply();

                                startActivity(new Intent(AdminLogin.this, Main2Activity.class));
                                pd.dismiss();


                    } else {

                        et_ps.setError("Wrong Password");
                        pd.dismiss();
                    }

                } else {
                    et_em.setError("Invalid Contact");
                    pd.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
                et_em.setError("Invalid User");
            }
        });
    }
}
