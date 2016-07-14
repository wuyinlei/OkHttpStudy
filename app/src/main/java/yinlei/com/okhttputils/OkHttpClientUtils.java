package yinlei.com.okhttputils;

import android.content.Context;
import android.content.DialogInterface;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: yinlei.com.okhttputils.OkHttpClientUtils.java
 * @author: myName
 * @date: 2016-07-14 21:26
 */

public class OkHttpClientUtils {

    private static OkHttpClient sOkHttpClient = null;
    private static OkHttpClientUtils mHttpClientUtils = null;

    private OkHttpClientUtils(Context context) {
        sOkHttpClient = getOkHttpClientSingleInstance();

        CookieJar cookieJar= CookieJar.NO_COOKIES;
        sOkHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        //开启响应缓存
        int cacheSize = 10 << 20;   //10MB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);
        sOkHttpClient.newBuilder().cache(cache);

        //设置合理的超时
        sOkHttpClient.newBuilder().connectTimeout(15, TimeUnit.SECONDS);
        sOkHttpClient.newBuilder().readTimeout(20, TimeUnit.SECONDS);
        sOkHttpClient.newBuilder().writeTimeout(20, TimeUnit.SECONDS);

        sOkHttpClient.newBuilder().hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

    }

    public static OkHttpClientUtils getOkkHttpClientUtils(Context context) {
        if (mHttpClientUtils == null) {
            synchronized (OkHttpClientUtils.class) {
                if (mHttpClientUtils == null) {
                    mHttpClientUtils = new OkHttpClientUtils(context);
                }
            }
        }
        return mHttpClientUtils;
    }

    public static OkHttpClient getOkHttpClientSingleInstance() {
        if (sOkHttpClient == null) {
            synchronized (OkHttpClient.Builder.class) {
                if (sOkHttpClient == null) {
                    sOkHttpClient = new OkHttpClient();
                }
            }
        }
        return sOkHttpClient;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    //GET方式访问网络
    //////////////////////////////////////////////////////////////////////////////////

    /**
     * 基本法方法
     *
     * @param url
     * @param obj
     * @return
     */
    private Request buildGetRequest(String url, Object obj) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (obj != null) {
            builder.tag(obj);
        }
        return builder.build();
    }

