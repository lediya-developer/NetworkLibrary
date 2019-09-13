package com.lediya.application;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.lediya.networklib.exception.NetworkCommonException;
import com.lediya.networklib.interfaces.NetworkCallback;
import com.lediya.networklib.request.RequestBuild;
import com.lediya.networklib.response.NetworkCall;
import com.lediya.networklib.response.NetworkResponse;
import com.lediya.networklib.utils.CommonConfig;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RequestBuild requestBuild;
    private static final String APPLICATION_BASE_URL = "https://reqres.in/";
    private NetworkResponse networkResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text =(TextView)findViewById(R.id.text);
        try {
        requestBuild = new RequestBuild();
        requestBuild.initRetrofit(getApplicationContext(),APPLICATION_BASE_URL,10,5000000,60,60);
        Map<String,Object> body = new HashMap<>();
      //  body.put("id","1");
       //body.put("email","eve.holt@reqres.in");
       // body.put("password","pistol");
        body.put("name","morpheus");
        body.put("job","zion resident");
        //String url = "api/login";
       // String url = "api/register";
       String url ="api/users/2";
       // String url ="api/users";
      /*  requestBuild.invokeGenericRequest(CommonConfig.DELETE, new ACVGenericCallback() {
            @Override
            public <T> void onSuccess(NetworkCall acvCall, ACVGenericResponse<T> dataResponse) {
             ACVDataResponse<UserData> userData = dataResponse.body();

            }

            @Override
            public void onError(NetworkCall acvCall, Throwable t) {

            }
        });*/
        requestBuild.setRequestInfoMethod(url,body,null);
        requestBuild.invokeRequestMethod(CommonConfig.PUT, new NetworkCallback() {
           @Override
           public void onResponse(NetworkCall networkCall, NetworkResponse response) {
               Log.d("tag", "" + networkCall.isCanceled());
               Log.e("tag", "" + response.toString());
               try{
               UserModel userModel = (UserModel)response.getModelData(UserModel.class.getName());
               text.setText(userModel.getName()+""+userModel.getJob());
           }
        catch(NetworkCommonException e) {
                Log.e("tag", e.getLocalizedMessage());
                Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                Log.d("tag", "class not properly defined");
            }
           }

           @Override
            public void onError(NetworkCall networkCall, Throwable t) {
                Log.d("tag", "" + networkCall.isExecuted());
            }
        });

        }
        catch(NetworkCommonException e) {
            Log.e("tag", e.getLocalizedMessage());
            Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            Log.d("tag", "class not properly defined");
        }

    }


}
