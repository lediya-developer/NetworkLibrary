package com.lediya.networklib.interfaces;

import com.lediya.networklib.response.NetworkCall;
import com.lediya.networklib.response.NetworkResponse;

public interface NetworkCallback<T> {
    void onResponse(NetworkCall call, NetworkResponse<T> response);

    void onError(NetworkCall call, Throwable t);
}
