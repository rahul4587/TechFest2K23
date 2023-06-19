package com.techfest.agroshop;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.techfest.agroshop.databinding.ActivitySignupBinding;

import java.util.HashMap;

import Models.FarmersModel;

public class SignupActivity extends AppCompatActivity {
ActivitySignupBinding activitySignupBinding;
FirebaseAuth auth=FirebaseAuth.getInstance();
FirebaseDatabase database=FirebaseDatabase.getInstance();
DatabaseReference databaseReference=database.getReference("Users");
FirebaseStorage storage=FirebaseStorage.getInstance();
ActivityResultLauncher<String> resultLauncher;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    HashMap<String, Object> signinmap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(activitySignupBinding.getRoot());


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Designation, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        activitySignupBinding.spinnerLanguages.setAdapter(adapter);
        activitySignupBinding.spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextSize(13);
                signinmap.put("Designation", ((TextView) adapterView.getChildAt(0)).getText().toString());
                if(((TextView) adapterView.getChildAt(0)).getText().toString().matches("Farmer")){
                    signinmap.put("FName",activitySignupBinding.Name.getText().toString());
                } else if (((TextView) adapterView.getChildAt(0)).getText().toString().matches("Carrier")) {
                    signinmap.put("CName",activitySignupBinding.Name.getText().toString());
                } else if (((TextView) adapterView.getChildAt(0)).getText().toString().matches("Distributor")) {
                    signinmap.put("DName",activitySignupBinding.Name.getText().toString());
                }
                else {
                    Toast.makeText(SignupActivity.this, "Choose correct one ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



            activitySignupBinding.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(TextUtils.isEmpty(activitySignupBinding.Phone.getText().toString())))  {
                    resultLauncher.launch("image/*");
                    return;}
                    else activitySignupBinding.Phone.setError("Enter Phone number First");
                }
            });
            resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    activitySignupBinding.profileImage.setImageURI(result);

                    final StorageReference storageReference = storage.getReference("Users").child(activitySignupBinding.Phone.getText().toString());
                    storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    signinmap.put("PictureUri",uri.toString());
                                    Toast.makeText(SignupActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


                }
            });


        activitySignupBinding.SignupBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        checkDataAvailability();
    }
});
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc=GoogleSignIn.getClient(this,gso);
      //Sign in with google
        activitySignupBinding.GoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, 1000);
            }
        });


 activitySignupBinding.gotologinActivity.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         startActivity(new Intent(SignupActivity.this, Login.class));

     }
 });

    }

    private void checkDataAvailability() {
        final String[] selected_item = new String[1];

        activitySignupBinding.spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // On selecting a spinner item
                selected_item[0] = adapter.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        if (TextUtils.isEmpty(activitySignupBinding.Name.getText().toString()))
        {activitySignupBinding.Name.setError("Enter Valid name"); return;}

        else if (TextUtils.isEmpty(activitySignupBinding.Email.getText().toString())) {
            activitySignupBinding.Email.setError("Enter Valid Email"); return;
        }
        else if (TextUtils.isEmpty(activitySignupBinding.Password.getText().toString())) {
            activitySignupBinding.Password.setError("Enter Valid Password"); return;
        }
        else if (TextUtils.isEmpty(activitySignupBinding.Username.getText().toString())) {
            activitySignupBinding.Username.setError("Enter Valid UserName");return;
        }
        else if (TextUtils.isEmpty(activitySignupBinding.Phone.getText().toString())) {
            activitySignupBinding.Phone.setError("Enter Valid PhoneNumber"); return;
        }
else {
    createAccount();

        }
    }

    private void createAccount() {
        progressDialog=new ProgressDialog(SignupActivity.this);
        progressDialog.setTitle("Just a moment....");
        progressDialog.setMessage("We are Creating Your Account");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(activitySignupBinding.Email.getText().toString(),activitySignupBinding.Password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        signinmap.put("Email",activitySignupBinding.Email.getText().toString());
                        signinmap.put("key",activitySignupBinding.Password.getText().toString());
                        signinmap.put("Phone",activitySignupBinding.Phone.getText().toString());
signinmap.put("Uid",auth.getUid());
                        activitySignupBinding.spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView textView = (TextView) view;
                                signinmap.put("Designation", ((TextView) adapterView.getChildAt(0)).getText().toString());
                                if(((TextView) adapterView.getChildAt(0)).getText().toString()=="Farmer"){
                                    signinmap.put("FName",activitySignupBinding.Name.getText().toString());
                                } else if (((TextView) adapterView.getChildAt(0)).getText().toString()=="Carrier") {
                                    signinmap.put("CName",activitySignupBinding.Name.getText().toString());
                                } else if (((TextView) adapterView.getChildAt(0)).getText().toString()=="Distributor") {
                                    signinmap.put("DName",activitySignupBinding.Name.getText().toString());
                                }
                                else {
                                    Toast.makeText(SignupActivity.this, "Choose correct one ", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
activitySignupBinding.spinnerLanguages.setPrompt("Required");
                            }
                        });

                   if (task.isSuccessful()){
                       databaseReference.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               FarmersModel picture=snapshot.getValue(FarmersModel.class);

                               Picasso.get().load(String.valueOf(picture.getPictureUri())).into(activitySignupBinding.profileImage);
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });


                       databaseReference.child(auth.getUid()).setValue(signinmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               Toast.makeText(SignupActivity.this, "New Activity", Toast.LENGTH_SHORT).show();
                               activitySignupBinding.Name.setText("");
                               activitySignupBinding.Email.setText("");
                               activitySignupBinding.Password.setText("");
                               activitySignupBinding.Username.setText("");
                               activitySignupBinding.Phone.setText("");


                           }
                       });


                   }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

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

    private void firebasewithgoogle(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}