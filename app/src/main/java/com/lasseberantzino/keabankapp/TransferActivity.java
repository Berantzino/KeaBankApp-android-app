package com.lasseberantzino.keabankapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TransferActivity extends AppCompatActivity {

    View mViewMyAccounts;
    View mViewOtherAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        init();
        setTitle(R.string.transfer_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    // Displays two options, transfer between own accounts or to others
    private void init() {

        mViewMyAccounts = findViewById(R.id.view_my_accounts);
        mViewOtherAccounts = findViewById(R.id.view_other_accounts);

        mViewMyAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferActivity.this, MyAccsTransferActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mViewOtherAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferActivity.this, OtherAccsTransferActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
