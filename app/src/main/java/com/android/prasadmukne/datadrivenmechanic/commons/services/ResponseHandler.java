package com.android.prasadmukne.datadrivenmechanic.commons.services;

import org.json.JSONObject;

/**
 * Created by prasad.mukne on 2/16/2017.
 */
public abstract class ResponseHandler
{
    public abstract void onPreExecute();

    public abstract void onSuccessfulResponse(JSONObject response);

    public abstract void onFailureResponse(Exception e);

}
