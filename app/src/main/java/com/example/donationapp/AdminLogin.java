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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminLogin extends AppCompatActivity {

    EditText username, password;
    Button loginButton;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        username = (EditText)findViewById(R.id.et_email);
        password = (EditText)findViewById(R.id.et_pass);
        loginButton = (Button)findViewById(R.id.btn_login);


        login_validity();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if(user.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://donationapp-f4b46.firebaseio.com/admins.json";
                    final ProgressDialog pd = new ProgressDialog(AdminLogin.this);
                    pd.setTitle("Loading");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(AdminLogin.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(AdminLogin.this, "user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("Password").equals(pass)){
                                        usern.username = user;
                                        usern.password = pass;
                                        SharedPreferences.Editor editor = getSharedPreferences("log", MODE_PRIVATE).edit();
                                        editor.putInt("flg", 2);
                                        editor.putString("Em", user);
                                        editor.apply();
                                        startActivity(new Intent(AdminLogin.this, Main2Activity.class));
                                    }
                                    else {
                                        Toast.makeText(AdminLogin.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(AdminLogin.this);
                    rQueue.add(request);
                }

            }
        });
    }

    public void login_validity(){
        SharedPreferences prefs = getSharedPreferences("log", MODE_PRIVATE);
        int r = prefs.getInt("flg", 0);
        String na=prefs.getString("Em",null);

        if (r == 2) {

            if(na.length()==0){
                Toast.makeText(this, "Login here", Toast.LENGTH_SHORT).show();
            }
            else {
                usern.username=na;
                startActivity(new Intent(AdminLogin.this, Main2Activity.class));
            }
        }else{
            Toast.makeText(this, "Login here", Toast.LENGTH_SHORT).show();
        }
    }
}
