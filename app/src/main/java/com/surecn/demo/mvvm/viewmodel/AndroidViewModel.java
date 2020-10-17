package com.surecn.demo.mvvm.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.surecn.demo.mvvm.dao.AppDatabase;
import com.surecn.demo.mvvm.entity.User;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class AndroidViewModel extends ViewModel {

    protected Application mContext;

    public AndroidViewModel(@NonNull Application context) {
        mContext = context;
    }
}
