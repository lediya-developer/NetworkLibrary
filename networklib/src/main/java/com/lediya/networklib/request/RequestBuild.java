package com.lediya.networklib.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import com.google.gson.JsonElement;
import com.lediya.networklib.Network;
import com.lediya.networklib.exception.NetworkCommonException;
import com.lediya.networklib.interceptor.NetworkServiceApi;
import com.lediya.networklib.interfaces.NetworkCallback;
import com.lediya.networklib.utils.CommonConfig;

import java.util.Map;
import retrofit2.Call;

public class RequestBuild {
    private String url;
    private Map<String,Object> body;
    private String TAG ="RequestBuild";
    private Network network;
    public void initRetrofit(Context context,String baseUrl,int cacheSize,int cacheRefreshTime,int connectTimeout,int readTimeOut){
        network = new Network(context);
        network.setEnableLog(true);
        network.setAppBaseUrl(baseUrl);
        network.SetCacheSizeInMb(cacheSize);
        network.setCacheRefreshTimeInMilliSec(cacheRefreshTime);
        network.SetConnectTimeoutSeconds(connectTimeout);
        network.SetReadTimeoutSeconds(readTimeOut);
        network.with(context, network);
    }
    public void setRequestInfoMethod(String url, Map<String,Object> body,String queryStr)throws NetworkCommonException {
        if(url!=null){
            this.url = network.getAppBaseUrl().concat(url);
            if(queryStr!=null){
                this.url = network.getAppBaseUrl().concat(url);
                this.url = this.url.concat(queryStr);
            }
            if(body!=null){
                this.body = body;
            }
        }else{
            Log.i(TAG,"url is null");
            throw new NetworkCommonException(NetworkCommonException.TYPE.URL_NOT_FOUND);
        }
    }
    public void  invokeRequestMethod(int method, NetworkCallback callback) throws NetworkCommonException {
            if (body != null && method == CommonConfig.GET) {
                this.url = buildURlParamRequest();
            }
            if(body==null){
                if(method != CommonConfig.POST || method !=CommonConfig.GET){
                    throw new NetworkCommonException(NetworkCommonException.TYPE.PARAMETER_NOT_FOUND);
                }
            }
            network.callACM(getCallRequest(method), callback);
    }
    private Call<JsonElement> getCallRequest(int method){
     Call<JsonElement> responseBodyCall= null;
      switch(method){
          case CommonConfig.GET:
              responseBodyCall= network.createApiService(NetworkServiceApi.class).responseGet(url);
              Log.i(TAG,"responseBodyCall:"+ responseBodyCall);
              break;
          case CommonConfig.POST:
            if(body!=null){
               responseBodyCall= network.createApiService(NetworkServiceApi.class).responseWithBodyPost(url,body);
              }else{
              responseBodyCall= network.createApiService(NetworkServiceApi.class).responsePost(url);
            }
              Log.i(TAG,"responseBodyCall:"+ responseBodyCall);
              break;
          case CommonConfig.PUT:
              if(body!=null){
                  responseBodyCall= network.createApiService(NetworkServiceApi.class).responseWithBodyPut(url,body);
              }
              break;
          case CommonConfig.PATCH:
              if(body!=null){
                  responseBodyCall= network.createApiService(NetworkServiceApi.class).responseWithBodyPatch(url,body);
              }
              break;
          case CommonConfig.DELETE:
              if(body!=null){
                  responseBodyCall= network.createApiService(NetworkServiceApi.class).responseWithBodyDelete(url,body);
              }
              break;
          default:
              break;
      }
        return responseBodyCall;
    }
    private String buildURlParamRequest(){
        final Uri.Builder builder = Uri.parse(url).buildUpon();
        if(body!=null){
            if(body.size()>=0&!body.isEmpty()){
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue().toString());
                }
                return builder.build().toString();
            }
            else{
                return url;
            }
        }
        else{
            return url;
        }
    }
}
