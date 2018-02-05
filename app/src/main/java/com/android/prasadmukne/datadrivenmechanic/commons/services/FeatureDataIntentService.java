package com.android.prasadmukne.datadrivenmechanic.commons.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.android.prasadmukne.datadrivenmechanic.AppApplication;
import com.android.prasadmukne.datadrivenmechanic.commons.database.SQLiteDatabaseManager;
import com.android.prasadmukne.datadrivenmechanic.commons.model.FeatureRequestModel;
import com.android.prasadmukne.datadrivenmechanic.commons.model.RawDataRequestModel;
import com.android.prasadmukne.datadrivenmechanic.feature.FeatgenFFT;
import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prasad.mukne on 7/24/2017.
 */

public class FeatureDataIntentService extends IntentService
{
	public static boolean isFeatureSyncRunning;
	public static boolean isDataSyncRunning;
	private final static int BUFFER_SIZE=2097152;//1048576;
	private String sessionId=null;


	public FeatureDataIntentService()
	{
		super("FeatureDataIntentService");
	}

	public FeatureDataIntentService(String name)
	{
		super(name);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, startId, startId);
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_REDELIVER_INTENT;
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent)
	{
		try
		{
			Log.i("onHandleIntent started", "onHandleIntent started");
			sessionId=intent.getStringExtra(AppConstants.SESSION_ID);
			ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			boolean isMobile = false, isWifi = false;

			if (null != netInfo)
			{
				if (netInfo.getType() == ConnectivityManager.TYPE_WIFI)
				{
					if (netInfo.isConnected() && netInfo.isAvailable())
					{
						isWifi = true;
					}
				}
				if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE)
				{
					if (netInfo.isConnected() && netInfo.isAvailable())
					{
						isMobile = true;

					}
				}


				runDeleteCycle();

				if (isMobile)
				{
					if(!isFeatureSyncRunning)
					{
						//isFeatureSyncRunning = true;
						sendFeatureData();
					}
					if(!isDataSyncRunning)
					{
						//isDataSyncRunning = true;
						sendRawData();
					}
				}
				else if (isWifi)
				{
					if(!isFeatureSyncRunning)
					{
						//isFeatureSyncRunning = true;
						sendFeatureData();
					}
					if(!isDataSyncRunning)
					{
						//isDataSyncRunning = true;
						sendRawData();
					}
				}

			}
			else
			{

				if(null!=sessionId)
				{
					JSONObject jsonObject=new JSONObject();
					jsonObject.put(AppConstants.STATUS,AppConstants.FAILURE);
					jsonObject.put(AppConstants.MESSAGE,AppConstants.NO_INTERNET);
					sendLocalBroadcast(jsonObject, sessionId);
				}
			}





		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void sendFeatureData()
	{
		try
		{
			final ArrayList<FeatureRequestModel> requestArrayList = new ArrayList<FeatureRequestModel>();
			SQLiteDatabaseManager sqLiteDatabaseManager = SQLiteDatabaseManager.getInstance(getApplicationContext());
			Cursor cursor = sqLiteDatabaseManager.search("Select * from " + SQLiteDatabaseManager.REQUEST_TABLE + " where status ='pending';");

			while (cursor.moveToNext())
			{
				try
				{
					String sourceFile=cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_PATH));
					int fileSize=(int) new File(sourceFile).length();
					int originalLoopSize=(fileSize/BUFFER_SIZE)+1;
					System.out.println("Current File name "+sourceFile);

						for(int j=0;j<originalLoopSize;j++)
						{
							System.out.println("originalLoopSize " + (originalLoopSize - 1));
							HashMap<String, ArrayList<Double>> firstHalfHashMap = generateLRChannels(j, sourceFile);

							ArrayList<Double> leftArrayList = firstHalfHashMap.get("left");
							ArrayList<Double> rightArrayList = firstHalfHashMap.get("right");

							HashMap<String, HashMap> hashMap = getFeatureArrays(leftArrayList, rightArrayList);

							if (null != hashMap)
							{
								FeatureRequestModel featureRequestModel = new FeatureRequestModel();
								featureRequestModel.set_id(cursor.getInt(cursor.getColumnIndex(SQLiteDatabaseManager.ROW_ID)));
								featureRequestModel.setUsername(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.USERNAME)));
								featureRequestModel.setRequestString(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.REQUEST)));
								featureRequestModel.setFileName(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_NAME)));
								featureRequestModel.setFilePath(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_PATH)));
								featureRequestModel.setStatus(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.STATUS)));
								featureRequestModel.setExpertUser(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.IS_EXPERT_USER)));
								featureRequestModel.setSystemFaulty(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.IS_SYSTEM_FAULTY)));
								featureRequestModel.setTimeStamp(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.TIMESTAMP)));
								if(j+1==originalLoopSize)
								{
									featureRequestModel.setIsFinal(true);
								}
								else
								{
									featureRequestModel.setIsFinal(false);
								}
								featureRequestModel.setBinnedfreqs((double[]) (hashMap.get("binnedfreqsfinal")).get("binnedfreqs"));
								featureRequestModel.setBinnedfeatFFT((double[][]) (hashMap.get("binnedfeatFFT2Dfinal")).get("binnedfeatFFT"));
								//requestArrayList.add(featureRequestModel);

								sendFeatureToServer(featureRequestModel);
							}
							else
							{
								continue;
							}
						}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			cursor.close();


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void sendFeatureToServer(final FeatureRequestModel featureRequestModel)
	{
		try
		{
			if (VolleyConnectionRequest.isNetworkAvailable(getApplicationContext()))
			{
					final VolleyConnectionRequest volleyConnectionRequest1 = new VolleyConnectionRequest(getApplicationContext(), AppConstants.FEATURE_DATA_SENDING_URL, Method.POST, featureRequestModel.get_id(), new JSONObject(new Gson().toJson(featureRequestModel)), false, new HashMap<String, String>(), new HashMap<String, String>(), Priority.HIGH, new ResponseHandler()
					{
						@Override
						public void onPreExecute()
						{

						}

						@Override
						public void onSuccessfulResponse(JSONObject response, int rowid)
						{
							try
							{
								if (response.getString(AppConstants.STATUS).equals(AppConstants.SUCCESS))
								{
									if(featureRequestModel.getIsFinal())
									{
										//SQLiteDatabaseManager.getInstance(getApplicationContext()).deleteOnRowId(SQLiteDatabaseManager.REQUEST_TABLE, rowid);
										//SQLiteDatabaseManager.getInstance(getApplicationContext()).delete(SQLiteDatabaseManager.RAW_TABLE, "timestamp =",new String[]{featureRequestModel.getTimeStamp()});
										//new File(featureRequestModel.getFilePath()).delete();
										boolean isUpdated=SQLiteDatabaseManager.getInstance(getApplicationContext()).update(SQLiteDatabaseManager.REQUEST_TABLE,new String[]{"uploaded"},rowid,new String[]{SQLiteDatabaseManager.STATUS});
										Log.d("isUpdated ","feature isUpdated "+isUpdated);
									}
								}
								String requestSessionID=featureRequestModel.getTimeStamp()+"_"+featureRequestModel.getUsername();
								if(featureRequestModel.getIsFinal()&& null!=sessionId && requestSessionID.equals(sessionId))
								{
									sendLocalBroadcast(response, featureRequestModel.getTimeStamp());
								}


							}
							catch (Exception e)
							{

								e.printStackTrace();
								Intent intent = new Intent(AppConstants.RESPONSE_RECEIVED);
								intent.putExtra(AppConstants.STATUS, AppConstants.FAILURE);
								intent.putExtra(AppConstants.MESSAGE, "Some error has occurred");
								intent.putExtra("timestamp", featureRequestModel.getTimeStamp());
								LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
							}
						}

						@Override
						public void onFailureResponse(Exception e)
						{
							e.printStackTrace();
							Intent intent = new Intent(AppConstants.RESPONSE_RECEIVED);
							intent.putExtra(AppConstants.STATUS, AppConstants.FAILURE);
							intent.putExtra(AppConstants.MESSAGE, "Some error has occurred");
							intent.putExtra("timestamp", featureRequestModel.getTimeStamp());
							LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
						}
					});
					volleyConnectionRequest1.sendRequest();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void sendLocalBroadcast(JSONObject response, String timestamp)
	{
		try
		{
			Intent intent = new Intent(AppConstants.RESPONSE_RECEIVED);
			intent.putExtra(AppConstants.STATUS, response.getString(AppConstants.STATUS));
			intent.putExtra(AppConstants.MESSAGE, response.getString(AppConstants.MESSAGE));
			intent.putExtra("timestamp", timestamp);
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void sendRawData()
	{
		try
		{
			final ArrayList<RawDataRequestModel> requestArrayList = new ArrayList<RawDataRequestModel>();
			SQLiteDatabaseManager sqLiteDatabaseManager = SQLiteDatabaseManager.getInstance(getApplicationContext());
			Cursor cursor = sqLiteDatabaseManager.search("Select * from " + SQLiteDatabaseManager.RAW_TABLE + " where status ='pending';");
			while (cursor.moveToNext())
			{
				try
				{
					RawDataRequestModel rawDataRequestModel = new RawDataRequestModel();
					rawDataRequestModel.set_id(cursor.getInt(cursor.getColumnIndex(SQLiteDatabaseManager.ROW_ID)));
					rawDataRequestModel.setUsername(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.USERNAME)));
					rawDataRequestModel.setFileName(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_NAME)));
					rawDataRequestModel.setFilePath(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_PATH)));
					rawDataRequestModel.setStatus(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.STATUS)));
					rawDataRequestModel.setTimeStamp(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.TIMESTAMP)));
					requestArrayList.add(rawDataRequestModel);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			cursor.close();

			if (VolleyConnectionRequest.isNetworkAvailable(getApplicationContext()))
			{
				for (int i = 0; i < requestArrayList.size(); i++)
				{
					final int finalI = i;
					Map<String, String> headers = new HashMap<>();
					headers.put(AppConstants.USERNAME, requestArrayList.get(i).getUsername());
					VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppConstants.RAW_DATA_SENDING_URL, new Response.Listener<NetworkResponse>()
					{
						@Override
						public void onResponse(NetworkResponse response)
						{

							try
							{
								String resultResponse = new String(response.data);

								if (new JSONObject(resultResponse).getString(AppConstants.STATUS).equals(AppConstants.SUCCESS))
								{
									/*SQLiteDatabaseManager sqLiteDatabaseManager=SQLiteDatabaseManager.getInstance(getApplicationContext());
									Cursor cursor = sqLiteDatabaseManager.search("Select * from " + SQLiteDatabaseManager.REQUEST_TABLE +" where "+SQLiteDatabaseManager.TIMESTAMP+" = "+requestArrayList.get(finalI).getTimeStamp()+ " ;");
									if(cursor.getCount()==0)
									{
										sqLiteDatabaseManager.deleteOnRowId(SQLiteDatabaseManager.RAW_TABLE, requestArrayList.get(finalI).get_id());
										new File(requestArrayList.get(finalI).getFilePath()).delete();
									}*/
									boolean isUpdated=SQLiteDatabaseManager.getInstance(getApplicationContext()).update(SQLiteDatabaseManager.RAW_TABLE,new String[]{"uploaded"},requestArrayList.get(finalI).get_id(),new String[]{SQLiteDatabaseManager.STATUS});
									Log.d("isUpdated ","raw data isUpdated"+isUpdated);
								}
								else
								{

								}
								if(finalI+1 == requestArrayList.size())
								{
									isDataSyncRunning=false;
								}
								// parse success output
							}
							catch (Exception e)
							{
								e.printStackTrace();
								if(finalI+1 == requestArrayList.size())
								{
									isDataSyncRunning=false;
								}
							}


						}
					}, new Response.ErrorListener()
					{
						@Override
						public void onErrorResponse(VolleyError error)
						{
							error.printStackTrace();
							if(finalI == requestArrayList.size())
							{
								isDataSyncRunning=false;
							}
						}
					}, headers)
					{
						@Override
						protected Map<String, String> getParams()
						{
							Map<String, String> params = new HashMap<>();
							return params;
						}

						@Override
						protected Map<String, DataPart> getByteData()
						{
							Map<String, DataPart> params = new HashMap<>();
							// file name could found file base or direct access from real path
							// for now just get bitmap data from ImageView
							try
							{
								byte data[] = new byte[Integer.valueOf("" + new File(requestArrayList.get(finalI).getFilePath()).length())];
								FileInputStream inputStream = new FileInputStream(requestArrayList.get(finalI).getFilePath());
								inputStream.read(data);
								params.put("file", new DataPart(requestArrayList.get(finalI).getFileName(), data, "audio/x-wav"));
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}

							return params;
						}
					};

					AppApplication.getInstance().addToRequestQueue(multipartRequest, "" + requestArrayList.get(i).get_id());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private HashMap<String, ArrayList<Double>> generateLRChannels(int loopPosition,String sourceFile)
	{
			HashMap<String,ArrayList<Double>> hashMap=new HashMap<String, ArrayList<Double>>();
			try
			{
				ArrayList<Double> leftArrayList = new ArrayList();
				ArrayList<Double> rightArrayList = new ArrayList();
				int lastValue=(loopPosition*BUFFER_SIZE);

				System.out.println("current loop= "+loopPosition);
				byte data[] = new byte[BUFFER_SIZE];
				FileInputStream inputStream = new FileInputStream(sourceFile);
				inputStream.skip(lastValue);
				int readSize = inputStream.read(data, 0, BUFFER_SIZE);

				for(int i = 0; i < readSize/2; i = i + 2)
				{
					leftArrayList.add(Double.valueOf(Math.abs(data[2*i])));
					leftArrayList.add(Double.valueOf(Math.abs(data[2*i+1])));

					rightArrayList.add(Double.valueOf(Math.abs(data[2*i+2])));
					rightArrayList.add(Double.valueOf(Math.abs(data[2*i+3])));
				}
				inputStream.close();
				System.gc();

				hashMap.put("left", leftArrayList);
				hashMap.put("right", rightArrayList);

			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return hashMap;


	}

	private HashMap<String, HashMap> getFeatureArrays(ArrayList myList1, ArrayList myList2)
	{
		HashMap<String, HashMap> featuresHashMap = new HashMap<>();

		/*---------------------------------------------------------*/
		double[] binnedfreqsfinal = null;
		double[][] binnedfeatFFT2Dfinal = null;
		double[][] featfinal=null;

		try
		{
			// General variable declaration
			int Fs = 48000; // Sampling frequency
			int centerFs = Fs/2; // For centering FFT
			int sampleLength = 131072; //Length of a sample should be a power of 2 and >= 2.5s
			double sampleDuration = Math.round(100.0*sampleLength/Fs)/100.0; //Time, in seconds, of sample to be used for fingerprint analysis
			System.out.println("Digital signal of " + sampleDuration + "s @ 48kHz is of length = " + sampleLength);


			// Frequency feature parameters
			double lowerWindowBound = 0; // Set the lower bound for the region of interest, in Hz
			double upperWindowBound = 10000; // Set the upper bound for the region of interest, in Hz
			double binWidthHz = 10; // Set the uniform window size, in Hz
			double startedge = 1.0; //duration of the audio to leave in the start in sec
			double endedge = 1.0; //duration of the audio to leave in the end in sec
			int startedgeLength = (int) Math.round(Fs*startedge);
			int endedgeLength = (int) Math.round(Fs*endedge);

			// histBinRanges = (lowerWindowBound:binWidthHz:upperWindowBound); % Works well at sample duration 0.5s

			double freqbin = (double) Fs/sampleLength; // Spacing between FFT values in terms of Hz
			// Bin size over which averaging needs to happen for calculating binned-avg FFT feature
			int bindel = (int) Math.floor((sampleLength*binWidthHz)/Fs);

			// One-sided frequency and FFT arrays
			double[] freqs = new double[(int) (sampleLength/2)];
			// Binned mid-point frequency
			double[] binnedfreqs = new double[(int) (sampleLength/(bindel*2))];

			// Generating the frequency array
			for(int i = 0; i < freqs.length; i++) {
				freqs[i] = (double) (1.0*Fs*i)/(2*freqs.length);
				// System.out.println(freqs[i]);
			}
			System.out.println("Maximum frequency = " + freqs[(freqs.length-1)]);

			System.out.println("Current directory is " + System.getProperty("user.dir"));

			/*
			// Reading the text files, might need a different IO
			// Need to make sure that the input files have at  least 5 seconds of data
			String filename1 = "leftChannel.txt";
			String filename2 = "rightChannel.txt";

			// Read the data from the text file and store the data in an Array List of double values
			ArrayList<Double> myList1 = readAuidoChannelFile(filename1);
			ArrayList<Double> myList2 = readAuidoChannelFile(filename2);
			*/

			double[] monoAudioSig = FeatgenFFT.convertoMono(myList1, myList2, sampleLength, startedgeLength, endedgeLength);
			System.out.println("The audio data is a digital signal of length " + monoAudioSig.length);

			double audiosigarr[][] = FeatgenFFT.convert1Dto2DArray(monoAudioSig, sampleLength);
			System.out.println("Size of the 2D signal array = "+ audiosigarr.length +"*"+ audiosigarr[0].length);


			double featFFT2D[][] = FeatgenFFT.FFT2DArr(audiosigarr);
			System.out.println("Size of the FFT 2D array = "+ featFFT2D.length +"*"+ featFFT2D[0].length);

			double audFeat[][] = FeatgenFFT.audFeat(audiosigarr);
			System.out.println("The audio features of the digital signal array is of size " + audFeat.length + "*" + audFeat[0].length);

			System.out.println("Length of the frequency array = " + freqs.length);
			System.out.println("Length of the binned frequency array = " + binnedfreqs.length);

			// Sub-selecting half of the array length representing one sided spectrum
			double featFFT2Dos[][] = new double[freqs.length][featFFT2D[0].length];
			for(int j = 0; j < featFFT2D[0].length; j++) {
				for(int i = 0; i < freqs.length; i++) {
					featFFT2Dos[i][j] = featFFT2D[i][j] ;
				}
			}

			System.out.println("Size of the onesided-FFT 2D array = " + featFFT2Dos.length + "*" + featFFT2Dos[0].length);


			// Perform binning of frequency array and binned-averaging of FFT
			int halfdel = (int) (bindel/2);
			System.out.println("Center of bin delta = " + halfdel);

			double binnedfeatFFT2D[][]  = new double[binnedfreqs.length][featFFT2Dos[0].length];

			for(int l = 0; l < featFFT2Dos[0].length; l++) {
				int k = 0;
				for(int i = 0; i < binnedfreqs.length; i++) {
					if(k+halfdel < freqs.length) {
						binnedfreqs[i] = freqs[k + halfdel];
						double val = 0;
						for(int j = 0; j < bindel; j++) {
							if(k +  j < featFFT2Dos.length) {
								val = val + featFFT2Dos[k+j][l];
							} else {
								j = bindel;
							}
						}
						val = val/bindel;
						binnedfeatFFT2D[i][l] = val;
						k = k + bindel;
					}
					// System.out.println("At frequency = " + binnedfreqs[i] + "Hz, feature value = " + binnedfeatFFT2D[i][l] );
				}
			}


			System.out.println("Size of the binned-frequencies = " + binnedfreqs.length);
			System.out.println("Size of the binned-onesided-FFT 2D array of the input signal = " + binnedfeatFFT2D.length + "*" + binnedfeatFFT2D[0].length);

			System.out.println("Maximum binned frequency = " + binnedfreqs[(binnedfreqs.length-1)]);

			int maxind = FeatgenFFT.maxiArr(binnedfreqs, upperWindowBound);
			System.out.println("Length of the upper bounded binned frequency array = " + (maxind+1));
			System.out.println("Max frequency = " + binnedfreqs[maxind]);


			// Final output of the code as a frequency 1D array and a feature 2D array
			binnedfreqsfinal = new double[(maxind+1)];
			binnedfeatFFT2Dfinal = new double[(maxind+1)][binnedfeatFFT2D[0].length];

			for(int i = 0; i <= maxind; i++) {
				binnedfreqsfinal[i] = binnedfreqs[i];
			}
			System.out.println("Max frequency = " + binnedfreqsfinal[(binnedfreqsfinal.length-1)]);

			for(int j = 0; j < binnedfeatFFT2D[0].length; j++) {
				for(int i = 0; i <= maxind; i++) {
					binnedfeatFFT2Dfinal[i][j] = binnedfeatFFT2D[i][j];
				}
			}

			System.out.println("Length of the final frequency array = " + binnedfreqsfinal.length + " with the max frequency = " + binnedfreqsfinal[(maxind-1)]);
			System.out.println("Size of the binned-onesided-fequencycapped-FFT 2D array of the input signal = " + binnedfeatFFT2Dfinal.length + "*" + binnedfeatFFT2Dfinal[0].length);


			featfinal = new double[(binnedfeatFFT2Dfinal.length+3)][binnedfeatFFT2Dfinal[0].length];

			for(int j = 0; j < binnedfeatFFT2Dfinal[0].length; j++) {
				for(int i = 0; i < binnedfeatFFT2Dfinal.length; i++) {
					featfinal[i][j] = binnedfeatFFT2Dfinal[i][j];
				}
				featfinal[binnedfeatFFT2Dfinal.length][j] = audFeat[0][j];
				featfinal[binnedfeatFFT2Dfinal.length+1][j] = audFeat[1][j];
				featfinal[binnedfeatFFT2Dfinal.length+2][j] = audFeat[2][j];
			}

			System.out.println("Size of the binned-onesided-fequencycapped-FFT 2D array + audio features of the input signal = " + featfinal.length + "*" + featfinal[0].length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

/*---------------------------------------------------------*/

		HashMap<String, double[]> binnedfreqsfinalHashMap = new HashMap();
		binnedfreqsfinalHashMap.put("binnedfreqs", binnedfreqsfinal);
		HashMap<String, double[][]> binnedfeatFFT2DfinalHashMap = new HashMap();
		binnedfeatFFT2DfinalHashMap.put("binnedfeatFFT", featfinal);
		featuresHashMap.put("binnedfreqsfinal", binnedfreqsfinalHashMap);
		featuresHashMap.put("binnedfeatFFT2Dfinal", binnedfeatFFT2DfinalHashMap);

		return featuresHashMap;
	}

	private void runDeleteCycle()
	{
		try
		{
			Cursor cursor=SQLiteDatabaseManager.getInstance(getApplicationContext()).rawQuery("select * from RequestTable where status='uploaded';",null);
			while(cursor.moveToNext())
			{
				String timestamp=cursor.getString(cursor.getColumnIndex("timestamp"));
				//SQLiteDatabaseManager.getInstance(getApplicationContext()).rawQuery("delete from RawData where status='uploaded' and timestamp="+timestamp+";",null);
				boolean isRawDataDeleted=SQLiteDatabaseManager.getInstance(getApplicationContext()).delete("RawTable","status= 'uploaded' and timestamp= "+timestamp,null);
				if(isRawDataDeleted)
				{
					new File(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_PATH))).delete();
					boolean isFeatureDeleted=SQLiteDatabaseManager.getInstance(getApplicationContext()).delete("RequestTable","status= 'uploaded' and timestamp= "+timestamp,null);
					//SQLiteDatabaseManager.getInstance(getApplicationContext()).rawQuery("delete from RequestTable where status='uploaded' and timestamp="+timestamp+";",null);
				}
			}
			cursor.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
