package com.lasseberantzino.keabankapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lasseberantzino.keabankapp.model.UserModel;

import javax.annotation.Nullable;

public class UserDetailsActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailsActivity";

    TextView mTextViewFirstName;
    TextView mTextViewLastName;
    TextView mTextViewAge;
    TextView mTextViewEmail;
    TextView mTextViewBankAffiliate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init();
        setTitle(R.string.user_details_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestoy() called");
    }

    private void init() {

        mTextViewFirstName = findViewById(R.id.text_view_first_name);
        mTextViewLastName = findViewById(R.id.text_view_last_name);
        mTextViewAge = findViewById(R.id.text_view_age);
        mTextViewEmail = findViewById(R.id.text_view_email);
        mTextViewBankAffiliate = findViewById(R.id.text_view_bank_affiliate);

        displayUserDetails();
    }

    private void displayUserDetails() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userCollectionRef = database.collection("Users");
        String userId = auth.getCurrentUser().getUid();

        Query query = userCollectionRef.whereEqualTo("uid", userId);

        // gets the current user's data and displays it
        query.addSnapshotListener(UserDetailsActivity.this ,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "onEvent: ERROR " + e.getLocalizedMessage());
                    return;
                }

                if (queryDocumentSnapshots != null) {

                    UserModel userModel = queryDocumentSnapshots
                            .getDocuments().get(0).toObject(UserModel.class);
                    mTextViewFirstName.setText(userModel.getFirstName());
                    mTextViewLastName.setText(userModel.getLastName());
                    mTextViewEmail.setText(userModel.getEmail());
                    mTextViewBankAffiliate.setText(userModel.getBankAffiliate());
                    mTextViewAge.setText(String.valueOf(userModel.getAge()));
                }
            }
        });
    }
}
