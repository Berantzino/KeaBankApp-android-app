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

// Gets the data from our datasourse (Firestore) into our RecyclerView
public class AccountsOverviewAdapter extends FirestoreRecyclerAdapter<AccountModel,
                                                AccountsOverviewAdapter.AccountsOverviewHolder> {

    private OnItemClickListener mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options The options for the RecyclerView
     */
    public AccountsOverviewAdapter(@NonNull FirestoreRecyclerOptions<AccountModel> options) {
        super(options);
    }

    // Binds data to the AccountsOverviewHolder
    @Override
    protected void onBindViewHolder(@NonNull AccountsOverviewHolder holder, int position, @NonNull AccountModel model) {

        holder.textViewAccountName.setText(model.getAccountName());
        holder.textViewAccountId.setText(model.getAccountId());
        holder.textViewBalance.setText(String.valueOf(model.getAccountBalance()));
    }

    // Creates the view in AccountsOverviewHolder, using the account_item layout
    @NonNull
    @Override
    public AccountsOverviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Tells the Adapter which layout it has to inflate
        // viewGroup is the RecyclerView, from where we can get the context
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_item,
                viewGroup, false);

        return new AccountsOverviewHolder(v);
    }

    // Describes an item view and metadata about it's place in the RecyclerView
    class AccountsOverviewHolder extends RecyclerView.ViewHolder {

        TextView textViewAccountName;
        TextView textViewAccountId;
        TextView textViewBalance;

        public AccountsOverviewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAccountName = itemView.findViewById(R.id.text_view_account_name);
            textViewAccountId = itemView.findViewById(R.id.text_view_account_id);
            textViewBalance = itemView.findViewById(R.id.text_view_balance);

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
