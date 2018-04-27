package com.ligf.androidhttplib.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ligf on 2018/4/10.
 */

public class RetrofitUtil {

    private static RetrofitUtil instance;

    private Retrofit retrofit;

    private RetrofitTestService retrofitTestService;

    private RetrofitUtil(){
        initRetrofit();
    }

    public static RetrofitUtil getInstance(){
        if (instance == null){
            synchronized (RetrofitUtil.class){
                if (instance == null){
                    instance = new RetrofitUtil();
                }
            }
        }
        return instance;
    }

    private void initRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl("")    // Base Url
                .client(getHttpClient())    // 设置HTTP请求体
                .addConverterFactory(GsonConverterFactory.create(getGson())) //设置实体类的序列化和反序列化转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())   //设置请求适配器，支持RxJava的Observable，Retrofit Service请求结果封装返回Observable对象
                .build();
        retrofitTestService = retrofit.create(RetrofitTestService.class);
    }

    private OkHttpClient getHttpClient(){
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();
        return okHttpClient;
    }

    private Gson getGson(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        return gson;
    }
}
