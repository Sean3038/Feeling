package com.example.ffes.feeling.api;

import android.support.annotation.NonNull;

/**
 * Created by Ffes on 2017/12/21.
 */

public interface UploadFeeling {
    void uploadFeeling(@NonNull Feel feel,@NonNull byte[] image,UploadCallBack callBack);
}
