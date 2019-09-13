package com.lediya.networklib.interceptor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lediya.networklib.exception.NetworkCommonException;
import com.lediya.networklib.utils.ConnectivityUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityInterceptor implements Interceptor {
    private Context context;

    public ConnectivityInterceptor(Context mContext) {
        context = mContext;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (!ConnectivityUtils.isConnected(context)) {
            throw new NetworkCommonException(NetworkCommonException.TYPE.NO_INTERNET);
        }
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }


}
