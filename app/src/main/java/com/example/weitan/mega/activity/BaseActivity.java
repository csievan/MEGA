package com.example.weitan.mega.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.fix.Navigator;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Created by Semicolon07 on 7/5/2016 AD.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Navigator navigator;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = Navigator.getInstance();

        this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void setupUpButton(){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    protected void showError(Throwable e){
        showToast(String.format(getString(R.string.error),e.getMessage()));
    }

    protected void showRetryConnect(Throwable e, View.OnClickListener onRetryClickListener){
        if(e instanceof ConnectException || e instanceof SocketTimeoutException){
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
            snackbar = Snackbar
                    .make(viewGroup, getString(R.string.cannot_connect_server), Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", onRetryClickListener);

            snackbar.show();
        }
    }
    protected void removeRetryConnectView(){
        if(snackbar!=null && snackbar.isShown())
            snackbar.dismiss();
    }
}
