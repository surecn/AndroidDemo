package com.surecn.demo.mvvm.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.surecn.demo.mvvm.entity.User;
import static android.arch.persistence.room.OnConflictStrategy.*;

@Dao
public interface UserDao {

    @Insert(onConflict = REPLACE)
    void register(User user);

    @Query("SELECT * FROM USER")
    User[] getAllUsers();

    @Query("SELECT * FROM USER WHERE USERNAME=:userName AND PASSWORD=:password")
    User login(String userName, String password);

}