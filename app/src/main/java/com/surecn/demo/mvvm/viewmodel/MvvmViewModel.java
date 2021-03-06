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

public class MvvmViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mResultLiveData = new MutableLiveData<>();

    private AppDatabase mAppDatabase;

    public MvvmViewModel(@NonNull Application context) {
        super(context);
        mAppDatabase = Room.databaseBuilder(mContext, AppDatabase.class, "android_room.db")
                .allowMainThreadQueries()
                .build();
    }

    public void login(final String userName, final String password) {
        Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> e) throws Exception {
                User user  = mAppDatabase.userDao().login(userName, password);
                mResultLiveData.postValue("登录成功");
                e.onNext(user);
            }
        }).subscribe();
    }

    public void register(final User user) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                mAppDatabase.userDao().register(user);
                mResultLiveData.postValue("注册成功");
            }
        }).subscribe();
    }

    public MutableLiveData<String> getResultLiveData() {
        return mResultLiveData;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private Application mContext;

        public Factory(Application context) {
            mContext = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new MvvmViewModel(mContext);
        }
    }

}
