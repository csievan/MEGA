package com.example.weitan.mega.datamanager;

import com.kanmanus.kmutt.sit.ijoint.models.response.SignInResponse;
import com.kanmanus.kmutt.sit.ijoint.net.DefaultSubscriber;
import com.kanmanus.kmutt.sit.ijoint.net.HttpManager;

import rx.Observable;
import rx.Subscription;

/**
 * Created by Semicolon07 on 7/5/2016 AD.
 */

public class UserDataManager extends BaseDataManager{

    public Subscription login(String userName, String password, DefaultSubscriber subscriber){
        Observable<SignInResponse> call = HttpManager.getInstance().getService().signIn(userName,password);
        return executeObservable(call,subscriber);
    }


}
