package com.techfest.agroshop02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techfest.agroshop02.databinding.ActivityLoginBinding;

import Models.FarmersModel;
import Models.PreferanceManager;


public class Login extends AppCompatActivity {
ActivityLoginBinding activityLoginBinding;
    GoogleSignInOptions gso;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    GoogleSignInClient gsc;
    ProgressDialog progressDialog;
    PreferanceManager preferanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        preferanceManager=new PreferanceManager(getApplicationContext());

if(preferanceManager.getBoolean(FarmersModel.KEY_IS_SIGNED_IN)){
    startActivity(new Intent(Login.this,MainActivity.class));
    finish();
}




        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc= GoogleSignIn.getClient(getApplicationContext(),gso);
//if(auth.getUid()!=null){
//    Toast.makeText(this, "New Actiivity", Toast.LENGTH_SHORT).show();
//
//}
        activityLoginBinding.GoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlesignin();
            }
        });
activityLoginBinding.layoutdirecttosignup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        directtosignup();
    }
});

activityLoginBinding.LoginBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        checkData();
    }
});

activityLoginBinding.ForgotPassword.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showForgotPassDialog();
    }
});
    }

    private void showForgotPassDialog() {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        EditText email=dialog.findViewById(R.id.ForgotEmailEdittext);
dialog.findViewById(R.id.sendRecoveryEmailBtn).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(TextUtils.isEmpty(email.getText().toString())){
        email.setError("Required");
        email.setText("");
    } else {
        sendRecoveryEmail(email.getText().toString(),dialog);
    }

    }
});

    }

    private void sendRecoveryEmail(String email,Dialog dialog) {
         auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful()){

    dialog.dismiss();
    Toast.makeText(Login.this, "Check Your Email", Toast.LENGTH_SHORT).show();

}
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(Login.this, e.getLocalizedMessage()
                         , Toast.LENGTH_SHORT).show();
             }
         });


    }


    private void checkData() {
        if (TextUtils.isEmpty(activityLoginBinding.Email.getText().toString())) {
            
            activityLoginBinding.Email.setError("Enter Email");
            return;
        }
        else if (TextUtils.isEmpty(activityLoginBinding.LoginPassword.getText().toString())) {

            activityLoginBinding.LoginPassword.setError("Enter Password");
            return;
        }
        else{
            loginAccount();
        }
    }

    private void loginAccount() {
        progressDialog=new ProgressDialog(Login.this);
        progressDialog.setTitle("Just a moment....");
        progressDialog.setMessage("Logging in");
        progressDialog.show();
        auth.signInWithEmailAndPassword(activityLoginBinding.Email.getText().toString(),activityLoginBinding.LoginPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                      if(task.isSuccessful()&& task.getResult()!=null){

                          FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                          firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER)
                                  .whereEqualTo(FarmersModel.KEY_EMAIL,activityLoginBinding.Email.getText().toString())
                                          .whereEqualTo(FarmersModel.KEY_PAASSWORD,activityLoginBinding.LoginPassword.getText().toString())
                                                  .get()
                                                          .addOnCompleteListener(task1 -> {
if(task1.isSuccessful()&&task1.getResult().getDocuments().size()>0){

    DocumentSnapshot documentSnapshot=task1.getResult().getDocuments().get(0);
    preferanceManager.putBoolean(FarmersModel.KEY_IS_SIGNED_IN,true);
    preferanceManager.putString(FarmersModel.KEY_USERID,documentSnapshot.getId());
   if(documentSnapshot.getString(FarmersModel.KEY_FNAME)!=null){
       preferanceManager.putString(FarmersModel.KEY_FNAME,documentSnapshot.getString(FarmersModel.KEY_FNAME));
   }else if(documentSnapshot.getString(FarmersModel.KEY_CNAME)!=null){
       preferanceManager.putString(FarmersModel.KEY_CNAME,documentSnapshot.getString(FarmersModel.KEY_CNAME));
   } else if (documentSnapshot.getString(FarmersModel.KEY_DNAME)!=null){
       preferanceManager.putString(FarmersModel.KEY_DNAME,documentSnapshot.getString(FarmersModel.KEY_DNAME));
   }
   preferanceManager.putString(FarmersModel.KEY_PICTURE_URI,documentSnapshot.getString(FarmersModel.KEY_PICTURE_URI));
   Intent intent=new Intent(getApplicationContext(),MainActivity.class);
   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
   startActivity(intent);
}
else{
    Toast.makeText(Login.this, "Unable to Signin", Toast.LENGTH_SHORT).show();
}


                                                          });


                          Toast.makeText(Login.this, "Enter new activity", Toast.LENGTH_SHORT).show();
                      }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();     
                    }
                });
        
    }

    private void googlesignin() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                // Toast.makeText(SignupActivity.this, "Signed in", Toast.LENGTH_SHORT).show();

                firebasewithgoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void firebasewithgoogle(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Signed in", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void directtosignup(){
        startActivity(new Intent(Login.this,SignupActivity.class));
    }



}