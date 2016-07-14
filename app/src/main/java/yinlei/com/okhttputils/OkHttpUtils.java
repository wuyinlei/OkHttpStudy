package yinlei.com.okhttputils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URI;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.Callback;
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
 * OkHttpUtils工具类
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: OkHttpUtils.java
 * @author: myName
 * @date: 2016-07-13 21:39
 */

public class OkHttpUtils {

    //////////////////////////////////////////////////////////////////////////
    //Http GET请求中的普通用法：4个步奏
    //1、获取Request对象     Request对象是OkHttp中访问的请求，Builder是辅助类
    //2、获取Response对象    获取Call对象，调用该对象的execute方法
    //3、获取ResponseBody对象  通过response对象的body方法来获取
    //4、从ResponseBody对象中获取服务器端返回的数据   服务器端返回的数据全在ResponseBody
    //响应体对象中，通过响应体的不同方法以获取到字符串、字节流、或者流对象，也可以封装一下获取bean对象
    /////////////////////////////////////////////////////////////////////////

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    /**
     * 获取request请求对象
     *
     * @param urlString
     * @return
     */
    private static Request buildGetRequest(String urlString) {
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        return request;
    }

    /**
     * 获取response响应对象
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    private static Response buildResponse(String urlString) throws IOException {
        Request request = buildGetRequest(urlString);
        Response response = OK_HTTP_CLIENT.newCall(request).execute();
        return response;
    }

    /**
     * 获取responseBody对象
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    private static ResponseBody buildResponseBody(String urlString) throws IOException {
        Response response = buildResponse(urlString);
        if (response.isSuccessful()) {//code >= 200 && code < 300
            ResponseBody responseBody = response.body();
            return responseBody;
        }
        return null;
    }

    /**
     * 通过网路请求获取服务器端发过来的字符串
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    public static String loadStringFromUrl(String urlString) throws IOException {
        ResponseBody responseBody = buildResponseBody(urlString);
        if (responseBody != null) {
            return responseBody.string();
        }
        return null;
    }

    /**
     * 通过网络请求获取字节数组
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    public static byte[] loadByteFromUrl(String urlString) throws IOException {
        ResponseBody responseBody = buildResponseBody(urlString);
        if (responseBody != null) {
            return responseBody.bytes();
        }
        return null;
    }

    /**
     * 通过网络请求获取输出流
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    public static InputStream loadInputStreamFromUrl(String urlString) throws IOException {
        ResponseBody responseBody = buildResponseBody(urlString);
        if (responseBody != null) {
            return responseBody.byteStream();
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////////////
    //Http GET请求中的高级用法：3个步奏
    //1、获取Request对象     Request对象是OkHttp中访问的请求，Builder是辅助类
    //2、将Request请求添加进请求队列  Call对象的enqueue为OkHttp提供了异步访问方法 而execute则是同步的方法
    //3、实现回调方法
    /////////////////////////////////////////////////////////////////////////

    /**
     * 开启一个异步线程，通过实现异步方法实现数据的异步加载
     *
     * @param urlString
     * @param callback
     */
    public static void getDataAsync(String urlString, Callback callback) {
        Request request = buildGetRequest(urlString);
        OK_HTTP_CLIENT.newCall(request).enqueue(callback);

    }

    ///////////////////////////////////////////////////////////////////////////////
    //POST同步网络请求和异步网络请求
    //A：HTTP  POST提交键值对数据 ： 6步奏
    //1 、往FormBody对象中放置键值对数据
    //2、获取RequestBody请求体对象
    //3、获取Request对象，将RequestBody放置到Request对象中
    //4、获取Response对象
    //5、获取ResponseBody队形
    //6、从ResponseBody对象中获取服务器返回的数据
    //////////////////////////////////////////////////////////////////////////////
    //B: HTTP POST提交 JSON字符串数据 ： 6步奏
    //1、定义MediaType对象
    //其他和上面一样
    //////////////////////////////////////////////////////////////////////////////
    //C: HTTP POST上传文件数据 ： 6步奏
    //1、往MultipartBuilder对象中写入上传文件及表单头信息
    //其他和上面一样
    //////////////////////////////////////////////////////////////////////////////

    /**
     * post请求获取request
     *
     * @param urlString
     * @param requestBody
     * @return
     */
    private static Request buildPostRequest(String urlString, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder();
        builder.url(urlString).post(requestBody);
        return builder.build();
    }

    /**
     * post网络请求，获取字符串
     *
     * @param urlString
     * @param requestBody
     * @return
     * @throws IOException
     */
    private static String postRequestBody(String urlString, RequestBody requestBody) throws IOException {
        Request request = buildPostRequest(urlString, requestBody);
        Response response = OK_HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        return null;
    }

    /**
     * post网络请求发送键值对时候，获取RequestBody对象
     *
     * @param map
     * @return
     */
    private static RequestBody buildRequestBody(Map<String, String> map) {
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
     * @return
     * @throws IOException
     */
    public static String postKeyValuePair(String uslString, Map<String, String> map) throws IOException {
        RequestBody requestBogy = buildRequestBody(map);
        return postRequestBody(uslString, requestBogy);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //POST异步网络请求
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * post异步网络请求，提交requestBody对象
     *
     * @param urlString
     * @param requestBody
     * @param callback
     */
    private static void postRequestBodyAsync(String urlString, RequestBody requestBody, Callback callback) {
        Request request = buildPostRequest(urlString, requestBody);
        OK_HTTP_CLIENT.newCall(request).enqueue(callback);
    }

    /**
     * post异步请求  提交键值对
     *
     * @param urlString
     * @param map
     * @param callback
     */
    public static void postKeyValuePairAsync(String urlString, Map<String, String> map, Callback callback) {
        RequestBody requestBody = buildRequestBody(map);
        postRequestBodyAsync(urlString, requestBody, callback);
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
    public static String postUploadFiles(String urlString, Map<String, String> map, File[] files, String[] fromFieldName) throws IOException {
        RequestBody requestBody = buildRequestBody(map,files,fromFieldName);
        return postRequestBody(urlString, requestBody);
    }

    /**
     * 文件上传的RequestBody
     *
     * @param map
     * @param files
     * @param fromFieldName
     * @return
     */
    private static RequestBody buildRequestBody(Map<String, String> map, File[] files, String[] fromFieldName) {
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
