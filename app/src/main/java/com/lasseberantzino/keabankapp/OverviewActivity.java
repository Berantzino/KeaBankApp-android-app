package com.lasseberantzino.keabankapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lasseberantzino.keabankapp.adapter.AccountsOverviewAdapter;
import com.lasseberantzino.keabankapp.model.AccountModel;

public class OverviewActivity extends AppCompatActivity {

    private static final String TAG = "OverviewActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mAccountsCollectionRef = mDatabase.collection("Accounts");

    private AccountsOverviewAdapter mAdapter;

    BottomNavigationView mBottomNavigationView;

    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        init();
        setUpRecyclerView();
        setTitle(R.string.title_overview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBottomNavigationView.setSelectedItemId(R.id.action_accounts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAlertDialog != null) {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    }

    // Tells the system to use our custom menu as the menu for this Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.accounts_top_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Whenever an item in the top nav bar is clicked, this is executed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add_account:
                Intent intent = new Intent(OverviewActivity.this, AddAccountActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_user:
                Intent myIntent = new Intent(OverviewActivity.this, UserDetailsActivity.class);
                startActivity(myIntent);
                return true;

            case R.id.action_sign_out:
                mAlertDialog = new AlertDialog.Builder(OverviewActivity.this)
                        .setTitle(getString(R.string.alert_sign_out_title))
                        .setMessage(getString(R.string.alert_sign_out_message))
                        .setPositiveButton(getString(R.string.alert_sign_out_positive_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();

                                Intent intent = new Intent(OverviewActivity.this, SignInActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create();
                mAlertDialog.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();

        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        // Whenever an item in the bottomNavigation is selected
        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Intent intent = null;
                switch (menuItem.getItemId()) {

                    case R.id.action_accounts:
                        break;

                    case R.id.action_pay:

                        intent = new Intent(OverviewActivity.this, PayBillActivity.class);
                        break;

                    case R.id.action_transfer:

                        intent = new Intent(OverviewActivity.this, TransferActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    private void setUpRecyclerView() {

        // Gets the accounts from the Accounts Collection
        // which has the ownerId equal to the current users id
        // and orders them by account name
        Query query = mAccountsCollectionRef.orderBy("accountName")
                .whereEqualTo("ownerId", mAuth.getCurrentUser().getUid());

        // FirestoreRecyclerOptions injects the query into the Adapter
        FirestoreRecyclerOptions<AccountModel> options =
                new FirestoreRecyclerOptions.Builder<AccountModel>()
                .setQuery(query, AccountModel.class)
                .build();

        mAdapter = new AccountsOverviewAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        // Sets the onItemClickListener that was impelemted in AccountsOverviewAdapter
        // and adds it to the mAdapter
        mAdapter.setOnItemClickListener(new AccountsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                AccountModel accountModel = documentSnapshot.toObject(AccountModel.class);

                String id = documentSnapshot.getId();
                Log.d(TAG, "Position: " + position + " ID: " + id);

                Intent intent = new Intent(OverviewActivity.this, AccountDetailsActivity.class);
                intent.putExtra("Account", accountModel);
                startActivity(intent);
            }
        });
    }

    // Starts listening and updating when the Activity is in the foreground
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    // Stops updating the RecyclerView if the Activity goes into the background
    // in order to not waste resources
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
