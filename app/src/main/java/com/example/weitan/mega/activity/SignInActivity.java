package com.example.weitan.mega.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.kanmanus.kmutt.sit.ijoint.MyApplication;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.datamanager.UserDataManager;
import com.kanmanus.kmutt.sit.ijoint.models.PatientProfileViewModel;
import com.kanmanus.kmutt.sit.ijoint.models.mapping.ServerResponseMapping;
import com.kanmanus.kmutt.sit.ijoint.models.response.SignInResponse;
import com.kanmanus.kmutt.sit.ijoint.net.DefaultSubscriber;


public class SignInActivity extends BaseActivity {

    public static Intent callingIntent(Context context) {
        Intent intent = new Intent(context,SignInActivity.class);
        return intent;
    }

    private UserDataManager userDataManager;
    private EditText userNameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userDataManager = new UserDataManager();
        userNameEditText = (EditText) findViewById(R.id.et_username);
        passwordEditText = (EditText) findViewById(R.id.et_password);

    }


    public void signIn(View v){
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if(userName.length() == 0){
            showToast(getString(R.string.error_empty_username));
            return;
        }
        if(password.length() == 0){
            showToast(getString(R.string.error_empty_password));
            return;
        }

        userDataManager.login(userName,password,new LoginSubscriber());
    }



    class LoginSubscriber extends DefaultSubscriber<SignInResponse>{
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            showError(e);
        }

        @Override
        public void onNext(SignInResponse signInResponse) {
            if(!signInResponse.isStatus()){
                showToast(getString(R.string.error_login_incorrect));
                return;
            }
            setLoginProfileSession(signInResponse);
        }
    }

    private void setLoginProfileSession(SignInResponse signInResponse){
        PatientProfileViewModel viewModel = new ServerResponseMapping().transformSignIn(signInResponse);
        MyApplication.getInstance().setSession(viewModel);

        navigator.navigateToMain(this);
        finish();
    }

}
