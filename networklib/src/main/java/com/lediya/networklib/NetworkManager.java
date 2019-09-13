package com.lediya.networklib;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.lediya.networklib.interceptor.ConnectivityInterceptor;
import com.lediya.networklib.interfaces.NetworkCallback;
import com.lediya.networklib.response.NetworkCall;
import com.lediya.networklib.response.NetworkResponse;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    private Retrofit myRetrofit = null;
    private String appBaseUrl = "";
    private boolean isEnableLog = false;
    private long setConnectTimeoutSeconds = 60;
    private long setReadTimeoutSeconds = 60;
    private int setCacheSizeInMb = 10;
    private long cacheRefreshTimeInMilliSec = 0;
    private OkHttpClient httpClient = null;
    private Context context;

    NetworkManager(Context context) {
        this.context = context;
    }

    public String getAppBaseUrl() {
        return appBaseUrl;
    }

    public void setAppBaseUrl(String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    public boolean isEnableLog() {
        return isEnableLog;
    }

    public void setEnableLog(boolean enableLog) {
        isEnableLog = enableLog;
    }

    public long getSetConnectTimeoutSeconds() {
        return setConnectTimeoutSeconds;
    }

    public void SetConnectTimeoutSeconds(long setConnectTimeoutSeconds) {
        this.setConnectTimeoutSeconds = setConnectTimeoutSeconds;
    }

    public long getSetReadTimeoutSeconds() {
        return setReadTimeoutSeconds;
    }

    public void SetReadTimeoutSeconds(long setReadTimeoutSeconds) {
        this.setReadTimeoutSeconds = setReadTimeoutSeconds;
    }

    public int getSetCacheSizeInMb() {
        return setCacheSizeInMb;
    }

    public void SetCacheSizeInMb(int setCacheSizeInMb) {
        this.setCacheSizeInMb = setCacheSizeInMb;
    }

    public long getCacheRefreshTimeInMilliSec() {
        return cacheRefreshTimeInMilliSec;
    }

    public void setCacheRefreshTimeInMilliSec(long cacheRefreshTimeInMilliSec) {
        this.cacheRefreshTimeInMilliSec = cacheRefreshTimeInMilliSec;
    }


    <T> Retrofit getInstance(Context mContext, Network myServiceData) {
        appBaseUrl = myServiceData.getAppBaseUrl();
        isEnableLog = myServiceData.isEnableLog();
        setConnectTimeoutSeconds = myServiceData.getSetConnectTimeoutSeconds();
        setReadTimeoutSeconds = myServiceData.getSetReadTimeoutSeconds();
        setCacheSizeInMb = myServiceData.getSetCacheSizeInMb();
        cacheRefreshTimeInMilliSec = myServiceData.getCacheRefreshTimeInMilliSec();
        context = mContext;

        if (appBaseUrl.isEmpty()) {
            return null;
        }
        if (myRetrofit == null) {
            myRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .client(getHttpClient())
                    .baseUrl(appBaseUrl)
                    .build();
        }


        return myRetrofit;
    }

    public <T> T createApiService(Class<T> clazz) {
        return myRetrofit.create(clazz);
    }

    private OkHttpClient getHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (isEnableLog) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        File httpCacheDirectory = new File(context.getCacheDir(), "offlineCache");
        Cache cache = new Cache(httpCacheDirectory, setCacheSizeInMb * 1024 * 1024);
/* .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())*/
        httpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new ConnectivityInterceptor(context))
                .connectTimeout(setConnectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(setReadTimeoutSeconds, TimeUnit.SECONDS)
                .build();

        return httpClient;
    }

    @NonNull
    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Long x = cacheRefreshTimeInMilliSec;
                int y = x.intValue();
                Request request = chain.request();
                Response originalResponse = chain.proceed(request);
                String cacheControl = originalResponse.header("Cache-Control");
                if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {
                    CacheControl cc = new CacheControl.Builder()
                            .maxStale(y, TimeUnit.MILLISECONDS)
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cc)
                            .build();
                    return chain.proceed(request);

                } else {
                    return originalResponse;
                }
            }
        };
    }

    private Interceptor provideOfflineCacheInterceptor() {
        /*
         * how many day offline data showing
         * */
        return new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                try {
                    return chain.proceed(chain.request());
                } catch (Exception e) {
                    Long x = cacheRefreshTimeInMilliSec;
                    int y = x.intValue();
                    CacheControl cacheControl = new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(y, TimeUnit.MILLISECONDS)
                            .build();
                    Request offlineRequest = chain.request().newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                    return chain.proceed(offlineRequest);
                }
            }
        };
    }

    public void cancelAllRequest() {
        if (httpClient != null) {
            for (Call call : httpClient.dispatcher().queuedCalls()) {
                call.cancel();
            }
            for (Call call : httpClient.dispatcher().runningCalls()) {
                call.cancel();
            }
        }
    }

    public <T> void callACM(retrofit2.Call<T> call, final NetworkCallback callback) {
      call.enqueue(new Callback<T>() {
          @Override
          public void onResponse(retrofit2.Call<T> call, retrofit2.Response<T> response) {
              NetworkCall networkCall = new NetworkCall(call.request(), call.isCanceled(), call.isExecuted());
              NetworkResponse<T> networkResponse = new NetworkResponse<T>(response, response.raw(), response.body(),response.errorBody(), response.isSuccessful());
              callback.onResponse(networkCall, networkResponse);
          }

          @Override
          public void onFailure(retrofit2.Call<T> call, Throwable t) {
              NetworkCall networkCall = new NetworkCall(call.request(), call.isCanceled(), call.isExecuted());
              callback.onError(networkCall, t);
          }
      });
    }
}