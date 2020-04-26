package com.mibai.phonelive.community.upload;


import com.mibai.phonelive.community.network.HttpContract;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UploadFileApi {

    String UPLOAD_FILE_URL = HttpContract.ForumUpimgURL;


    @POST
    Observable<ResponseBody> uploadFile(@Url String url, @Body MultipartBody body);

    // 圈子发布
    @POST
    Observable<ResponseBody> sendPost(@Url String url, @Body MultipartBody body);
}
