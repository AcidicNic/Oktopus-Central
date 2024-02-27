package com.thirtythreelabs.ttsstt;

import java.io.IOException;
import java.util.List;

import com.thirtythreelabs.bluetooth.BluetoothHeadsetUtils;
import com.thirtythreelabs.ttsstt.Tts.OnTtsResults;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class Stt {
	private SpeechRecognizer mSpeechRecognizer;
	private CountDownTimer countDownTimer;
	private Activity mActivity;
	private Context mContext;
	private BluetoothHeadsetUtils mBluetoothHelper;
	
	public interface OnSttResults {
        public abstract void setToast(String myToast);
		public abstract void speak(String string);
		public abstract void sttReady();
		public abstract void sttResults(List<String> data, float[] confidence);
		public abstract boolean isBluethoothOn();
    }


	
	public void startSpeech(Context tempContext, Activity tempActivity){
		mContext = tempContext;
		mActivity = tempActivity;

		// setToast("startSpeech");
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(tempContext);
		mSpeechRecognizer.setRecognitionListener(new listener());
		
		((OnSttResults) mActivity).sttReady();
	}
	
	public void stopStt(){
		if(countDownTimer != null){
			countDownTimer.cancel();
		}
		
		if (mSpeechRecognizer != null) {
			stopRecognizeSpeech();
		}
	}

	
	public void destroyStt(){
		if (mSpeechRecognizer != null) {
        	stopRecognizeSpeech();
        	mSpeechRecognizer.destroy();
        }
	}
	
	
	class listener implements RecognitionListener {
	    public void onReadyForSpeech(Bundle params) {
		   	// setToast("onReadyForSpeech");
		}
	    
	    public void onBeginningOfSpeech() {
	    	countDownTimer.cancel();
		   	// setToast("onBeginningOfSpeech");
		}
	    
	    public void onRmsChanged(float rmsdB) {

		}
	    
	    public void onBufferReceived(byte[] buffer) {
		   	// setToast("onBufferReceived");
		}
	    
	    public void onEndOfSpeech() {
	    	countDownTimer.cancel();
		   	// setToast("onEndOfSpeech");
		}

	    public void onError(int error) {
	    	countDownTimer.cancel();
	    	
	    	
	    	if (error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) {
	    		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_NETWORK_TIMEOUT (error " +  error + ")");
		    }
	    	
	    	if (error == SpeechRecognizer.ERROR_NETWORK) {
	    		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_NETWORK_TIMEOUT (error " +  error + ")");
		    }
	    	
	    	if (error == SpeechRecognizer.ERROR_AUDIO) {
	    		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_AUDIO (error " +  error + ")");
	        }
	    	
	    	if (error == SpeechRecognizer.ERROR_SERVER) {
	    		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_SERVER (error " +  error + ")");
	        }
	    	
	    	if (error == SpeechRecognizer.ERROR_CLIENT) {
	    		countDownTimer.cancel();
	    		// startRecognizeSpeech();
	    		
	    		// ((OnSttResults) mActivity).setToast("ERROR_CLIENT (error " +  error + ")");
	        }
	    	
	    	if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
	    		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_SPEECH_TIMEOUT (error " +  error + ")");
	        }
	    	
	    	if (error == SpeechRecognizer.ERROR_NO_MATCH) {
	    		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_NO_MATCH (error " +  error + ")");
		    }
		   	
		   	if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
		   		
	    		
		   		((OnSttResults) mActivity).setToast("ERROR_RECOGNIZER_BUSY (error " +  error + ")");
		    }
		   	
		   	if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
		   		countDownTimer.cancel();
	    		startRecognizeSpeech();
	    		
	    		((OnSttResults) mActivity).setToast("ERROR_INSUFFICIENT_PERMISSIONS (error " +  error + ")");
		   	}
		   	
			// mText.setText("error " + error);
			
		}
	    
	    public void onResults(Bundle results) {
	    	countDownTimer.cancel();
		   				
			List<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
			String dataConfidence = "";
			
			for (float number : confidence) {
				dataConfidence = dataConfidence + String.valueOf(number) + ", ";
			}
			
			if(data.isEmpty()){
				((OnSttResults) mActivity).setToast("No reults: data.isEmpty()");
	    	}else{
	    		
	    		((OnSttResults) mActivity).setToast(data.toString() + " / " + dataConfidence);
	    		((OnSttResults) mActivity).sttResults(data, confidence);

	    	}
		}
	    


		public void onPartialResults(Bundle partialResults) {
	    	countDownTimer.cancel();
	    	((OnSttResults) mActivity).setToast("onPartialResults");
		}
	    
	    public void onEvent(int eventType, Bundle params) {
	    	((OnSttResults) mActivity).setToast("Stt onEvent");
		}
	}
	
	
	public void startRecognizeSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mActivity.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10); 
        
        mSpeechRecognizer.startListening(intent);
        
        /*
        if (((OnSttResults) mActivity).isBluethoothOn()){
       	 try {
				playSound(mActivity);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        */
        
        
        countDownTimer = new CountDownTimer(4500, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            	//mTextTime.setText(String.valueOf(Math.round(millisUntilFinished/1000)));
            }

            public void onFinish() {
            	stopRecognizeSpeech();
            	startRecognizeSpeech();
            }
            
         }.start();
      
    }

	
    public void stopRecognizeSpeech(){
    	mSpeechRecognizer.stopListening();
    	mSpeechRecognizer.cancel();
    }
    
    
    
    public void playSound(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException,
    IOException {
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setDataSource(context, soundUri);
		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
		    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		    mMediaPlayer.setLooping(false);
		    mMediaPlayer.prepare();
		    mMediaPlayer.start();
		    // mMediaPlayer.setVolume(0.0f, 0.0f);
		}
	}
    
}
