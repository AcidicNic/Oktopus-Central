package com.thirtythreelabs.ttsstt;


import java.util.HashMap;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

import com.thirtythreelabs.bluetooth.BluetoothHeadsetUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Tts implements OnInitListener{
	
	private TextToSpeech mTtextToSpeech;
	private Activity mActivity;
	
	
	public interface OnTtsResults {
        public abstract void setToast(String myToast);
        public abstract void ttsReady();
		public abstract void speak(String string);
		public abstract void ttsOnDone(String pUtteranceId);
		public abstract boolean isBluethoothOn();
    }
	
	
	public void startTts(Context tempContext, Activity tempActivity){
		mActivity = tempActivity;
		mTtextToSpeech = new TextToSpeech(tempContext, this);
		
		
		
	}
	
	public void resumeTts(){
		
	}
	
	public void pauseTts(){
		
	}
	
	public void stopTts(){
		if(mTtextToSpeech.isSpeaking()){
			mTtextToSpeech.stop();
			
		}
	}
	
	public void destroyTts(){
		if (mTtextToSpeech != null) {
    		mTtextToSpeech.stop();
    		mTtextToSpeech.shutdown();
    	}
	}
	
	
	public boolean isSpeaking(){
		boolean tempIsSpeaking = false;
		if(mTtextToSpeech.isSpeaking()){
			tempIsSpeaking = true;
		}
		return tempIsSpeaking;
	}
	
	private class BluetoothHelper extends BluetoothHeadsetUtils
	{
	    public BluetoothHelper(Context context)
	    {
	        super(context);
	    }

	    @Override
	    public void onScoAudioDisconnected()
	    {
	    	((OnTtsResults) mActivity).setToast("onScoAudioDisconnected");
	    }

	    @Override
	    public void onScoAudioConnected()
	    {           
	    	((OnTtsResults) mActivity).setToast("onScoAudioConnected");
	    }

	    @Override
	    public void onHeadsetDisconnected()
	    {
	    	((OnTtsResults) mActivity).setToast("onScoAudioDisconnected");
	    }

	    @Override
	    public void onHeadsetConnected()
	    {
	    	((OnTtsResults) mActivity).setToast("onHeadsetConnected");
	    }
	}
	
	@Override
	public void onInit(int status) {
		
		// ((OnTtsResults) mActivity).setToast("Init");
		if (status == TextToSpeech.SUCCESS) {
			int result;
			result = mTtextToSpeech.setLanguage(new Locale("spa", "ESP"));
			//result = mTtextToSpeech.setLanguage(Locale.US);

			//if (result == TextToSpeech.LANG_MISSING_DATA
			//		|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				
			//	((OnTtsResults) mActivity).setToast("Language is not supported");
			
			//}else{
				
				setTtsListener();
				((OnTtsResults) mActivity).ttsReady();
				
				// ((OnTtsResults) mainActivity).setToast("Listener");
			//}


		} else {
			// textToSpeech: Initilization Failed
			
			((OnTtsResults) mActivity).setToast("Error");
		}
		
	}


	@TargetApi(15)
	private void setTtsListener() {
        final Tts callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult = mTtextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String pUtteranceId) {
                    callWithResult.onDone(pUtteranceId);
                }

				@Override
				public void onError(String arg0) {
					// TODO Auto-generated method stub
					//mainActivity.setToast("error");
					
				}

				@Override
				public void onStart(String utteranceId) {
					// TODO Auto-generated method stub
					//mainActivity.setToast("start");
					
				}

            });
            if (listenerResult != TextToSpeech.SUCCESS) {
                // Log.e(TAG, "failed to add utterance progress listener");
            	
            	((OnTtsResults) mActivity).setToast("error listener");
            }
        }
    }
	
	
	private void onDone(final String pUtteranceId) {
		mActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				// ((OnTtsResults) mainActivity).setToast("DONE");
				
				((OnTtsResults) mActivity).ttsOnDone(pUtteranceId);
			}
		 });
	}
	


	
	
	public void speakOut(String textOut, String utteranceId) {
		
		// ((OnTtsResults) mActivity).setToast("Speak");
		
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		String voiceSpeed = sharedPrefs.getString("voiceSpeed", "2");
		
		if(voiceSpeed.contains("1")){
			mTtextToSpeech.setSpeechRate(0.7f);
		} else if(voiceSpeed.contains("2")){
			mTtextToSpeech.setSpeechRate(1.0f);
		} else if(voiceSpeed.contains("3")){
			mTtextToSpeech.setSpeechRate(1.2f);
		} else if(voiceSpeed.contains("4")){
			mTtextToSpeech.setSpeechRate(1.5f);
		} else if(voiceSpeed.contains("5")){
			mTtextToSpeech.setSpeechRate(1.8f);
		} 
		
		
		mTtextToSpeech.setPitch(1.0f);
		
		HashMap<String, String> myHash = new HashMap<String, String>();		
		myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
		
		if (((OnTtsResults) mActivity).isBluethoothOn())
	    {
			myHash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	            String.valueOf(AudioManager.STREAM_VOICE_CALL));
	    }
		
		mTtextToSpeech.speak(textOut, TextToSpeech.QUEUE_FLUSH, myHash);
	}
}
