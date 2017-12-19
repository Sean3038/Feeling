package com.example.ffes.feeling.timber;

import android.app.Application;
import android.util.Log;

import com.example.ffes.feeling.BuildConfig;

import java.util.Timer;

import timber.log.Timber;

/**
 * Created by Ffes on 2017/12/20.
 */

public class TimberApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Timber.plant(new ThreadAwareTree());
        }else{
            Timber.plant(new ReleaseTree());
        }
    }

    public class ThreadAwareTree extends Timber.DebugTree{
        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return super.createStackElementTag(element)+": "+element.getLineNumber();
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if(tag!=null){
                String threadName=Thread.currentThread().getName();
                tag="<"+threadName+">"+tag;
            }
            super.log(priority, tag, message, t);
        }
    }

    public class ReleaseTree extends ThreadAwareTree{
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO){
                return;
            }
            super.log(priority, tag, message, t);
        }
    }
}
