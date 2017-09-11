package com.agustin.firebaseauthandroid_diaprogramador2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.applinks.FacebookAppLinkResolver;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView email,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showToolbar(getString(R.string.main_activity_label),false);

        email = this.findViewById(R.id.userEmail);
        name = this.findViewById(R.id.userName);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){

            if(user.getDisplayName()!=null)
                name.setText(user.getDisplayName());
            else

                name.setText("...");

            email.setText(user.getEmail());
        }
        else{
            goLogin();
        }
    }

    public void showToolbar(String tittle, boolean upButton){
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    private void goLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void logOut(View v){
        LoginManager.getInstance().logOut();
        mAuth.signOut();

        goLogin();
    }
}
