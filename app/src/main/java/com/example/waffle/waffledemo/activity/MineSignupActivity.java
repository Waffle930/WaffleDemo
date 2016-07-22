package com.example.waffle.waffledemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.waffle.waffledemo.R;
import com.example.waffle.waffledemo.bean.UserBean;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MineSignupActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;
    private EditText mTelephone;
    private EditText mEmail;
    private Button mSignupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_account_signup_layout);
        initView();
        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String telephone = mTelephone.getText().toString();
                String email = mEmail.getText().toString();
                if( !"".equals(username) &&
                        !"".equals(password) &&
                        !"".equals(telephone) &&
                        !"".equals(email)){
                    accountSignUp(username,password,telephone,email);
                } else{
                    Toast.makeText(MineSignupActivity.this,"信息输入不正确！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void accountSignUp(String username, String password, String telephone, String email) {
        UserBean user = new UserBean();
        user.setUsername(username);
        user.setPassword(password);
        user.setMobilePhoneNumber(telephone);
        user.setEmail(email);
        user.signUp(new SaveListener<UserBean>() {
            @Override
            public void done(UserBean user, BmobException e) {
                if(e==null){
                    Toast.makeText(MineSignupActivity.this,"注册成功:" +user.toString(),Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(MineSignupActivity.this,"注册失败:" + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initView() {
        mUsername = (EditText) findViewById(R.id.account_signup_username);
        mPassword = (EditText) findViewById(R.id.account_signup_password);
        mTelephone = (EditText) findViewById(R.id.account_signup_telephone);
        mEmail = (EditText) findViewById(R.id.account_signup_email);
        mSignupBtn = (Button) findViewById(R.id.account_signup_button);

    }
}
