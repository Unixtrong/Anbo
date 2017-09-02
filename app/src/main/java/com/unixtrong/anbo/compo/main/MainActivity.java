package com.unixtrong.anbo.compo.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.unixtrong.anbo.R;
import com.unixtrong.anbo.compo.home.HomeFragment;
import com.unixtrong.anbo.handler.AppInfo;
import com.unixtrong.anbo.tools.Lg;

public class MainActivity extends AppCompatActivity implements WbAuthListener {

    private SsoHandler mSsoHandler;
    private MainPagerAdapter mAdapter;
    private ViewPager mPager;
    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // 判断用户是否登录
        if (AppInfo.hasSignIn(this)) {
            Lg.debug("has sign in.");
            // 用户登录，则更新需要展示的数据
        } else {
            Lg.debug("to sign in.");
            // 用户未登录，则跳转微博的 Web 登录页
            toSignIn();
        }
    }

    private void toSignIn() {
        mSsoHandler = new SsoHandler(this);
        mSsoHandler.authorizeWeb(this);
    }

    private void initView() {
        mPager = (ViewPager) findViewById(R.id.vp_main_fragment);
        mAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mHomeFragment = (HomeFragment) mAdapter.getItem(0);
        mPager.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取登录结果
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 验证成功时回调该方法。
     */
    @Override
    public void onSuccess(Oauth2AccessToken o2Token) {
        String result = getString(o2Token.isSessionValid() ? R.string.main_auth_success : R.string.main_auth_invalid);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        if (o2Token.isSessionValid()) {
            // 登录成功时，保存验证 token，并更新数据
            AppInfo.setAccessToken(this, o2Token.getToken());
            if (mHomeFragment != null) {
                mHomeFragment.updateFeedList();
            }
        }
    }

    /**
     * 验证取消时回调该方法。
     */
    @Override
    public void cancel() {
        Toast.makeText(this, getString(R.string.main_auth_cancel), Toast.LENGTH_SHORT).show();
        if (mSsoHandler != null) {
            mSsoHandler.authorizeWeb(this);
        }
    }

    /**
     * 验证失败时回调该方法。
     */
    @Override
    public void onFailure(WbConnectErrorMessage errorMsg) {
        String toast = getString(R.string.main_auth_failure, errorMsg.getErrorCode(), errorMsg.getErrorMessage());
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
