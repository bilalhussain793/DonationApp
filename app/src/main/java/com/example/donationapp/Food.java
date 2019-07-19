package com.example.donationapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Food extends Fragment {

    View view;
    private Fragment baseContext;
    private int contentView;

    ListView lv;
    ArrayList<String> arrayList=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Firebase firebase;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Firebase fb;
    String a,b;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_food, container, false);
        Firebase.setAndroidContext(getContext());


        lv=view.findViewById(R.id.lv);

        firebase=new Firebase("https://donationapp-f4b46.firebaseio.com/donations/Food");

        adapter=new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,arrayList);

        lv.setAdapter(adapter);

      firebase.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {

              arrayList.add(s);
              adapter.notifyDataSetChanged();
              Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();

          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(FirebaseError firebaseError) {

          }
      });

        return view;


    }


}