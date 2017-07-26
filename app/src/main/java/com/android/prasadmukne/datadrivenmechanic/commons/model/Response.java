package com.android.prasadmukne.datadrivenmechanic.commons.model;

import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import org.json.JSONObject;


public class Response
{
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String data;
	
	private String message;
	
	private String status;
	
	private JSONObject jsonObject;
	
	public Response(String status,String message,String data)
	{
		this.status=status;
		this.data=data;
		this.message=message;
		jsonObject=new JSONObject();
		try 
		{
			jsonObject.put(AppConstants.RESPONSE_STATUS, status);
			jsonObject.put(AppConstants.RESPONSE_MESSAGE, message);
			jsonObject.put(AppConstants.RESPONSE_DATA, data);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public JSONObject getResonseJson()
	{
		return jsonObject;
	}

	public Response(JSONObject jsonObject)
	{
		this.jsonObject=jsonObject;
		try
		{
			status=jsonObject.getString(AppConstants.RESPONSE_STATUS);
			message=jsonObject.getString(AppConstants.RESPONSE_MESSAGE);
			data=jsonObject.getString(AppConstants.RESPONSE_DATA);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
