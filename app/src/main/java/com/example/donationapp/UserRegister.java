package com.example.donationapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegister extends AppCompatActivity {
    EditText username, password,et_email,et_pass,et_phone,et_org;
    Button registerButton;
    String email, pass;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);


        et_email = (EditText)findViewById(R.id.et_email);
        et_phone = (EditText)findViewById(R.id.et_phone);
        et_org = (EditText)findViewById(R.id.et_org);
        username = (EditText)findViewById(R.id.et_username);
        password = (EditText)findViewById(R.id.et_pass);
        registerButton = (Button)findViewById(R.id.r_btn);
        login = (TextView)findViewById(R.id.login);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserRegister.this, UserLogin.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();
                pass = password.getText().toString();

                if(email.equals("")){
                    et_email.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else if(et_phone.getText().toString().equals("")){
                    et_phone.setError("can't be blank");
                }
                else if(et_org.getText().toString().equals("")){
                    et_org.setError("can't be blank");
                }
                else if(username.getText().toString().equals("")){
                    username.setError("can't be blank");
                }
                else if(email.contains("@")){
                    et_email.setError("not an email!");
                }
                else if(email.length()<5){
                    et_email.setError("at least 5 characters long");
                }
                else if(pass.length()<5){
                    password.setError("at least 5 characters long");
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(UserRegister.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://donationapp-f4b46.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://donationapp-f4b46.firebaseio.com/users");

                            if(s.equals("null")) {
                                reference.child(email).child("Password").setValue(pass);
                                Toast.makeText(UserRegister.this, "registration successful", Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(email)) {
                                        reference.child(email).child("Password").setValue(pass);
                                        reference.child(email).child("Email").setValue(email);
                                        reference.child(email).child("Phone").setValue(et_phone.getText().toString());
                                        reference.child(email).child("Organization").setValue(et_org.getText().toString());
                                        reference.child(email).child("Username").setValue(username.getText().toString());
                                        //image will be added there
                                        Toast.makeText(UserRegister.this, "registration successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(UserRegister.this, "user already exists", Toast.LENGTH_LONG).show();
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
                            System.out.println("" + volleyError );
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(UserRegister.this);
                    rQueue.add(request);
                }
            }
        });
    }
}
