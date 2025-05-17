package com.localiserarbres;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {


    private FirebaseAuth auth = null;
    private EditText signupName,signupEmail,signupPassword,signupCoPassword;
    private Button signupBtn;
    private TextView loginTxt;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        auth =FirebaseAuth.getInstance();
        signupName = findViewById(R.id.name);
        signupEmail = findViewById(R.id.emailsignup);
        signupPassword = findViewById(R.id.passwordsignup);
        signupCoPassword = findViewById(R.id.copasswordsignup);
        signupBtn = findViewById(R.id.signupBtn);
        loginTxt = findViewById(R.id.logintxt);



        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String coPassword = signupCoPassword.getText().toString().trim();

                if(name.isEmpty()){
                    signupName.setError("Name Can Not Be Empty ");
                }if(email.isEmpty()){
                    signupEmail.setError("Email Can Not Be Empty ");
                }if(password.isEmpty()){
                    signupPassword.setError("Password Can Not Be Empty ");
                }if(password.isEmpty()){
                    signupCoPassword.setError("Confirm Password Can Not Be Empty ");
                }if(!coPassword.equals(password)){
                    signupCoPassword.setError("Confirmation Password is wrong");
                }else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"Sign Up Successful ",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            }else{
                                Toast.makeText(SignUpActivity.this,"Sign Up Failed "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });



    }
}