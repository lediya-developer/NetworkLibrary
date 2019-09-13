package com.lediya.networklib.interceptor;

import com.google.gson.JsonElement;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;
public interface NetworkServiceApi {
    @GET
    Call<JsonElement> responseGet(@Url String url);
    @POST
    Call<JsonElement> responsePost(@Url String url);
    @POST
    Call<JsonElement> responseWithBodyPost(@Url String url, @Body Map<String,Object> body);
    @PUT
    Call<JsonElement> responseWithBodyPut(@Url String url, @Body Map<String,Object> body);
    @PATCH
    Call<JsonElement> responseWithBodyPatch(@Url String url, @Body Map<String,Object> body);
    @PATCH
    Call<JsonElement> responseWithBodyDelete(@Url String url, @Body Map<String,Object> body);
    //Generic types parameter
    @GET
    <T> Call<T> responseGetGeneric(@Url String url);
    @POST
    <T> Call<T>  responsePostGeneric(@Url String url);
    @POST
    <T> Call<T>  responseWithBodyPostGeneric(@Url String url, @Body Map<String,Object> body);
    @PUT
    <T> Call<T>  responseWithBodyPutGeneric(@Url String url, @Body Map<String,Object> body);
    @PATCH
    <T> Call<T>  responseWithBodyPatchGeneric(@Url String url, @Body Map<String,Object> body);
    @PATCH
    <T> Call<T>  responseWithBodyDeleteGeneric(@Url String url, @Body Map<String,Object> body);
}
