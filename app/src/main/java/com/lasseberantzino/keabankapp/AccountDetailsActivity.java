package com.lasseberantzino.keabankapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lasseberantzino.keabankapp.adapter.TransactionAdapter;
import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.TransactionModel;

public class AccountDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private TransactionAdapter mAdapter;
    public AccountModel mAccountModel;

    TextView mTextViewBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        init();
    }

    // Sets the recycler view up
    private void setUpRecyclerView(String accountId) {

        // Reference to the accounts transaction collection
        final CollectionReference transactionCollectionRef = mDatabase.collection("Accounts").document(accountId)
                .collection("Transactions");
        Query query = transactionCollectionRef.orderBy("transactionDate", Query.Direction.DESCENDING);

        // Configures the options for the recycler, with a query and the model it contains
        FirestoreRecyclerOptions<TransactionModel> options =
                new FirestoreRecyclerOptions.Builder<TransactionModel>()
                .setQuery(query, TransactionModel.class)
                .build();

        mAdapter = new TransactionAdapter(options, mAccountModel);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        // When an item in the recyclerView is clicked this is executed
        // Sends the clicked transactionModel and the accound id to the transaction details activity
        mAdapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                TransactionModel transactionModel = documentSnapshot.toObject(TransactionModel.class);
                Intent intent = new Intent(AccountDetailsActivity.this,
                        TransactionDetailsActivity.class);
                intent.putExtra("Transaction", transactionModel);
                intent.putExtra("AccountId", mAccountModel.getAccountId());
                startActivity(intent);
            }
        });
    }

    private void init() {

        mTextViewBalance = findViewById(R.id.text_view_balance);

        // Grabs the intent sent from OverviewActivity
        Bundle data = getIntent().getExtras();

        if (data != null) {
            mAccountModel = data.getParcelable("Account");
        }
        if (mAccountModel != null) {
            setTitle(mAccountModel.getAccountName());
            mTextViewBalance.setText(String.valueOf(mAccountModel.getAccountBalance()));
            setUpRecyclerView(mAccountModel.getAccountId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
