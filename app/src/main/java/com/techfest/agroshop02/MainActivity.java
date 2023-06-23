package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techfest.agroshop02.databinding.ActivityMainBinding;

import Models.FarmersModel;
import Models.PreferanceManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    PreferanceManager preferanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferanceManager=new PreferanceManager(getApplicationContext());
        loadUserDetails();

    }
    private  void loadUserDetails(){
        if(preferanceManager.getString(FarmersModel.KEY_FNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_FNAME));
        else if (preferanceManager.getString(FarmersModel.KEY_CNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_CNAME));
        else if (preferanceManager.getString(FarmersModel.KEY_DNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_DNAME));
    }
    private void updateToken(String token){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER)

                .document(preferanceManager.getString(FarmersModel.KEY_USERID));
        documentReference.update(FarmersModel.KEY_FCM,token)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Token Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Unable to Update Token", Toast.LENGTH_SHORT).show());
    }
}