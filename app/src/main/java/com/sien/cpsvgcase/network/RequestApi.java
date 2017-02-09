package com.sien.cpsvgcase.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author sien
 * @date 2016/9/28
 * @descript
 */
public interface RequestApi {
    @GET
    Call<ResponseBody> getDownloadApk(@Url String url);
}
