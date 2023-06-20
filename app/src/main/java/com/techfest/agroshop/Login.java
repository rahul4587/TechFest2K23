package com.techfest.agroshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.techfest.agroshop.databinding.ActivityLoginBinding;
import com.techfest.agroshop.databinding.ActivitySignupBinding;

public class Login extends AppCompatActivity {
ActivityLoginBinding activityLoginBinding;
    GoogleSignInOptions gso;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    GoogleSignInClient gsc;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc= GoogleSignIn.getClient(getApplicationContext(),gso);
if(auth.getUid()!=null){
    Toast.makeText(this, "New Actiivity", Toast.LENGTH_SHORT).show();

}
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
                      if(task.isSuccessful()){
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