package com.example.donationapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrgLogin extends AppCompatActivity {

    Button bt_login;
    EditText et_pn,et_ps;
    Button reg_btn;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_login);

        et_pn=findViewById(R.id.et_email);
        et_ps=findViewById(R.id.et_pass);

        bt_login=findViewById(R.id.btn_login);
        reg_btn=findViewById(R.id.btn_reg);

        SharedPreferences prefs = getSharedPreferences("LOGIN1", MODE_PRIVATE);
        int r = prefs.getInt("flg", 0);
        String s = prefs.getString("phone", null);
        if (r == 2) {
            usern.usr=s;
            startActivity(new Intent(new Intent(OrgLogin.this,UserHome.class)));
            finish();
        }else{
            Toast.makeText(this, "Login here", Toast.LENGTH_SHORT).show();
        }

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validlogin(et_pn,et_ps);

            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrgLogin.this,OrgRegister.class));
            }
        });

    }

    public void validlogin(final EditText phone, final EditText password){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("org/"+phone.getText().toString());
        pd=new ProgressDialog(OrgLogin.this);
        pd.setTitle("Loading....");
        pd.show();

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String phn = dataSnapshot.child("Email").getValue(String.class);
                String pass = dataSnapshot.child("Password").getValue(String.class);

                if(phone.getText().toString().equals(phn)){

                    if(password.getText().toString().equals(pass)){

                            SharedPreferences.Editor editor = getSharedPreferences("LOGIN1", MODE_PRIVATE).edit();
                            editor.putInt("flg", 2);
                            editor.putString("phone",phone.getText().toString());
                            editor.apply();
                            startActivity(new Intent(OrgLogin.this,UserHome.class));
                            pd.dismiss();

                    }else {

                        password.setError("Wrong Password");
                    }

                }else {
                    phone.setError("Invalid Contact");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
                phone.setError("Invalid User");
            }
        });
    }
}