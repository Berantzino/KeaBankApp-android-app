package com.lasseberantzino.keabankapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.TransactionModel;

import java.text.SimpleDateFormat;

public class TransactionDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TransactionDetails";

    TextView mTextViewTitle;
    TextView mTextViewDate;
    TextView mTextViewFromAccount;
    TextView mTextViewToAccount;
    TextView mTextViewAmount;
    TextView mTextViewType;
    TextView mTextViewAutoPay;
    TextView mTextViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        setTitle("Details");
        init();
    }

    private void init() {

        mTextViewTitle = findViewById(R.id.text_view_transaction_title);
        mTextViewDate = findViewById(R.id.text_view_transaction_date);
        mTextViewFromAccount = findViewById(R.id.text_view_transaction_from);
        mTextViewToAccount = findViewById(R.id.text_view_transaction_to);
        mTextViewAmount = findViewById(R.id.text_view_transaction_amount);
        mTextViewType = findViewById(R.id.text_view_transaction_type);
        mTextViewAutoPay = findViewById(R.id.text_view_transaction_auto_pay);
        mTextViewMessage = findViewById(R.id.text_view_transaction_message);

        // Grabs the data from the intent
        Bundle data = getIntent().getExtras();
        TransactionModel transactionModel = data.getParcelable("Transaction");
        String accountId = data.getString("AccountId");
        String amount = String.valueOf(transactionModel.getAmount());
        String message = transactionModel.getTransactionMessage();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // Sets the text in the view to the transactionModel's details
        mTextViewTitle.setText(transactionModel.getTransactionTitle());
        mTextViewDate.setText(sdf.format(transactionModel.getTransactionDate()));
        mTextViewFromAccount.setText(transactionModel.getTransferFromId());
        mTextViewToAccount.setText(transactionModel.getTransferToId());
        mTextViewType.setText(String.valueOf(transactionModel.getTransactionType()));
        mTextViewAutoPay.setText(String.valueOf(transactionModel.isAutoPay()));


        // Adds a message if none was provided
        if (TextUtils.isEmpty(message)) {
            message = "No message provided";
        }
        mTextViewMessage.setText(message);

        // Formats it so that all transactions from the current user's accounts are displayed
        // with a - in front of amount to display withdrawal
        if (accountId.equals(transactionModel.getTransferFromId())) {
            amount = "-" + amount;
        }
        mTextViewAmount.setText(amount);
    }
}
