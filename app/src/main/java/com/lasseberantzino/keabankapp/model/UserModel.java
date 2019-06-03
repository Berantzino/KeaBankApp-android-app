package com.lasseberantzino.keabankapp.model;

public class UserModel {

    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mUid;
    private String mBankAffiliate;
    private int mAge;

    public UserModel() {

    }

    public UserModel(String firstName, String lastName, String email, String Uid, String bankAffiliate, int age) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mUid = Uid;
        mBankAffiliate = bankAffiliate;
        mAge = age;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getBankAffiliate() {
        return mBankAffiliate;
    }

    public void setBankAffiliate(String bankAffiliate) {
        mBankAffiliate = bankAffiliate;
    }
}
