package com.android.prasadmukne.datadrivenmechanic.fragments;

import android.app.Dialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaExtractor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import com.android.prasadmukne.datadrivenmechanic.R;
import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import com.android.prasadmukne.datadrivenmechanic.utils.SharedPreferencesUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by prasad.mukne on 7/14/2017.
 */

public class HomeScreenFragment extends Fragment
{

	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "DDMAudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	//private static final int RECORDER_SAMPLE_RATE = 44100;
	private static final int RECORDER_SAMPLE_RATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private AudioRecord recorder = null;
	private int bufferSize = 0;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	private View view;
	private CheckBox isSystemFaultyCheckBox;
	private Chronometer chronometer;
	String lastFileName="";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		view = inflater.inflate(R.layout.fragment_home_screen, container, false);
		setRetainInstance(false);
		try
		{
			((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home);
			chronometer=(Chronometer) view.findViewById(R.id.chronometer);
			isSystemFaultyCheckBox=(CheckBox) view.findViewById(R.id.isSystemFaultyCheckBox);
			isSystemFaultyCheckBox.setVisibility(View.INVISIBLE);

			isSystemFaultyCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					Log.d("isChecked","isChecked "+isChecked);
				}
			});

			setButtonHandlers();
			enableButtons(false);

			bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
			//initialiseAndSetUI(rootView);
			SharedPreferencesUtility sharedPreferencesUtility=SharedPreferencesUtility.getSharedPreferencesUtility(getActivity());
			String userType=sharedPreferencesUtility.getString(AppConstants.USER_TYPE);
			if(userType.equals(""))
			{
				showUserTypeDialog();
			}
			else
			{
				if(userType.equals(AppConstants.NORMAL_USER))
				{
					isSystemFaultyCheckBox.setVisibility(View.INVISIBLE);
				}
				else if(userType.equals(AppConstants.EXPER_TUSER))
				{
					isSystemFaultyCheckBox.setVisibility(View.VISIBLE);
				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		isSystemFaultyCheckBox.setChecked(false);
		return view;
	}

	private void setButtonHandlers()
	{
		((Button) view.findViewById(R.id.btnStart)).setOnClickListener(btnClick);
		((Button) view.findViewById(R.id.btnStop)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable)
	{
		((Button) view.findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording)
	{
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnStop, isRecording);
	}

	private String getFilename()
	{
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists())
		{
			file.mkdirs();
		}
		lastFileName=(file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
		return lastFileName;
	}

	private String getTempFilename()
	{
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists())
		{
			file.mkdirs();
		}

		File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
		{
			tempFile.delete();
		}

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	private void startRecording()
	{
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

		int i = recorder.getState();
		if (i == 1)
		{
			recorder.startRecording();
		}

		isRecording = true;

		recordingThread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");

		recordingThread.start();
	}

	private void writeAudioDataToFile()
	{
		try
		{
			byte data[] = new byte[bufferSize];
			String filename = getTempFilename();
			FileOutputStream os = null;

			os = new FileOutputStream(filename);

			int read = 0;

			if (null != os)
			{
				while (isRecording)
				{
					read = recorder.read(data, 0, bufferSize);

					if (AudioRecord.ERROR_INVALID_OPERATION != read)
					{
						os.write(data);
					}
				}
				os.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void stopRecording()
	{
		chronometer.stop();

		if (null != recorder)
		{
			isRecording = false;

			int i = recorder.getState();
			if (i == 1)
			{
				recorder.stop();
			}
			recorder.release();

			recorder = null;
			recordingThread = null;
		}

		copyWaveFile(getTempFilename(), getFilename());
		deleteTempFile();
		//generateTempOutputFiles();
	}

	private void deleteTempFile()
	{
		File file = new File(getTempFilename());

		file.delete();
	}

	private void copyWaveFile(String inFilename, String outFilename)
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLE_RATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLE_RATE * channels / 8;

		byte[] data = new byte[bufferSize];

		try
		{
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			System.out.print("File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

			while (in.read(data) != -1)
			{
				out.write(data);
			}

			in.close();
			out.close();

			MediaExtractor mex = new MediaExtractor();

			System.out.println("inFilename=" + inFilename + " outFilename" + outFilename);

			mex.setDataSource(outFilename);// the adresss location of the sound on sdcard.

			/*MediaFormat mf = mex.getTrackFormat(0);

			int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
			int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
			int channelCount = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

			System.out.println("bitRate=" + bitRate);
			System.out.println("sampleRate=" + sampleRate);
			System.out.println("channelCount=" + channelCount);
*/
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException
	{

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	private View.OnClickListener btnClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.btnStart:
				{
					System.out.print("Start Recording");

					enableButtons(true);
					startRecording();

					break;
				}
				case R.id.btnStop:
				{
					System.out.print("Start Recording");

					enableButtons(false);
					stopRecording();

					break;
				}
			}
		}
	};

	public void showUserTypeDialog()
	{
		final Dialog fingerPrintDialog = new Dialog(new ContextThemeWrapper(getActivity(), R.style.BasicAppBaseTheme));
		fingerPrintDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_type,null);
		fingerPrintDialog.setContentView(dialogView);
		fingerPrintDialog.setCancelable(false);
		final RadioButton normalUserRadioButton= (RadioButton) dialogView.findViewById(R.id.normalUserRadioButton);
		final RadioButton expertUserRadioButton= (RadioButton) dialogView.findViewById(R.id.expertUserRadioButton);
		//TextView usernameDialogTitle = (TextView) fingerPrintDialog.findViewById(R.id.usenameDialogTitle);
		//SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getSharedPreferencesUtility(getActivity());
		//usernameDialogTitle.setText(sharedPreferencesUtility.getString(AppConstants.FINGERPRINT_USER_EMAIL));
		Button dialogButton = (Button) fingerPrintDialog.findViewById(R.id.doneButton);
		dialogButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				fingerPrintDialog.dismiss();
				storeAndRefreshUIWRTUserType(normalUserRadioButton.isChecked(),expertUserRadioButton.isChecked());
			}
		});
		;
		fingerPrintDialog.show();
	}

	public void storeAndRefreshUIWRTUserType(boolean isNormalUserRadioButtonChecked,boolean isExpertUserRadioButtonChecked)
	{
		isSystemFaultyCheckBox.setChecked(false);
		SharedPreferencesUtility sharedPreferencesUtility=SharedPreferencesUtility.getSharedPreferencesUtility(getActivity());
		if(isNormalUserRadioButtonChecked)
		{
			sharedPreferencesUtility.putString(AppConstants.USER_TYPE,AppConstants.NORMAL_USER);
			isSystemFaultyCheckBox.setVisibility(View.INVISIBLE);
		}
		else if(isExpertUserRadioButtonChecked)
		{
			sharedPreferencesUtility.putString(AppConstants.USER_TYPE,AppConstants.EXPER_TUSER);
			isSystemFaultyCheckBox.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(new Bundle());

	}

	public void generateTempOutputFiles()
	{
		try
		{
			String filepath = Environment.getExternalStorageDirectory().getPath();
			File file = new File(filepath, AUDIO_RECORDER_FOLDER);

			if (!file.exists())
			{
				file.mkdirs();
			}

			File tempFile1 = new File(filepath, "rightChannel.txt");

			if (tempFile1.exists())
			{
				tempFile1.delete();
			}

			File tempFile2 = new File(filepath, "leftChannel.txt");

			if (tempFile2.exists())
			{
				tempFile2.delete();
			}


			File leftFile = new File(file.getAbsolutePath() + "/" +"leftChannel.txt");

			File rightFile = new File(file.getAbsolutePath() + "/" +"rightChannel.txt");

			byte data[] = new byte[2048];

			FileInputStream inputStream = new FileInputStream(lastFileName);

			if (null != inputStream)
			{
				while (inputStream.read(data)!=-1)
				{
					FileOutputStream leftFileOutputStream=new FileOutputStream(leftFile,true);
					FileOutputStream rightFileOutputStream=new FileOutputStream(rightFile,true);
					PrintStream printStream1 = new PrintStream(leftFileOutputStream);
					PrintStream printStream2 = new PrintStream(rightFileOutputStream);
					/*//for(int i=0;i<data.length/2;i++)
					for(int i=0;i<data.length-1;i=i+2)
					{
						//leftFileOutputStream.write(Math.abs(data[i*2]));
						printStream1.print(","+Math.abs(data[i*2]));
						//rightFileOutputStream.write(Math.abs(data[i*2+1]));
						printStream2.print(","+Math.abs(data[i*2+1]));
						//printStream1.print(","+Math.abs(data[i]));
						//printStream2.print(","+Math.abs(data[i+1]));
						Log.d("data ","data "+Math.abs(data[i+1]));
					}*/
					for(int i=0;i<data.length-3;i=i+4)
					{
						printStream1.print(","+Math.abs(data[i]));
						printStream1.print(","+Math.abs(data[i+1]));
						printStream2.print(","+Math.abs(data[i+2]));
						printStream2.print(","+Math.abs(data[i+3]));
						//Log.d("data ","data "+Math.abs(data[i+1]));
					}
					leftFileOutputStream.close();
					rightFileOutputStream.close();
				}
				inputStream.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