    /**
     * 自定义方法   返回response请求对象
     *
     * @param url
     * @param tag
     * @return
     */
    private Response buildResponse(String url, Object tag) throws IOException {
        Request request = buildGetRequest(url, tag);
        Response response = sOkHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 获取ResponseBody对象
     *
     * @param url
     * @param tag
     * @return
     * @throws IOException
     */
    private ResponseBody buildResponseBody(String url, Object tag) throws IOException {
        Response response = buildResponse(url, tag);
        if (response.isSuccessful()) {
            return response.body();
        }
        return null;
    }

    /**
     * 通过网路请求获取服务器端发过来的字符串
     *
     * @param url
     * @param obj
     * @return
     * @throws IOException
     */
    public static String loadStringFromUrl(String url, Object obj) throws IOException {
        ResponseBody responseBody = mHttpClientUtils.buildResponseBody(url, obj);
        if (responseBody != null) {
            return responseBody.string();
        }
        return null;

    }

    /**
     * 通过网络请求获取字节数组
     *
     * @param url
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] loadByteFromUrl(String url, Object obj) throws IOException {
        ResponseBody responseBody = mHttpClientUtils.buildResponseBody(url, obj);
        if (responseBody != null) {
            return responseBody.bytes();
        }
        return null;
    }

    /**
     * 通过网络请求获取输出流
     *
     * @param url
     * @param obj
     * @return
     * @throws IOException
     */
    public static InputStream loadInputStreamFromUrl(String url, Object obj) throws IOException {
        ResponseBody responseBody = mHttpClientUtils.buildResponseBody(url, obj);
        if (responseBody != null) {
            return responseBody.byteStream();
        }
        return null;
    }

    /**
     * 开启一个异步线程，通过实现异步方法实现数据的异步加载
     *
     * @param url
     * @param callback
     * @param obj
     */
    public static void getDataAsync(String url, Callback callback, Object obj) {
        Request request = mHttpClientUtils.buildGetRequest(url, obj);
        sOkHttpClient.newCall(request).enqueue(callback);

    }

    /**
     * post请求获取request请求体
     *
     * @param urlString
     * @param requestBody
     * @param obj
     * @return
     */
    private Request buildPostRequest(String urlString, RequestBody requestBody, Object obj) {
        Request.Builder builder = new Request.Builder();
        builder.tag(obj);
        builder.url(urlString).post(requestBody);
        return builder.build();
    }

    /**
     * post请求获取响应体
     *
     * @param urlString
     * @param requestBody
     * @param obj
     * @return
     * @throws IOException
     */
    private String postRequestBody(String urlString, RequestBody requestBody, Object obj) throws IOException {
        Request request = buildPostRequest(urlString, requestBody, obj);
        Response response = sOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        return null;
    }

    /**
     * post网络请求发送键值对的时候，获取RequestBody对象
     *
     * @param map
     * @return
     */
    private RequestBody buildRequestBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (!map.isEmpty() && map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * post网络访问，提交键值对
     *
     * @param uslString
     * @param map
     * @param obj
     * @return
     * @throws IOException
     */
    public static String postKeyValuePair(String uslString, Map<String, String> map, Object obj) throws IOException {
        RequestBody requestBogy = mHttpClientUtils.buildRequestBody(map);
        return mHttpClientUtils.postRequestBody(uslString, requestBogy, obj);
    }

    /**
     * post异步网络请求，提交requestBody对象
     *
     * @param urlString
     * @param requestBody
     * @param callback
     * @param obj
     */
    private void postRequestBodyAsync(String urlString, RequestBody requestBody, Callback callback, Object obj) {
        Request request = buildPostRequest(urlString, requestBody, obj);
        sOkHttpClient.newCall(request).enqueue(callback);
    }


    /**
     * post异步请求   提交键值对
     *
     * @param urlString
     * @param map
     * @param callback
     * @param obj
     */
    public static void postKeyValuePairAsync(String urlString, Map<String, String> map, Callback callback, Object obj) {
        RequestBody requestBody = mHttpClientUtils.buildRequestBody(map);
        mHttpClientUtils.postRequestBodyAsync(urlString, requestBody, callback, obj);
    }


    /**
     * post同步上传文件以及其他表单控件  (也就是提交分块请求)
     *
     * @param urlString     网络地址
     * @param map           键值对
     * @param files         文件
     * @param fromFieldName 文件名字
     * @return
     * @throws IOException
     */
    public static String postUploadFiles(String urlString, Map<String, String> map, File[] files, String[] fromFieldName, Object obj) throws IOException {
        RequestBody requestBody = mHttpClientUtils.buildRequestBody(map, files, fromFieldName);
        return mHttpClientUtils.postRequestBody(urlString, requestBody, obj);
    }

    /**
     * 文件上传的RequestBody
     *
     * @param map
     * @param files
     * @param fromFieldName
     * @return
     */
    private RequestBody buildRequestBody(Map<String, String> map, File[] files, String[] fromFieldName) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //第一部分提交  键值对信息
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));
            }
        }
        //第二部分提交   上传文件数据
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                //添加file的input部分
                RequestBody requestBody = RequestBody.create(MediaType.parse(getMimeType(fileName)), file);
                builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"" + fromFieldName[i] + "\";filename=\"" + fileName + "\""),
                        requestBody);
            }
        }

        return builder.build();

    }

    /**
     * 获取文件的格式
     *
     * @param fileName
     * @return
     */
    private static String getMimeType(String fileName) {

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "applicaption/octet-stream";
        }
        return contentTypeFor;
    }


}
