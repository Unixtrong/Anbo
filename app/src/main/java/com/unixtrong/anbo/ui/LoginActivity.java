package com.unixtrong.anbo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.unixtrong.anbo.R;

public class LoginActivity extends AppCompatActivity implements WbAuthListener {

    private SsoHandler mSsoHandler;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mResultTextView = (TextView) findViewById(R.id.tv_result);

        // 初始化 SsoHandler，该类是发起授权的核⼼类。
        mSsoHandler = new SsoHandler(this);
    }

    public void loginWeibo(View view) {
        // 调用应用内的网页登录微博
        mSsoHandler.authorizeWeb(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取登录结果
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    /**
     * 验证成功时回调该方法。
     */
    @Override
    public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
        String result = oauth2AccessToken.isSessionValid() ? "验证成功" : "会话无效";
        mResultTextView.setText(result + ":\n" + oauth2AccessToken.getToken());
    }

    /**
     * 验证取消时回调该方法。
     */
    @Override
    public void cancel() {
        mResultTextView.setText("验证取消");
    }

    /**
     * 验证失败时回调该方法。
     */
    @Override
    public void onFailure(WbConnectErrorMessage errorMsg) {
        mResultTextView.setText("验证失败：\n" + errorMsg.getErrorCode() + ": " + errorMsg.getErrorMessage());
    }
}