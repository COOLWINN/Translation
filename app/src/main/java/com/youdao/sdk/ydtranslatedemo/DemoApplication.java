/**
 * @(#)DemoApplication.java, 2015年4月3日. Copyright 2012 Yodao, Inc. All rights
 *                           reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is
 *                           subject to license terms.
 */
package com.youdao.sdk.ydtranslatedemo;

import com.youdao.sdk.app.YouDaoApplication;

import android.app.Application;

/**
 * @author lukun
 */
public class DemoApplication extends Application {

    private static DemoApplication yourApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        if(YouDaoApplication.getApplicationContext() == null)
            YouDaoApplication.init(this, "zhudytest123");
//        YouDaoApplication.init(this,"1b8cb73b7a069078");//创建应用，每个应用都会有一个Appid，绑定对应的翻译服务实例，即可使用
//        YouDaoApplication.init(this,"72103667078f5b93");//创建应用，每个应用都会有一个Appid，绑定对应的翻译服务实例，即可使用
        yourApplication = this;
    }

    public static DemoApplication getInstance() {
        return yourApplication;
    }

}
