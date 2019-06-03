package com.lasseberantzino.keabankapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lasseberantzino.keabankapp.service.UtilityService;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private UtilityService mUtilityService = new UtilityService();

    FirebaseAuth mAuth;

    EditText mEmailView;
    EditText mPasswordView;
    Button mSignInBtn;
    Button mSignUpBtn;
    Button mForgotPasswordBtn;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        // Values for testing
        mEmailView.setText("test@account.com");
        mPasswordView.setText("123456");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSignInBtn.setEnabled(true);
        mSignUpBtn.setEnabled(true);
        mForgotPasswordBtn.setEnabled(true);

        // Check if user is already signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(SignInActivity.this, OverviewActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // If an AlertDialog is showing, dismiss it
        if (mAlertDialog != null) {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    }

    private void init() {

        mEmailView = findViewById(R.id.sign_in_email);
        mPasswordView = findViewById(R.id.sign_in_password);
        mSignInBtn = findViewById(R.id.sign_in_btn);
        mSignUpBtn = findViewById(R.id.sign_up_btn);
        mForgotPasswordBtn = findViewById(R.id.forgot_password_btn);

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        mForgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mEmailView.requestFocus();
    }

    private void signIn() {

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Validate that the user has typed an email/password
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            mPasswordView.requestFocus();
            return;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.signing_in_toast_message),
                    Toast.LENGTH_SHORT).show();
        }

        mSignInBtn.setEnabled(false);
        // Uses FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(TAG, "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d(TAG, "Problem signing in: " + task.getException());
                    mAlertDialog = mUtilityService.getErrorDialog(SignInActivity.this,
                            task.getException().getMessage());
                    mAlertDialog.show();
                    mSignInBtn.setEnabled(true);

                } else {
                    mSignInBtn.setEnabled(true);
                    Intent intent = new Intent(SignInActivity.this, OverviewActivity.class);
                    startActivity(intent);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                        mSignInBtn.setEnabled(true);
                    }
                });
    }

    private void signUp() {

        mSignUpBtn.setEnabled(false);
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    // Called if the users has forgotten his password and wants to reset it
    private void resetPassword() {

        mForgotPasswordBtn.setEnabled(false);
        final String email = mEmailView.getText().toString();

        if (!TextUtils.isEmpty(email)) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_reset_password_title))
                    .setMessage(getString(R.string.alert_reset_password_message) + " " + email)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (!task.isSuccessful()) {
                                                mAlertDialog = mUtilityService
                                                        .getErrorDialog(SignInActivity.this,
                                                                task.getException().getMessage());
                                            } else {
                                                Toast.makeText(SignInActivity.this,
                                                        getString(R.string.password_reset_email_sent_toast_message),
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            mForgotPasswordBtn.setEnabled(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, e.getMessage());
                                            mForgotPasswordBtn.setEnabled(true);
                                        }
                                    });
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mForgotPasswordBtn.setEnabled(true);
                        }
                    })
                    .create();
            mAlertDialog.show();
        } else {
            mEmailView.setError(getString(R.string.error_enter_reset_email_here));
            mEmailView.requestFocus();
            mForgotPasswordBtn.setEnabled(true);
        }
    }
}
