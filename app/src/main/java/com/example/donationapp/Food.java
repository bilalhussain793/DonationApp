package com.example.donationapp;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.android.volley.VolleyLog.TAG;


public class Food extends Fragment {

    View view;
    private Fragment baseContext;
    private int contentView;


    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    Dialog dialog;
    ImageView iv;
    TextView tvd,tvn,tvc;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_food, container, false);

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.donated_layout);
        iv=dialog.findViewById(R.id.imv);
        tvd=dialog.findViewById(R.id.dec);
        tvn=dialog.findViewById(R.id.tv_p);
        tvc=dialog.findViewById(R.id.tv_c);


        usersList = (ListView) view.findViewById(R.id.lv);
        noUsersText = (TextView) view.findViewById(R.id.txt);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://donationapp-f4b46.firebaseio.com/donations/Food.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                usern.chatWith = al.get(position);
              //  startActivity(new Intent(getContext(), Chat.class));
                tvn.setText(al.get(position));
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("donations/Food/"+al.get(position));

                // Read from the database

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.child("User").getValue(String.class);
                        Toast.makeText(getContext(), ""+value, Toast.LENGTH_SHORT).show();
                        tvd.setText(al.get(position));
                        tvn.setText(value+"");
                        Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/donationapp-f4b46.appspot.com" +
                                "/o/images%2F"+value+"?alt=media&token=2f61f8e8-7144-46a9-83e5-2c50f89789c9").into(iv);

                        //   Log.d(TAG, "Value is: " + value);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                      //  Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

                dialog.show();

            }
        });


        return view;
    }

    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();

                if (!key.equals(usern.username)) {
                    al.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers <= 1) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();

    }
}