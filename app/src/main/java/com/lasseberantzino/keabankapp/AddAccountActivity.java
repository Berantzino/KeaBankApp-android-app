package com.lasseberantzino.keabankapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.AccountType;

public class AddAccountActivity extends AppCompatActivity {

    private static final String TAG = "AddAccountActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    private AccountType mAccountType;

    EditText mAccountNameView;
    Spinner mAccountTypeSpinner;
    Button mAddAccountBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        init();
        setTitle(R.string.title_add_new_account);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();

        mAccountNameView = findViewById(R.id.edit_text_account_name);
        mAccountTypeSpinner = findViewById(R.id.account_type_spinner);
        mAddAccountBtn = findViewById(R.id.add_account_btn);

        // Adapter for the spinner
        ArrayAdapter<AccountType> dataAdapter = new ArrayAdapter<AccountType>(this,
                android.R.layout.simple_spinner_item, AccountType.values());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Adds the adapter to the spinner
        mAccountTypeSpinner.setAdapter(dataAdapter);

        // Whenever an item in the spinner is selected
        mAccountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position));
                mAccountType = (AccountType) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAddAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddNewAccount();
            }
        });

    }

    private void attemptAddNewAccount() {

        // Reset errors displayed in the form.
        mAccountNameView.setError(null);

        String accountName = mAccountNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(accountName)) {
            mAccountNameView.setError(getString(R.string.error_field_required));
            focusView = mAccountNameView;
            cancel = true;
        }

        if (accountName.length() > 25) {
            mAccountNameView.setError(getString(R.string.error_max_chars_25));
            focusView = mAccountNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            createNewAccount();
        }

    }

    private void createNewAccount() {

        String accountName = mAccountNameView.getText().toString();
        String ownerId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseFirestore.getInstance();
        final CollectionReference accountCollectionRef = mDatabase.collection("Accounts");

        if (mAccountType == null) {
            Toast.makeText(this, getString(R.string.error_getting_account_type),
                    Toast.LENGTH_LONG).show();
        } else {
            AccountModel accountModel = new AccountModel(accountName, ownerId, mAccountType);

            // Adds the created accountModel to the database
            accountCollectionRef.add(accountModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {

                    String accountId = task.getResult().getId();

                    // Grabs the id of the created account
                    // and updates the account to give it it's id
                    DocumentReference documentReference = accountCollectionRef.document(accountId);
                    documentReference.update("accountId", accountId);
                    finish();
                }
            });
        }
    }
}
