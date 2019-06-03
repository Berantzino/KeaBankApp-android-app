package com.lasseberantzino.keabankapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lasseberantzino.keabankapp.R;
import com.lasseberantzino.keabankapp.model.AccountModel;
import com.lasseberantzino.keabankapp.model.TransactionModel;

import java.text.SimpleDateFormat;

// Gets the data from our datasourse (Firestore) into our RecyclerView
public class TransactionAdapter extends FirestoreRecyclerAdapter<TransactionModel, TransactionAdapter.TransactionHolder> {

    private OnItemClickListener mListener;
    private AccountModel mAccountModel;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options The options for the RecyclerView
     */
    public TransactionAdapter(@NonNull FirestoreRecyclerOptions<TransactionModel> options,
                              AccountModel accountModel) {
        super(options);
        mAccountModel = accountModel;
    }

    // Binds data to the TransactionHolder
    @Override
    protected void onBindViewHolder(@NonNull TransactionHolder holder, int position, @NonNull TransactionModel model) {

        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String amount = String.valueOf(model.getAmount());

        holder.textViewName.setText(model.getTransactionTitle());
        holder.textViewDate.setText(sdf.format(model.getTransactionDate()));

        if (mAccountModel.getAccountId().equals(model.getTransferFromId())) {
            amount = "-" + amount;
        }
        holder.textViewamount.setText(amount);

    }

    // Creates the view in TransactionHolder, using the transaction_item layout
    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_item,
                viewGroup, false);

        return new TransactionHolder(v);
    }

    // Describes an item view and metadata about it's place in the RecyclerView
    class TransactionHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDate;
        TextView textViewamount;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_transaction_name);
            textViewDate = itemView.findViewById(R.id.text_view_transaction_date);
            textViewamount = itemView.findViewById(R.id.text_view_amount);

            // Catches a click anywhere on the card view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    // Gets rid of crash in case we click and item that's being deleted
                    if (position != RecyclerView.NO_POSITION && mListener != null) {
                        // Calls the interface method OnItemClick
                        mListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    // Used to send data to the Activity that needs to respond to a card view clicked
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
