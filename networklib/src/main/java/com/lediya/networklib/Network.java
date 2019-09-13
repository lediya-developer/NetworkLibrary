package com.lediya.networklib;

import android.content.Context;

import retrofit2.Retrofit;

public class Network extends NetworkManager {

    public Network(Context context) {
        super(context);
    }

    public Retrofit with(Context mContext, Network myRetrofit) {
        return getInstance(mContext,myRetrofit);
    }

    public <T> T createApi(Class<T> aClass){
        return createApiService(aClass);
    }



}
