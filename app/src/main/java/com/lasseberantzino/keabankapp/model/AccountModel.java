package com.lasseberantzino.keabankapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountModel implements Parcelable {

    private String mAccountName;
    private String mAccountId;
    private String mOwnerId;
    private double mAccountBalance = 100.0;
    private AccountType mAccountType;

    public AccountModel() {
    }

    public AccountModel(String accountName, String ownerId, AccountType accountType) {
        mAccountName = accountName;
        mOwnerId = ownerId;
        mAccountType = accountType;
    }

    public AccountModel(String accountName, String accountId, String ownerId,
                        double accountBalance, AccountType accountType) {
        mAccountName = accountName;
        mAccountId = accountId;
        mOwnerId = ownerId;
        mAccountBalance = accountBalance;
        mAccountType = accountType;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountId) {
        mAccountId = accountId;
    }

    public String getAccountName() {
        return mAccountName;
    }

    public void setAccountName(String accountName) {
        mAccountName = accountName;
    }

    public String getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(String ownerId) {
        mOwnerId = ownerId;
    }

    public double getAccountBalance() {
        return mAccountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        mAccountBalance = accountBalance;
    }

    public AccountType getAccountType() {
        return mAccountType;
    }

    public void setAccountType(AccountType accountType) {
        mAccountType = accountType;
    }

    protected AccountModel(Parcel in) {
        mAccountName = in.readString();
        mAccountId = in.readString();
        mOwnerId = in.readString();
        mAccountBalance = in.readDouble();
        mAccountType = (AccountType) in.readValue(AccountType.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAccountName);
        dest.writeString(mAccountId);
        dest.writeString(mOwnerId);
        dest.writeDouble(mAccountBalance);
        dest.writeValue(mAccountType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AccountModel> CREATOR = new Parcelable.Creator<AccountModel>() {
        @Override
        public AccountModel createFromParcel(Parcel in) {
            return new AccountModel(in);
        }

        @Override
        public AccountModel[] newArray(int size) {
            return new AccountModel[size];
        }
    };
}