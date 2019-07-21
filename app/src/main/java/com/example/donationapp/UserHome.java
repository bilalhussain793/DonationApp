package com.example.donationapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UserHome extends AppCompatActivity {

    Button btn_logout,bt_donate,bt_cdn,bt_cancel;
    Spinner spinner;
    EditText et_desc;
    String userid;

    ProgressDialog pd;
    ImageView iv;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        pd=new ProgressDialog(UserHome.this);

        btn_logout=findViewById(R.id.btn_logout);
        bt_donate=findViewById(R.id.btn_don);
        userid=usern.usr;

        dialog=new Dialog(UserHome.this);
        dialog.setContentView(R.layout.donate_layout);


        bt_cdn=dialog.findViewById(R.id.btn_donate);
        bt_cancel=dialog.findViewById(R.id.btn_cancel);
        et_desc=dialog.findViewById(R.id.et_desc);
        iv=dialog.findViewById(R.id.imageView);
        spinner=dialog.findViewById(R.id.spinner);

        String ar[]={"Cloth","Food","Other"};
        ArrayAdapter adapter=new ArrayAdapter(UserHome.this,R.layout.support_simple_spinner_dropdown_item,ar);
        spinner.setAdapter(adapter);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        bt_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                dialog.setCancelable(false);
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_cdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("donations/"+spinner.getSelectedItem().toString()+"/"+et_desc.getText().toString());

                uploadImage();
                myRef.child("User").setValue(userid);
                myRef.child("Type").setValue(spinner.getSelectedItem().toString());
                myRef.child("Description").setValue(et_desc.getText().toString());

                Toast.makeText(UserHome.this, "Added successfully", Toast.LENGTH_SHORT).show();

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Toast.makeText(UserHome.this, "You have been LoggedOut", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ userid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(UserHome.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserHome.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                             progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("LOGIN1", MODE_PRIVATE).edit();
        editor.putInt("flg", 0);
        editor.putString("phone","");
        editor.apply();
        startActivity(new Intent(UserHome.this,MainActivity.class));
        finish();
    }

}