package com.lasseberantzino.keabankapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.AccountType;
import com.lasseberantzino.keabankapp.model.UserModel;
import com.lasseberantzino.keabankapp.service.UtilityService;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    // Request code for Location
    private static final int LOCATION_REQUEST_CODE = 101;
    // minimum time interval between location updates, in milliseconds
    private static final long LOCATION_REFRESH_TIME = 1000;
    // minimum distance between location updates, in meters
    private static final float LOCATION_REFRESH_DISTANCE = 100.00f;
    private final UtilityService mUtilityService = new UtilityService();

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();;

    private Location mLocation;

    EditText mFirstNameView;
    EditText mLastNameView;
    EditText mAgeView;
    EditText mEmailView;
    EditText mPasswordView;
    EditText mConfirmPasswordView;
    Button mSignUpBtn;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        getUserLocation();
        setTitle(R.string.sign_up_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if an AlertDialog is showing, dismiss it to prevent a crash
        if (mAlertDialog != null) {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();

        mFirstNameView = findViewById(R.id.sign_up_first_name);
        mLastNameView = findViewById(R.id.sign_up_last_name);
        mAgeView = findViewById(R.id.sign_up_age);
        mEmailView = findViewById(R.id.sign_up_email);
        mPasswordView = findViewById(R.id.sign_up_password);
        mConfirmPasswordView = findViewById(R.id.sign_up_confirm_password);
        mSignUpBtn = findViewById(R.id.sign_up_btn);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptRegistration();
            }
        });

        mFirstNameView.requestFocus();
    }

    // Executed when Sign Up button is pressed.
    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mAgeView.setError(null);

        // Store values at the time of the sign up attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String age = mAgeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user has entered one, using TextUtils.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for valid age
        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        } else if (Integer.parseInt(age) <= 0 || Integer.parseInt(age) > 120) {
            mAgeView.setError(getString(R.string.error_invalid_age));
            focusView = mAgeView;
            cancel = true;
        }

        // Check for valid last name
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for valid first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // If everything is valid, continue to create the new user
            createFirebaseUser();
        }
    }

    // Quick check since we dont want to make a call to Firebase if the email doesn't contain @
    private boolean isEmailValid(String email) {
        // Add more checking logic here if needed

        return email.contains("@");
    }

    // Checks if the 2 passwords are the same and has 6 or more characters
    private boolean isPasswordValid(String password) {

        String confirmPassword = mConfirmPasswordView.getText().toString();

        return confirmPassword.equals(password) && password.length() >= 6;
    }

    // Creates the user in Firebase database
    private void createFirebaseUser() {

        mSignUpBtn.setEnabled(false);
        final String firstName = mFirstNameView.getText().toString();
        final String lastName = mLastNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        final int age = Integer.parseInt(mAgeView.getText().toString());

        // Check if we have the user's location
        if (mLocation != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUser onComplete: " + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Log.d(TAG, "user creation failed: " + task.getException().getMessage());
                                mAlertDialog = mUtilityService.getErrorDialog(SignUpActivity.this,
                                        task.getException().getMessage());
                                mAlertDialog.show();
                                mSignUpBtn.setEnabled(true);
                            } else {
                                String userId = task.getResult().getUser().getUid();

                                // Adds the created user to the "Users" collection in Firestore
                                uploadUserToFirestore(firstName, lastName, email, userId,
                                        whichBankIsClosest(mLocation), age);

                                // Gives the user the 2 required accounts
                                provideUserWithRequiredAccounts(userId);
                                setDisplayName(firstName, lastName);
                                Toast.makeText(SignUpActivity.this,
                                        getString(R.string.sign_up_success_toast_message),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.getMessage());
                            mSignUpBtn.setEnabled(true);
                        }
                    });
        } else {
            mAlertDialog = mUtilityService.getErrorDialog(this,
                    getString(R.string.error_current_location_needed));
            mAlertDialog.show();
            mSignUpBtn.setEnabled(true);
        }
    }

    // Adds the newly created user to the "Users" collection in Firestore for later use
    private void uploadUserToFirestore(String firstName, String lastName, String email,
                                       String uid, String bankAffiliate, int age) {

        CollectionReference userCollectionRef = mDatabase.collection("Users");

        UserModel userModel = new UserModel(firstName, lastName, email, uid, bankAffiliate, age);

        userCollectionRef.add(userModel);
    }

    // Adds the users first and last name to it's display name
    private void setDisplayName(String firstName, String lastName) {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName + " " + lastName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "setDisplayName: User profile updated");
                            }
                        }
                    });
        }
    }

    // Checks which bank affiliate is closest to the user's location
    // and returns the corresponding bank name
    private String whichBankIsClosest(Location currentLocation) {

        Location odense = new Location("");
        odense.setLatitude(55.396229);
        odense.setLongitude(10.390600);

        Location copenhagen = new Location("");
        copenhagen.setLatitude(55.676098);
        copenhagen.setLongitude(12.568337);

        float distanceToOdense = currentLocation.distanceTo(odense);
        float distanceToCopenhagen = currentLocation.distanceTo(copenhagen);

        Log.d(TAG, "Distance to Copenhagen: " + distanceToCopenhagen);
        Log.d(TAG, "Distance to Odense: " + distanceToOdense);

        if (distanceToCopenhagen <= distanceToOdense) {
            return "Copenhagen";
        } else {
            return "Odense";
        }
    }

    // Retrieves the user's location and sets mLocation to it
    private void getUserLocation() {

        if (isOnline()) {

            final LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    Log.d(TAG, "Lat: " + mLocation.getLatitude() + " Lon: " + mLocation.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            int permission = ContextCompat.checkSelfPermission(SignUpActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED) {
                // Grabs the user's location
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, locationListener);
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_REQUEST_CODE);
            }

        } else {
            mAlertDialog = new AlertDialog.Builder(SignUpActivity.this)
                    .setTitle(getString(R.string.alert_no_network_title))
                    .setMessage(getString(R.string.alert_no_network_message))
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            mAlertDialog.show();
        }
    }

    // Requests permission to use the user's Location
    protected void requestPermission(String permissionType, int requestCode) {

        ActivityCompat.requestPermissions(this,
                new String[]{permissionType}, requestCode);
    }

    // Callback method, which is executed after requestPermission
    // Displays a toast if the required permission is not granted
    // Otherwise it gets the user's current location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length == 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        getString(R.string.error_permission_required_toast_message),
                        Toast.LENGTH_LONG).show();

            } else {
                getUserLocation();
            }
        }
    }

    // Checks if the user is online
    private boolean isOnline() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    // Creates the two required accounts for the user (Default and Budget)
    private void provideUserWithRequiredAccounts(String userId) {

        final CollectionReference accountCollectionRef = mDatabase.collection("Accounts");

        AccountModel defaultAccount = new AccountModel("Default Account", userId,
                AccountType.DEFAULT_ACCOUNT);
        AccountModel budgetAccount = new AccountModel("Budget Account", userId,
                AccountType.BUDGET_ACCOUNT);

        // Adds the account to the Accounts Collection
        // When it has been added, it grabs the id from the document that's returned
        // and adds it to the account document
        accountCollectionRef.add(defaultAccount).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                String accountId = task.getResult().getId();
                Log.d(TAG, "DefaultAccId: " + accountId);

                DocumentReference documentReference = accountCollectionRef.document(accountId);
                documentReference.update("accountId", accountId);
            }
        });

        accountCollectionRef.add(budgetAccount).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                String accountId = task.getResult().getId();
                Log.d(TAG, "BudgetAccId: " + accountId);

                DocumentReference documentReference = accountCollectionRef.document(accountId);
                documentReference.update("accountId", accountId);
            }
        });
    }
}
