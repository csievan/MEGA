package com.example.weitan.mega.datamanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kanmanus.kmutt.sit.ijoint.Contextor;
import com.kanmanus.kmutt.sit.ijoint.net.DefaultSubscriber;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Semicolon07 on 7/5/2016 AD.
 */

public class BaseDataManager {
    protected Subscription executeObservable(Observable observable, DefaultSubscriber subscriber){
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    protected boolean isThereInternetConnection() {
        boolean isConnected;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) Contextor.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }
}
