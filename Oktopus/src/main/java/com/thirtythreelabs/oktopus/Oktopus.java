package com.thirtythreelabs.oktopus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import com.thirtythreelabs.flowmodel.Command;
import com.thirtythreelabs.flowmodel.Data;
import com.thirtythreelabs.flowmodel.DataModule;
import com.thirtythreelabs.flowmodel.Flow;
import com.thirtythreelabs.flowmodel.FlowModule;
import com.thirtythreelabs.flowmodel.Outcome;
import com.thirtythreelabs.flowmodel.Phrase;
import com.thirtythreelabs.flowmodel.Utterance;
import com.thirtythreelabs.ttsstt.Stt;
import com.thirtythreelabs.ttsstt.Tts;
import com.thirtythreelabs.util.readXmlResource;

public class Oktopus extends Activity implements OnClickListener, Stt.OnSttResults, Tts.OnTtsResults {
	
	public static Flow generalFlow = new Flow();
	private Stt stt = new Stt();
	private boolean sttReady = false;
	private Tts tts = new Tts();
	private boolean ttsReady = false;
	
	private Button mButton;
	
	public interface OnSttResults {
        // public abstract void onFinishEditDialog(String inputText, String labelText);
        public abstract void setToast(String toSay);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_xml);
		
		mButton = (Button) findViewById(R.id.button1);
		mButton.setOnClickListener(this);
		
		stt.startSpeech(this, this);
		// stt.startRecognizeSpeech();
		
		tts.startTts(this, this);
		
		File xmlFile = new File("/sdcard/download/test/example.xml");
		
		Serializer serializer = new Persister();
		
		Data data = new Data();
		data.setAction("getOrders");
		
		Outcome outcome = new Outcome();
		outcome.setModType("module");
		outcome.setGotoWhere("nextOrder");

		
		data.outcomeList.add(outcome);
		
		Flow flow = new Flow();
		DataModule module1 = new DataModule();
		module1.setName("nextOrder");
		module1.setStart("true");
		module1.setData(data);
		
		Phrase phrase1 = new Phrase();
		phrase1.setType("met");
		phrase1.setSpeed("1.0");
		phrase1.setLine("readOrderId");
		
		Phrase phrase2 = new Phrase();
		phrase2.setType("met");
		phrase2.setSpeed("1.0");
		phrase2.setLine("readOrderId");
		
		Utterance utterance1 = new Utterance();
		utterance1.setLine("pronto");
		
		
		Utterance utterance2 = new Utterance();
		utterance2.setLine("codigo");
		
		Outcome outcome2 = new Outcome();
		outcome2.setModType("mod");
		outcome2.setGotoWhere("nextItem");
		
		
		Phrase phrase3 = new Phrase();
		phrase3.setType("met");
		phrase3.setLine("readOrderCode");
		
		
		Outcome outcome3 = new Outcome();
		outcome3.setModType("say");
		outcome3.say.add(phrase3);
		
		
		Command command1 = new Command("va al prox item");

		command1.inputList.add(utterance1);
		command1.outcomeList.add(outcome2);
		
		
		Command command2 = new Command();
		command2.setHelp("va al prox item");
		command2.inputList.add(utterance2);
		command2.outcomeList.add(outcome3);
		
		
		FlowModule module2 = new FlowModule();
		module2.setName("Order");
		
		module2.listenTo.add(command1);
		module2.listenTo.add(command2);
		
		module2.say.add(phrase1);
		module2.say.add(phrase2);
		
		
		
		
		
		
		
		flow.flowModules.add(module1);
		flow.flowModules.add(module2);
		

		//startTts(this, this);
	

		try {
			serializer.write(flow, xmlFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Serializer serializer2 = new Persister();
		// File source = new File("/sdcard/download/Oktopus/Orders.xml");

		try {
			
			generalFlow = serializer2.read(Flow.class, readXmlResource.readXml(this, R.raw.orders));
			String algo = ((DataModule) generalFlow.flowModules.get(0)).getData().getAction();
			
			Toast.makeText(this, algo, Toast.LENGTH_LONG).show();
			
			if(generalFlow.equals(flow)){
				Toast.makeText(this, "true", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "false", Toast.LENGTH_LONG).show();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
    public void onDestroy() {
		stt.destroyStt();
		tts.destroyTts();
	}

	
	public void setToast(String myToast){
		Toast.makeText(this, myToast, Toast.LENGTH_SHORT).show();
    }
	
	public void speak(String toSay){
		setToast("toSay " + toSay);
		tts.speakOut(toSay, toSay);
	}
	
	
	
	@Override
	public void onClick(View v) {

		if (v.getId() == mButton.getId()){
			stt.startRecognizeSpeech();
		}
			
			
	}

	public void sttReady() {
		sttReady = true;
		setToast("sttReady");
		checkSystemReady();
	}


	public void ttsReady() {
		ttsReady = true;
		setToast("ttsReady");
		checkSystemReady();
		
	}
	
	public void checkSystemReady(){
		if(ttsReady && sttReady){
			setToast("System Ready");
		}
	}

	@Override
	public void ttsOnDone(String pUtteranceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sttResults(List<String> data, float[] pConfidence) {
		String toSay = data.get(0);
		tts.speakOut(toSay, toSay);
		
	}

	@Override
	public boolean isBluethoothOn() {
		// TODO Auto-generated method stub
		return false;
	}


}
