package com.lediya.networklib.response;


import com.google.gson.Gson;
import com.lediya.networklib.exception.NetworkCommonException;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.Nullable;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class NetworkResponse<T> {
    private Response response;
    private final okhttp3.Response rawResponse;
    private final @Nullable
    T body;
    private final @Nullable
    ResponseBody errorBody;
    private boolean isSuccessful;
    private Json responseData;

    public NetworkResponse(retrofit2.Response response, okhttp3.Response rawResponse, @Nullable T body, @Nullable ResponseBody errorBody, boolean isSuccessful) {
        this.response = response;
        this.rawResponse = rawResponse;
        this.body = body;
        this.errorBody = errorBody;
        this.isSuccessful=isSuccessful;
    }

    private NetworkResponse(okhttp3.Response rawResponse, @Nullable T body, @Nullable Json resData,
                            @Nullable ResponseBody errorBody) {
        this.rawResponse = rawResponse;
        this.body = body;
        this.errorBody = errorBody;
        this.responseData = resData;
    }

    public okhttp3.Response raw() {
        return rawResponse;
    }

    public int code() {
        return rawResponse.code();
    }

    public String message() {
        return rawResponse.message();
    }

    public Headers headers() {
        return rawResponse.headers();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public @Nullable
    T body() {
        return body;
    }
    public @Nullable
    Json responseBody() {
        return responseData;
    }
    public @Nullable
    ResponseBody errorBody() {
        return errorBody;
    }
    @Override
    public String toString() {
        return rawResponse.toString();
    }

    public Object getModelData(String className) throws NetworkCommonException {
        Class classToInvestigate = null;
        try {
            classToInvestigate = Class.forName(className);
             return new Gson().fromJson(body().toString(),classToInvestigate);
        } catch (ClassNotFoundException e) {
            throw new NetworkCommonException(NetworkCommonException.TYPE.CLASS_NOT_FOUND);
        } catch (SecurityException e) {
            throw new NetworkCommonException(NetworkCommonException.TYPE.SECURITY_ISSUE);
        }catch(ClassCastException e){
            throw new NetworkCommonException(NetworkCommonException.TYPE.ClASS_CANNOT_CAST);
        }

    }
}
