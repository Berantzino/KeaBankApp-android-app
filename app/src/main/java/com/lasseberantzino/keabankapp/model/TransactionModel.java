package com.lasseberantzino.keabankapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TransactionModel implements Parcelable {

    private String mTransactionTitle;
    private String mTransactionMessage;
    private String mTransferToId;
    private String mTransferFromId;
    private double mAmount;
    private Date mTransactionDate;
    private TransactionType mTransactionType;
    private boolean mAutoPay;

    public TransactionModel() {
    }

    public TransactionModel(String transactionTitle, String transactionMessage, String transferToId,
                            String transferFromId, double amount, Date transactionDate,
                            TransactionType transactionType, boolean autoPay) {
        mTransactionTitle = transactionTitle;
        mTransactionMessage = transactionMessage;
        mTransferToId = transferToId;
        mTransferFromId = transferFromId;
        mAmount = amount;
        mTransactionDate = transactionDate;
        mTransactionType = transactionType;
        mAutoPay = autoPay;
    }

    public TransactionType getTransactionType() {
        return mTransactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        mTransactionType = transactionType;
    }

    public boolean isAutoPay() {
        return mAutoPay;
    }

    public void setAutoPay(boolean autoPay) {
        mAutoPay = autoPay;
    }

    public String getTransactionTitle() {
        return mTransactionTitle;
    }

    public void setTransactionTitle(String transactionTitle) {
        mTransactionTitle = transactionTitle;
    }

    public String getTransactionMessage() {
        return mTransactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        mTransactionMessage = transactionMessage;
    }

    public String getTransferToId() {
        return mTransferToId;
    }

    public void setTransferToId(String transferToId) {
        mTransferToId = transferToId;
    }

    public String getTransferFromId() {
        return mTransferFromId;
    }

    public void setTransferFromId(String transferFromId) {
        mTransferFromId = transferFromId;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public Date getTransactionDate() {
        return mTransactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        mTransactionDate = transactionDate;
    }

    protected TransactionModel(Parcel in) {
        mTransactionTitle = in.readString();
        mTransactionMessage = in.readString();
        mTransferToId = in.readString();
        mTransferFromId = in.readString();
        mAmount = in.readDouble();
        long tmpMTransactionDate = in.readLong();
        mTransactionDate = new Date(tmpMTransactionDate);
        mTransactionType = (TransactionType) in.readValue(TransactionType.class.getClassLoader());
        mAutoPay = (boolean) in.readValue(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTransactionTitle);
        dest.writeString(mTransactionMessage);
        dest.writeString(mTransferToId);
        dest.writeString(mTransferFromId);
        dest.writeDouble(mAmount);
        dest.writeLong(mTransactionDate.getTime());
        dest.writeValue(mTransactionType);
        // Android handles it as: writeInt( (Boolean) v ? 1 : 0 )
        // if v == true assign 1 else 0
        dest.writeValue(mAutoPay);
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TransactionModel> CREATOR = new Parcelable.Creator<TransactionModel>() {
        @Override
        public TransactionModel createFromParcel(Parcel in) {
            return new TransactionModel(in);
        }

        @Override
        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };
}