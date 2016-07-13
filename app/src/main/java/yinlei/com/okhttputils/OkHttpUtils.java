package yinlei.com.okhttputils;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 在此写用途
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
    private static Request getRequestFromUrl(String urlString) {
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
    private static Response getResponseFromUrl(String urlString) throws IOException {
        Request request = getRequestFromUrl(urlString);
        Response response = OK_HTTP_CLIENT.newCall(request).execute();
        return response;
    }

    /**
     * 获取responseBody对象
     * @param urlString
     * @return
     * @throws IOException
     */
    private static ResponseBody getResponseBodyFromUrl(String urlString) throws IOException {
        Response response = getResponseFromUrl(urlString);
        if (response.isSuccessful()) {//code >= 200 && code < 300
            ResponseBody responseBody = response.body();
            return responseBody;
        }
        return null;
    }

    /**
     * 通过网路请求获取服务器端发过来的字符串
     * @param urlString
     * @return
     * @throws IOException
     */
    public static String loadStringFromUrl(String urlString) throws IOException {
        ResponseBody responseBody = getResponseBodyFromUrl(urlString);
        if (responseBody != null){
            return responseBody.string();
        }
        return null;
    }

    /**
     * 通过网络请求获取字节数组
     * @param urlString
     * @return
     * @throws IOException
     */
    public static byte[] loadByteFromUrl(String urlString) throws IOException {
        ResponseBody responseBody = getResponseBodyFromUrl(urlString);
        if (responseBody != null){
            return responseBody.bytes();
        }
        return null;
    }

    /**
     * 通过网络请求获取输出流
     * @param urlString
     * @return
     * @throws IOException
     */
    public static InputStream loadInputStreamFromUrl(String urlString) throws IOException {
        ResponseBody responseBody = getResponseBodyFromUrl(urlString);
        if (responseBody != null){
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
     * @param urlString
     * @param callback
     */
    public static void loadDataByNewThread(String urlString, Callback callback){
        Request request = getRequestFromUrl(urlString);
        OK_HTTP_CLIENT.newCall(request).enqueue(callback);
    }


}
