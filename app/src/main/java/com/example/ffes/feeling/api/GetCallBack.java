package com.example.ffes.feeling.api;

/**
 * Created by Ffes on 2017/12/24.
 */

public interface GetCallBack<T> {
    void onSuccess(T data);
    void onFail(String message);
}
