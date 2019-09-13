package com.lediya.networklib.response;

import okhttp3.Request;

public class NetworkCall {

    private Request request;
    private boolean isCanceled;
    private boolean isExecuted;

    public NetworkCall(Request request, boolean isCanceled, boolean isExecuted) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean isExecuted() {
        return isExecuted;
    }






}
