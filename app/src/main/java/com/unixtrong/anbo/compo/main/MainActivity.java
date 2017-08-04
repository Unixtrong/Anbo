package com.unixtrong.anbo.compo.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.unixtrong.anbo.R;
import com.unixtrong.anbo.entity.ApiResult;
import com.unixtrong.anbo.entity.Feed;
import com.unixtrong.anbo.handler.AppInfo;
import com.unixtrong.anbo.handler.WeiboApi;
import com.unixtrong.anbo.tools.Lg;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements WbAuthListener {

    private RecyclerView.Adapter mAdapter;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private List<Feed> mFeedList = new ArrayList<>();
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (AppInfo.hasSignIn(this)) {
            Lg.debug("has sign in.");
            initData();
        } else {
            Lg.debug("to sign in.");
            toSignIn();
        }
    }

    private void toSignIn() {
        mSsoHandler = new SsoHandler(this);
        mSsoHandler.authorizeWeb(this);
    }

    private void initView() {
        mAdapter = new MainAdapter(this, mFeedList);

        RecyclerView feedsRecycler = (RecyclerView) findViewById(R.id.rv_main_feeds);
        feedsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        feedsRecycler.setAdapter(mAdapter);
        feedsRecycler.setItemAnimator(new DefaultItemAnimator());
        feedsRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void initData() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String token = AppInfo.getAccessToken(MainActivity.this);
                Lg.debug("token: " + token);
                ApiResult<List<Feed>> apiResult = WeiboApi.timeLine(token, 10);
                boolean loadResult = false;
                if (apiResult != null && apiResult.isSuccess()) {
                    List<Feed> feeds = apiResult.getBody();
                    if (feeds != null) {
                        Lg.debug(String.format(Locale.getDefault(), "result, size: %d, data: %s", feeds.size(), apiResult.getData()));
                        mFeedList.clear();
                        mFeedList.addAll(feeds);
                        loadResult = true;
                    }
                }
                final boolean isLoadSuccess = loadResult;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoadSuccess) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.main_request_failure), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
            AppInfo.setAccessToken(this, o2Token.getToken());
            initData();
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
