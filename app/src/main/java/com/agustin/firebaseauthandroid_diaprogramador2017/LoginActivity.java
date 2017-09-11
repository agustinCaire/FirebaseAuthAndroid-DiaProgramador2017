package com.agustin.firebaseauthandroid_diaprogramador2017;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";

    EditText edtEmail,edtPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    CallbackManager callbackManager;

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        showToolbar(getString(R.string.login_activity_label),false);

        //Obtener instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.w(TAG, "Usuario Logeado" + user.getEmail());

                    //Usuario logueado
                    goHome();
                } else {
                    // Usuario no logueado
                    Log.w(TAG, "Usuario NO logueado");
                }
            }
        };

        //GOOGLE AUTHENTICACTION
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.login_google_button).setOnClickListener(this);



        //Facebook Authentication
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_facebook_button);
        loginButton.setReadPermissions("email", "public_profile");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.w(TAG, "facebook login activity response fine");
                        firebaseLoginWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.w(TAG, "facebook login activity canceled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.w(TAG, "facebook login activity failed");
                    }
                });

        edtEmail = findViewById(R.id.login_email);
        edtPassword = findViewById(R.id.login_password);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG,"Fallo la conexion");
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(mAuth.getCurrentUser()!=null){
            goHome();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_google_button:
                this.loginWithGoogle();
                break;
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleLoginWithGoogleResult(result);
        }
    }

    public void showToolbar(String tittle, boolean upButton){
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    private void goHome(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /***********************************************************/
    /***********************GOOGLE LOGIN************************/
    /***********************************************************/

    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleLoginWithGoogleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //Se logeo correctamente
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseLoginWithGoogle(account);

        } else {
            // No se pudo loguear
            Log.w(TAG, "Ocurrio un error al loguearse por google");
            Toast.makeText(this, getString(R.string.access_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseLoginWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.w(TAG, "Login por google correcto");
                        }
                        else{
                            Log.w(TAG, "Ocurrio un error al loguearse por google");
                            Toast.makeText(getApplicationContext(),getString(R.string.access_failed),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /***********************************************************/
    /**********************FACEBOOK LOGIN***********************/
    /***********************************************************/
    private void firebaseLoginWithFacebook(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.w(TAG, "Login por facebook correcto");
                        }
                        else{
                            Log.w(TAG, "Ocurrio un error al loguearse por facebook");
                            Toast.makeText(getApplicationContext(),getString(R.string.access_failed),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /***********************************************************/
    /***********************EMAIL LOGIN*************************/
    /***********************************************************/
    public void loginWithEmail(View v){
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.w(TAG, "Login con email correcto");
                }
                else{
                    Log.w(TAG, "Login con email fallo");
                    Toast.makeText(getApplicationContext(),getString(R.string.access_failed),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /***********************************************************/
    /***********************************************************/

    public void goCreateAccount(View v){
        Intent intent = new Intent(LoginActivity.this,CreateAccountActivity.class);
        startActivity(intent);
    }
}
