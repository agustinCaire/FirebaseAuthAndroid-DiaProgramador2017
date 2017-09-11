package com.agustin.firebaseauthandroid_diaprogramador2017;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";

    EditText edtEmail,edtPassword;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        showToolbar(getString(R.string.create_account_activity_label),true);

        edtEmail = findViewById(R.id.create_email);
        edtPassword = findViewById(R.id.create_password);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.w(TAG, "Usuario Logeado" + user.getEmail());

                    //Usuario logueado
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Usuario no logueado
                    Log.w(TAG, "Usuario NO logueado");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void showToolbar(String tittle, boolean upButton){
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    public void createAccount(View v) {
        String email    = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.w(TAG,"Cuenta creada");
                        }else {
                            Log.w(TAG,"No se pudo crear la cuenta");
                            Toast.makeText(CreateAccountActivity.this, "Ocurrió un error al crear la cuenta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
