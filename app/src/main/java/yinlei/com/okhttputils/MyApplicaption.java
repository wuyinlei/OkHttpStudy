package yinlei.com.okhttputils;

import android.app.Application;

import okhttp3.OkHttpClient;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: MyApplicaption.java
 * @author: myName
 * @date: 2016-07-14 22:17
 */

public class MyApplicaption extends Application{

    private static final String TAG = "MyApplicaption";
    private static MyApplicaption app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        initOkHttpUtils();
    }

    private void initOkHttpUtils() {

        OkHttpClient okHttpClient = OkHttpClientUtils.getOkHttpClientSingleInstance();

    }
}
