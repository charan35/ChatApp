package com.example.altachatapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.altachatapp.MainActivityChat;
import com.example.altachatapp.R;
import com.example.altachatapp.data.SharedPreferenceHelper;
import com.example.altachatapp.data.StaticConfig;
import com.example.altachatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewEmployee extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    private Button save,resend,btnverifyotp;
    TextView login;
    EditText username,fullname,phone,verifyotp;
    TextView dob;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private LovelyProgressDialog waitingDialog;

    DatePickerDialog datePickerDialog;

    private static String TAG = "NewRegister";
    public static String STR_EXTRA_ACTION_REGISTER = "register";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private AuthUtils authUtils;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationid;
    private PhoneAuthCredential phoneCredential;

    RequestQueue requestQueue;
    RelativeLayout relativeLayout6;

    FirebaseUser user = null;
    private boolean firstTimeAccess;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_employee);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firstTimeAccess = true;
        initFirebase();

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        phone = findViewById(R.id.phone);
        verifyotp = findViewById(R.id.verifyotp);
       // tac = findViewById(R.id.tac);
        relativeLayout6=findViewById(R.id.relativeLayout6);
        btnverifyotp=findViewById(R.id.btnverifyotp);

        dob = findViewById(R.id.dob);

        resend=findViewById(R.id.resend);

        save = findViewById(R.id.register);
        //login = findViewById(R.id.login);

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(NewEmployee.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(username.getText().toString())&&TextUtils.isEmpty(fullname.getText().toString())&&TextUtils.isEmpty(dob.getText().toString())){

                    Toast.makeText(NewEmployee.this, "please enter all fields", Toast.LENGTH_SHORT).show();

                }
                else {

                    if (phone.getText().toString().startsWith("+91")) {
                        startPhoneNumberVerification();
                    } else {
                        Toast.makeText(NewEmployee.this, "please add +91 before the mobile number", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        /*save.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    startPhoneNumberVerification();

                    return true;
                }
                return false;
            }
        });*/
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                    if (phone.getText().toString().startsWith("+91")){

                    }
                    else{
                        Toast.makeText(NewEmployee.this, "please add +91 before the mobile number", Toast.LENGTH_SHORT).show();
                    }

                // TODO Auto-generated method stub
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendSMSCodeCallBack();
            }
        });

        btnverifyotp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSMSCodeVerication();
                    return true;
                }
                return false;
            }
        });
        btnverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    attemptSMSCodeVerication();

            }
        });

    }
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        authUtils = new AuthUtils();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(NewEmployee.this, MainActivityChat.class));
                        NewEmployee.this.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
    private void attemptSMSCodeVerication() {
        verifyotp.setError(null);
        String smscode = verifyotp.getText().toString();


            verifyPhoneNumberWithCode(verificationid, smscode);

    }
    private void resendSMSCodeCallBack(){
        String phoneNumber = phone.getText().toString();

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                120,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                mResendToken);
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        authUtils.signInWithPhoneAuthCredential(credential);
    }
    private void startPhoneNumberVerification() {
        //this will send sms code to users phone
        String phoneNumber = phone.getText().toString();
        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private void setUpVerificationCallbacks(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                System.out.println("phoneAuth: verified ");
                authUtils.signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                save.setVisibility(View.VISIBLE);
                btnverifyotp.setVisibility(View.GONE);
                relativeLayout6.setVisibility(View.GONE);
                Toast.makeText(NewEmployee.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //System.out.println("phoneAuth: code sent ");

                mResendToken = forceResendingToken;
                verificationid = s;
                save.setVisibility(View.GONE);
                btnverifyotp.setVisibility(View.VISIBLE);
                relativeLayout6.setVisibility(View.VISIBLE);
            }
        };
    }




    class AuthUtils {

        void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
            waitingDialog.setIcon(R.drawable.ic_add_friend)
                    .setTitle("Registering....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();

            mAuth.signInWithCredential(credential).addOnCompleteListener(NewEmployee.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithPhone:onComplete:" + task.isSuccessful());
                    waitingDialog.dismiss();
                    if (!task.isSuccessful()) {
                        new LovelyInfoDialog(NewEmployee.this) {
                            @Override
                            public LovelyInfoDialog setConfirmButtonText(String text) {
                                findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dismiss();
                                    }
                                });
                                return super.setConfirmButtonText(text);
                            }
                        }
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Register false")
                                .setMessage("Email exist or weak password!")
                                .setConfirmButtonText("ok")
                                .setCancelable(false)
                                .show();
                    }
                    else{
                        if(mAuth.getCurrentUser()!=null) {
                                saveUserInfo();
                                initNewUserInfo();
                        }
                        Toast.makeText(NewEmployee.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewEmployee.this, MainActivityChat.class));
                        NewEmployee.this.finish();
                        System.out.println("phoneAuth: login success ");

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    waitingDialog.dismiss();

                }
            });

        }

        void saveUserInfo() {
            FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waitingDialog.dismiss();
                    HashMap hashUser = (HashMap) dataSnapshot.getValue();
                    User userInfo = new User();
                    userInfo.name = (String) hashUser.get("name");
                    userInfo.email = (String) hashUser.get("email");
                    userInfo.phone=(String)hashUser.get("phone");
                    userInfo.avata = (String) hashUser.get("avata");
                    userInfo.dateofbirth=(String)hashUser.get("dob");
                    userInfo.ID=(String)hashUser.get("ID");
                    SharedPreferenceHelper.getInstance(NewEmployee.this).saveUserInfo(userInfo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        void initNewUserInfo() {
            User newUser = new User();
            newUser.email = username.getText().toString();
            newUser.phone=user.getPhoneNumber();
            newUser.name = fullname.getText().toString();
            newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
            newUser.dateofbirth=dob.getText().toString();
            newUser.ID=user.getUid();
            FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
        }
    }


}
