package com.surecn.demo.mvvm.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "USER")
public class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "USERNAME")
    private String mUserName;

    @NonNull
    @ColumnInfo(name = "PASSWORD")
    private String mPassword;

    @NonNull
    public String getUserName() {
        return mUserName;
    }

    public void setUserName(@NonNull String userName) {
        this.mUserName = userName;
    }

    @NonNull
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@NonNull String password) {
        this.mPassword = password;
    }
}