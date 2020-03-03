package com.example.altachatapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.ui.NewEmployee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

public class Welcome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    //private Authils1 authUtils1;

    FirebaseUser user = null;
    private boolean firstTimeAccess;
    private static String TAG = "NewRegister";


    private LovelyProgressDialog waitingDialog;


    TextView termsandcondition;
    Button agree;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);

        setContentView(R.layout.welcome_activity);
        mAuth = FirebaseAuth.getInstance();
        firstTimeAccess = true;
        initFirebase();

        termsandcondition=findViewById(R.id.terms);
        agree=findViewById(R.id.agree);

        termsandcondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Welcome.this,Terms_And_Conditions.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(Welcome.this,R.anim.fade_in,R.anim.fade_out);
                startActivity(i,options.toBundle());
            }
        });
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Welcome.this, NewEmployee.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(Welcome.this,R.anim.fade_in,R.anim.fade_out);
                startActivity(i,options.toBundle());
                finish();
            }
        });

    }
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
       // authUtils = new NewEmployee.AuthUtils();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        Intent intent = new Intent(Welcome.this,MainActivityChat.class);
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(Welcome.this,R.anim.fade_in,R.anim.fade_out);
                        startActivity(intent,options.toBundle());
                        Welcome.this.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }
}
