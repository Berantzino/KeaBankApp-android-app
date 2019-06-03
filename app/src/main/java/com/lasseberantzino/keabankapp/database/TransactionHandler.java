package com.lasseberantzino.keabankapp.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.lasseberantzino.keabankapp.R;
import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.TransactionModel;


public class TransactionHandler {

    public TransactionHandler() {
    }

    // Handles a transfer
    public void makeTransfer(final Context context, final String TAG, final AccountModel fromAccount,
                             final AccountModel toAccount, final TransactionModel transactionModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference accountsCollectionRef = db.collection("Accounts");
        DocumentReference fromAccountRef = accountsCollectionRef.document(fromAccount.getAccountId());
        DocumentReference toAccountRef = accountsCollectionRef.document(toAccount.getAccountId());

        Log.d(TAG, "makeTransfer(): ");

        // Creates a batch to update multiples fields in 1 call
        // Add and subtracts the transaction amount to/from the corresponding accounts
        WriteBatch batch = db.batch();
        batch.update(fromAccountRef, "accountBalance",
                fromAccount.getAccountBalance() - transactionModel.getAmount());
        batch.update(toAccountRef, "accountBalance",
                toAccount.getAccountBalance() + transactionModel.getAmount());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "Transfer completed");
                    // Uploads transaction details when the transfer has occured
                    uploadTransaction(context, TAG, fromAccount, toAccount, transactionModel);
                } else {
                    Log.d(TAG, task.getException().getMessage());
                }
            }
        });
    }

    // Handles a payment
    public void makePayment(final Context context, final String TAG, final AccountModel fromAccount,
                            final TransactionModel transactionModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference accountsCollectionRef = db.collection("Accounts");
        DocumentReference fromAccountRef = accountsCollectionRef.document(fromAccount.getAccountId());

        Log.d(TAG, "makeTransfer(): ");

        // Subtracts the amount from the accounts balance and updates the account
        fromAccountRef
                .update("accountBalance",
                fromAccount.getAccountBalance() - transactionModel.getAmount())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Payment processed");
                            // Uploads payment transaction
                            uploadPaymentTransaction(context, TAG, fromAccount, transactionModel);
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    // Uploads transfer transactions between 2 accounts
    private void uploadTransaction(final Context context, final String TAG, AccountModel fromAccount,
                                   AccountModel toAccount,
                                   TransactionModel transactionModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Required since Firestore doesnt have an OR operator for queries
        final CollectionReference fromAccountTransactionRef = db.collection("Accounts")
                .document(fromAccount.getAccountId()).collection("Transactions");
        final CollectionReference toAccountTransactionRef = db.collection("Accounts")
                .document(toAccount.getAccountId()).collection("Transactions");
        final DocumentReference fromAccountTransactionDocRef = fromAccountTransactionRef.document();
        final DocumentReference toAccountTransactionDocRef = toAccountTransactionRef.document();

        // Creates a batch for multiple writes
        WriteBatch batch = db.batch();
        batch.set(fromAccountTransactionDocRef, transactionModel);
        batch.set(toAccountTransactionDocRef, transactionModel);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(context,
                            context.getString(R.string.transfer_complete), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Unable to upload Transaction");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Unable to upload Transaction");
                    }
                });
    }

    // Uploads payment transaction
    private void uploadPaymentTransaction(final Context context, final String TAG,
                                          AccountModel fromAccount,
                                          TransactionModel transactionModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference fromAccountTransactionRef = db.collection("Accounts")
                .document(fromAccount.getAccountId()).collection("Transactions");
        final DocumentReference fromAccountTransactionDocRef = fromAccountTransactionRef.document();

        fromAccountTransactionDocRef.set(transactionModel)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(context,
                            context.getString(R.string.payment_completed), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Unable to upload Transaction");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Unable to upload Transaction");
                    }
                });
    }
}
