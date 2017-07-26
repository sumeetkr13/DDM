package com.android.prasadmukne.datadrivenmechanic.commons.model;

/**
 * Created by prasad.mukne on 7/26/2017.
 */

public class RequestBO
{
	private int _id;

	private String requestString;

	private String filePath;

	public int get_id()
	{
		return _id;
	}

	public void set_id(int _id)
	{
		this._id = _id;
	}

	public String getRequestString()
	{
		return requestString;
	}

	public void setRequestString(String requestString)
	{
		this.requestString = requestString;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	private String status;
}
