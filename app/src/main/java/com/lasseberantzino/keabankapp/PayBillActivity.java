package com.lasseberantzino.keabankapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lasseberantzino.keabankapp.database.TransactionHandler;
import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.AccountType;
import com.lasseberantzino.keabankapp.model.TransactionModel;
import com.lasseberantzino.keabankapp.model.TransactionType;
import com.lasseberantzino.keabankapp.model.UserModel;
import com.lasseberantzino.keabankapp.service.UtilityService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

public class PayBillActivity extends AppCompatActivity {

    private static final String TAG = "PayBillActivity";
    private UtilityService mUtilityService = new UtilityService();
    private TransactionHandler mTransactionHandler = new TransactionHandler();

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    private AccountModel mFromAccount;
    private UserModel mUser;
    // SparseIntArray = better performance than HashMap
    private SparseIntArray mNemIdCodes;

    EditText mEditTextAmount;
    EditText mEditTextMessage;
    Spinner mSpinnerRecipient;
    Spinner mSpinnerFrom;
    CheckBox mCheckBoxAutoPay;
    Button mPayBillBtn;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);

        init();
        setTitle(R.string.pay_bill_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        setUpSpinner(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserModel();
        mNemIdCodes = mUtilityService.populateNemId();
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

        mEditTextAmount = findViewById(R.id.edit_text_amount);
        mEditTextMessage = findViewById(R.id.edit_text_message);
        mSpinnerRecipient = findViewById(R.id.spinner_recipient);
        mSpinnerFrom = findViewById(R.id.spinner_from);
        mCheckBoxAutoPay = findViewById(R.id.check_box_auto_pay);
        mPayBillBtn = findViewById(R.id.pay_bill_btn);

        mPayBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPayment();
            }
        });
    }

    private void setUpSpinner(final Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        CollectionReference accountsCollectionRef = mDatabase.collection("Accounts");
        String uId = mAuth.getCurrentUser().getUid();

        // List of the users accountModels
        final List<AccountModel> accountModels = new ArrayList<>();
        // List of custom account strings used for display
        final List<String> accounts = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, accounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerFrom.setAdapter(adapter);

        // Gets all the current user's accounts
        accountsCollectionRef.orderBy("accountName").whereEqualTo("ownerId", uId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    AccountModel accountModel = snapshot.toObject(AccountModel.class);
                                    accounts.add(accountModel.getAccountName() + " - " +
                                            accountModel.getAccountId());
                                    accountModels.add(accountModel);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                        // Restores itemSelected
                        if (savedInstanceState != null) {

                            mSpinnerFrom.setSelection(savedInstanceState
                                    .getInt("spinnerFrom", 0));
                        }
                    }
                });

        mSpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mFromAccount = accountModels.get(position);
                Log.d(TAG, mFromAccount.getAccountId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void attemptPayment() {

        String amount = mEditTextAmount.getText().toString();

        mEditTextAmount.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(amount)) {
            mEditTextAmount.setError(getString(R.string.error_transfer_amount_required));
            cancel = true;
            focusView = mEditTextAmount;
        }
        else if (Double.parseDouble(amount) > mFromAccount.getAccountBalance()) {
            mEditTextAmount.setError(getString(R.string.error_insufficient_funds));
            cancel = true;
            focusView = mEditTextAmount;
        }
        else if (mFromAccount == null) {
            mAlertDialog = mUtilityService.getErrorDialog(this,
                    getString(R.string.error_busy_retrieving_account_data));
            mAlertDialog.show();
            cancel = true;
        }
        else if (mFromAccount.getAccountType() == AccountType.PENSION_ACCOUNT) {
            if (mUser.getAge() < 77) {
                mAlertDialog = mUtilityService.getErrorDialog(this,
                        getString(R.string.error_age_lower_than_77));
                mAlertDialog.show();
                cancel = true;
            }
        }

        if (cancel) {
            if (!(focusView == null)) {
                focusView.requestFocus();
            }
        } else {
            String title = mSpinnerRecipient.getSelectedItem().toString();
            String message = mEditTextMessage.getText().toString();
            String transferToId = title;
            String transferFromId = mFromAccount.getAccountId();
            Date date = new Date();
            TransactionType transactionType = TransactionType.BILL;
            boolean autoPay = mCheckBoxAutoPay.isChecked();

            if (TextUtils.isEmpty(title)) {
                title = "Transfer";
            }

            TransactionModel transactionModel = new TransactionModel(title, message, transferToId,
                    transferFromId, Double.parseDouble(amount), date, transactionType, autoPay);

            verifyWithNemId(transactionModel);
        }
    }

    private void getUserModel() {

        CollectionReference usersCollectionRef = mDatabase.collection("Users");
        String userId = mAuth.getCurrentUser().getUid();

        Query query = usersCollectionRef.whereEqualTo("uid", userId);

        query.addSnapshotListener(PayBillActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "onEvent: ERROR " + e.getLocalizedMessage());
                    return;
                }

                if (queryDocumentSnapshots != null) {

                    mUser = queryDocumentSnapshots
                            .getDocuments().get(0).toObject(UserModel.class);
                }

            }
        });
    }

    // Called in order to make the payment
    private void verifyWithNemId(final TransactionModel transactionModel) {

        Random random = new Random();
        int nemIdCodesSize = mNemIdCodes.size();
        int randomNumber = random.nextInt(nemIdCodesSize);
        int randomKey = mNemIdCodes.keyAt(randomNumber);
        final int valueForRandomKey = mNemIdCodes.get(randomKey);

        final EditText inputText = new EditText(this);
        inputText.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputText.setHint(getString(R.string.nem_id_edit_text_hint));

        // Builds an AlertDialog with an editText where the user can type
        // the value that is also displayed
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("NemId Verification")
                .setTitle(getString(R.string.nem_id_verification_title))
                .setMessage(getString(R.string.nem_id_message_part_1) + " " + randomKey + "\n" +
                        getString(R.string.nem_id_message_part_2) + " " + valueForRandomKey)
                .setView(inputText)
                .setPositiveButton(getString(R.string.nem_id_positive_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String inputValue = inputText.getText().toString();
                        if (inputValue.equals(String.valueOf(valueForRandomKey))) {
                            Log.d(TAG, "Nem Id Alert onClick: Correct value");
                            mTransactionHandler.makePayment(getApplicationContext(), TAG,
                                    mFromAccount, transactionModel);
                            finish();
                        } else {
                            Log.d(TAG, "Nem Id Alert onClick: Wrong value");
                            Toast.makeText(PayBillActivity.this,
                                    getString(R.string.error_wrong_nem_id_code),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.nem_id_negative_btn), null)
                .create();
        mAlertDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinnerFrom", mSpinnerFrom.getSelectedItemPosition());
    }
}
