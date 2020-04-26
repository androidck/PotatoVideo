package com.mibai.phonelive.community.upload;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class RetrofitClient {

    private static RetrofitClient mInstance;
    private static Retrofit retrofit;

    private RetrofitClient() {
        retrofit = RetrofitBuilder.buildRetrofit();
    }

    /**
     * 获取RetrofitClient实例.
     *
     * @return 返回RetrofitClient单例
     */
    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    private <T> T create(Class<T> clz) {
        return retrofit.create(clz);
    }

    /**
     * 单上传文件的封装.
     *
     * @param url 完整的接口地址
     * @param file 需要上传的文件
     * @param fileUploadObserver 上传回调
     */
    public void upLoadFile(String url, String uid,File file,
                           FileUploadObserver<ResponseBody> fileUploadObserver) {
        UploadFileRequestBody uploadFileRequestBody =
                new UploadFileRequestBody(file, fileUploadObserver);
        create(UploadFileApi.class)
                .uploadFile(url, MultipartBuilder.fileToMultipartBody(uid,file,
                        uploadFileRequestBody))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileUploadObserver);
    }



    /**
     * 多文件上传.
     *
     * @param url 上传接口地址
     * @param files 文件列表
     * @param fileUploadObserver 文件上传回调
     */
    public void upLoadFiles(String url,List<File> files,
                            FileUploadObserver<ResponseBody> fileUploadObserver) {
        create(UploadFileApi.class)
                .sendPost(url, MultipartBuilder.filesToMultipartBody(files,
                        fileUploadObserver))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileUploadObserver);

    }
}
