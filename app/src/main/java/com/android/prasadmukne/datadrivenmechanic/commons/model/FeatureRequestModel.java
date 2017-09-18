package com.android.prasadmukne.datadrivenmechanic.commons.model;

/**
 * Created by prasad.mukne on 7/26/2017.
 */

public class FeatureRequestModel
{

	private int _id;

	private String requestString;

	private String fileName;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	private String username;

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	private String filePath;

	public double[] getBinnedfreqs()
	{
		return binnedfreqs;
	}

	public void setBinnedfreqs(double[] binnedfreqs)
	{
		this.binnedfreqs = binnedfreqs;
	}

	public double[][] getBinnedfeatFFT()
	{
		return binnedfeatFFT;
	}

	public void setBinnedfeatFFT(double[][] binnedfeatFFT)
	{
		this.binnedfeatFFT = binnedfeatFFT;
	}

	private double[] binnedfreqs;

	private double[][] binnedfeatFFT;

	public String getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	private String timeStamp;

	public String getExpertUser()
	{
		return expertUser;
	}

	public void setExpertUser(String expertUser)
	{
		this.expertUser = expertUser;
	}

	public String getSystemFaulty()
	{
		return systemFaulty;
	}

	public void setSystemFaulty(String systemFaulty)
	{
		this.systemFaulty = systemFaulty;
	}

	private String expertUser;

	private String systemFaulty;

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

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
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

	public boolean getIsFinal()
	{
		return isFinal;
	}

	public void setIsFinal(boolean aFinal)
	{
		isFinal = aFinal;
	}

	private boolean isFinal;
}
