package com.sien.cpsvgcase.network;

import java.io.InputStream;

import retrofit2.Call;

/**
 * Name: BaseOkHttpFileCallBack
 * Author: sien
 * Email:
 * Comment: //为了统一不同okhttp版本参数不一样的问题(使用okhttputils,继承自FileCallBack，暂未使用)
 * Date: 2016-07-07 16:27
 */
public abstract class BaseOkHttpFileCallBack {
    private String mDestFileDir;
    private String mDestFileName;


    public BaseOkHttpFileCallBack(String destFileDir, String destFileName) {
        //super(destFileDir, destFileName);
        mDestFileDir = destFileDir;
        mDestFileName = destFileName;
    }

    public String getmDestFileDir() {
        return mDestFileDir;
    }

    public String getmDestFileName() {
        return mDestFileName;
    }

    //    @Override
    public void onError(Call call, Exception e) {
        int id = -1;
        onFail(call,e);
    }

//    @Override
    public void onResponse(InputStream response) {
        int id = -1;
        onSuccess(response);
    }

//    @Override
    public void inProgress(float progress, long total) {
        int id = -1;
        onProgress(progress,total);
    }

    public abstract void onProgress(float progress,long total);
    public abstract void onFail(Call call, Exception e);
    public abstract void onSuccess(InputStream response);
}
