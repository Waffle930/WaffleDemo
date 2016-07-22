package com.example.waffle.waffledemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.waffle.waffledemo.R;
import com.example.waffle.waffledemo.Utils.Constants;

import cn.bmob.v3.BmobUser;
import rx.Subscriber;

public class MineLoginActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_account_login_layout);
        initView();
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void initView() {
        mUsername = (EditText) findViewById(R.id.account_login_username);
        mPassword = (EditText) findViewById(R.id.account_login_password);
        mLoginBtn = (Button) findViewById(R.id.account_login_button);
    }

    private void userLogin(){
        final BmobUser user = new BmobUser();
        user.setUsername(mUsername.getText().toString());
        user.setPassword(mPassword.getText().toString());
        user.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                if(throwable!=null){
                    Toast.makeText(MineLoginActivity.this,"登陆失败：" + throwable.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNext(BmobUser bmobUser) {
                Toast.makeText(MineLoginActivity.this,bmobUser.getUsername() + "登陆成功",Toast.LENGTH_LONG).show();
                setResult(Constants.ACCOUNT_LOGIN_COMPLETE);
                finish();
            }
        });
    }
}
