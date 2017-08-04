package com.unixtrong.anbo.compo;

import android.app.Application;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.unixtrong.anbo.consts.Constants;

/**
 * Created by danyun on 2017/8/3
 */

public class AnboApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化微博 SDK，需要传入（应用 key，跳转链接，权限作用域）
        // SDK 的全称是：Software Development Kit（软件开发工具包）
        WbSdk.install(this, new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));
    }
}
