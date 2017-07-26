package com.android.prasadmukne.datadrivenmechanic.commons.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.android.prasadmukne.datadrivenmechanic.AppApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

/**
 * Created by prasad.mukne on 2/16/2017.
 */
public class VolleyConnectionRequest
{
    private String URL;
    private int method;
    private JSONObject jsonRequest;
    private HashMap<String, String> headers;
    private HashMap<String, String> postParams;
    private ResponseHandler responseHandler;
    private int uniqueTag;
    private Request.Priority priority;
    private Context context;
    private boolean applyDefaultHeaders;

    public int getUniqueTag()
    {
        return uniqueTag;
    }

    public VolleyConnectionRequest(Context context,String URL,int method,int uniqueTag,JSONObject jsonRequest,boolean applyDefaultHeaders,HashMap<String, String> headers,HashMap<String, String> postParams,Request.Priority priority,ResponseHandler responseHandler)
    {
        this.URL=URL;
        this.method=method;
        this.uniqueTag=uniqueTag;
        this.jsonRequest=jsonRequest;
        this.headers=headers;
        this.postParams=postParams;
        this.priority=priority;
        this.responseHandler=responseHandler;
        this.context=context;
        this.applyDefaultHeaders=applyDefaultHeaders;

        responseHandler.onPreExecute();
        //sendRequest();
    }

    public void sendRequest()
    {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(method,
                    URL, jsonRequest,
                    new Response.Listener<JSONObject>()
                    {

                        @Override
                        public void onResponse(JSONObject response)
                        {
                            Log.d("tag1", response.toString());
                            responseHandler.onSuccessfulResponse(response,uniqueTag);
                        }
                    },
                    new Response.ErrorListener()
                    {

                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                             VolleyLog.d("tag", "Error: " + error.getMessage());
                            responseHandler.onFailureResponse(error);

                        }
                    })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    if(applyDefaultHeaders)
                    {
                        headers.putAll(getDefaultHeaders());
                    }
                    return headers;
                }

                @Override
                protected Map<String, String> getParams()
                {
                    return postParams;
                }

                @Override
                public Priority getPriority()
                {
                    return priority;
                }

            };

            AppApplication.getInstance().addToRequestQueue(jsonObjReq, ""+uniqueTag);
    }


    public void cancelRequest(String uniqueTag)
    {
        AppApplication.getInstance().getRequestQueue().cancelAll(uniqueTag);
    }

    protected HashMap<String, String>  getDefaultHeaders()
    {
        HashMap<String, String> headers =new HashMap<>();
       /* headers.put(ZygrateSecurityConstants.NONCE.getValue(), System.currentTimeMillis()
                + "");
        headers.put(ZygrateSecurityConstants.ACCESS_TOKEN.getValue(),
                AccessIntegrationKeys.getAccessToken());
        headers.put(ZygrateSecurityConstants.APPLICATION_ID.getValue(),
                DataService.getApplicationID(context));
        headers.put(ZygrateSecurityConstants.USERNAME.getValue(), DataService.userEmailId);*/
        return headers;
    }

/*    protected DefaultHttpClient setTimeOut(DefaultHttpClient httpClient)
    {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 25000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 35000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        httpClient.setParams(httpParameters);
        return httpClient;
    }*/

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
