package com.thirtythreelabs.oktopus;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

import com.thirtythreelabs.flowmodel.Command;
import com.thirtythreelabs.flowmodel.DataModule;
import com.thirtythreelabs.flowmodel.Flow;
import com.thirtythreelabs.flowmodel.FlowModule;
import com.thirtythreelabs.flowmodel.Module;
import com.thirtythreelabs.flowmodel.Outcome;
import com.thirtythreelabs.flowmodel.Phrase;
import com.thirtythreelabs.flowmodel.SayDoModule;
import com.thirtythreelabs.flowmodel.Utterance;
import com.thirtythreelabs.util.ConvertWordToNumber;



@SuppressLint("DefaultLocale")
public class Bicycle {
	
	Flow mFlow;
	Activity mActivity;
	String itemsPicked = "0";
	
	public interface OnBicycleResults {
        public abstract void goToSystemAndProcessData(String pAction, Module pModule);
        public abstract void goToSystemAndProcessSay(Module pModule, List<Phrase> sayList);
        public abstract void goToSystemAndProcessListenTo(Module pModule);
        public abstract void goToSystemAndGoToActivity(String pActivityToGo, Module pModule);
        public abstract void goToSystemMethod(String pMethod, Module pModule);
        public abstract void goToSystemAndSetPicked(String itemsPicked);
        public abstract void setToast(String myToast);
    }
	
	
	public void startFlow(Flow tempFlow, Activity tempActivity){
		mFlow = tempFlow;
		mActivity = tempActivity;
	}
	
	public void ride(String pModuleName, Boolean start){
		
		if(start){
			for (int i = 0; i < mFlow.flowModules.size(); i++){
				
				String moduleStart = mFlow.flowModules.get(i).getStart().toLowerCase();
				String moduleType = mFlow.flowModules.get(i).getType().toLowerCase();
				
				// data type
				if(moduleType.equalsIgnoreCase("data") || moduleStart.equalsIgnoreCase("start")){
						
					DataModule dataModule = (DataModule) mFlow.flowModules.get(i);
					processDataModule(dataModule);
					return;
					
				}
				
				// flow type
				if(moduleType.equalsIgnoreCase("flow") || moduleStart.equalsIgnoreCase("start")){
						
					FlowModule flowModule = (FlowModule) mFlow.flowModules.get(i);
					processFlowModule(flowModule, "say");
					return;
					
				}
				
				// sayDo type
				if(moduleType.equalsIgnoreCase("sayDo") || moduleStart.equalsIgnoreCase("start")){
						
					SayDoModule sayDoModule = (SayDoModule) mFlow.flowModules.get(i);
					processSayDoModule(sayDoModule, "say");
					return;
					
				}
			}
			
		}else{
			porcessModule(pModuleName);
		}
		
		

	}
	
	private void porcessModule(String pModuleName){
		for (int i = 0; i < mFlow.flowModules.size(); i++){
			
			String moduleName = mFlow.flowModules.get(i).getName().toLowerCase();
			String moduleType = mFlow.flowModules.get(i).getType().toLowerCase();
			
			// data type
			if(moduleType.equalsIgnoreCase("data") && moduleName.equalsIgnoreCase(pModuleName)){
					
				DataModule dataModule = (DataModule) mFlow.flowModules.get(i);
				processDataModule(dataModule);
				return;
				
			}
			
			// flow type
			if(moduleType.equalsIgnoreCase("flow") && moduleName.equalsIgnoreCase(pModuleName)){
					
				FlowModule flowModule = (FlowModule) mFlow.flowModules.get(i);
				processFlowModule(flowModule, "say");
				return;
				
			}
			
			// flow type
			if(moduleType.equalsIgnoreCase("sayDo") && moduleName.equalsIgnoreCase(pModuleName)){
					
				SayDoModule sayDoModule = (SayDoModule) mFlow.flowModules.get(i);
				processSayDoModule(sayDoModule, "say");
				return;
				
			}
		}
	}
	

	private void processFlowModule(FlowModule flowModule, String sayOrListenTo) {
		// say type
		if(sayOrListenTo.equalsIgnoreCase("say")){
			List<Phrase> sayList = flowModule.getSay();
			processSay(flowModule, sayList);
		}
		
		// listenTo type
		if(sayOrListenTo.equalsIgnoreCase("listenTo")){
			processListenTo(flowModule);
		}
	}
	

	private void processSayDoModule(SayDoModule sayDoModule, String sayOrDo) {
		// say type
		if(sayOrDo.equalsIgnoreCase("say")){
			List<Phrase> sayList = sayDoModule.getSay();
			processSay(sayDoModule, sayList);
		}
		
		// listenTo type
		if(sayOrDo.equalsIgnoreCase("do")){
			Outcome outcome = sayDoModule.getOutcomeList().get(0);
			processOutcome(sayDoModule, outcome);
		}
	}
	
	
	private void processSay(Module module, List<Phrase> sayList){
		
		((OnBicycleResults) mActivity).goToSystemAndProcessSay(module, sayList);

	}
	
	public void processSayCallback(Module module){
		// vuelve despues de hablar
		if(module.getType().equalsIgnoreCase("flow")){
			FlowModule flowmodule = (FlowModule) module;
			processFlowModule(flowmodule, "listenTo");
		}
		
		if(module.getType().equalsIgnoreCase("sayDo")){
			SayDoModule sayDoModule = (SayDoModule) module;
			processSayDoModule(sayDoModule, "do");
		}
		
	}
	
	
	private void processCommands(FlowModule flowModule, List<Command> commandList){
		
		HashMap<String, Integer> commands = new HashMap<String, Integer>();
		
		for (int i = 0; i < commandList.size(); i++){
			for (int u = 0; u < commandList.size(); u++){
				String listenTo = commandList.get(i).inputList.get(u).getLine();
				Integer outcomeIndex = i;
				commands.put(listenTo, outcomeIndex);
			}
		}
		
		processListenTo(flowModule);
	}
	
	
	
	private void processListenTo(FlowModule flowModule) {
		((OnBicycleResults) mActivity).goToSystemAndProcessListenTo(flowModule);
		
	}
	
	
	
	public void processListenToCallback(Module module, List<String> iHeardThis){
		
		FlowModule flowModule = (FlowModule) module;
		// vuelve del sistema con lo que escucho.
		List<Command> commandList = flowModule.getListenTo();
		
		// recorre los comandos
		for (int i = 0; i < commandList.size(); i++){
			List<Utterance> utteranceList = commandList.get(i).getInputList();
			
			// recorre las utterance
			for (int u = 0; u < utteranceList.size(); u++){
				String listenTo = utteranceList.get(u).getLine().toLowerCase();
				
				String originalListenTo = utteranceList.get(u).getLine();
				
				listenTo = listenTo.replace("á", "a");
				listenTo = listenTo.replace("é", "e");
				listenTo = listenTo.replace("í", "i");
				listenTo = listenTo.replace("ó", "o");
				listenTo = listenTo.replace("ú", "u");
				
				if(listenTo.length() > 3 && !listenTo.substring(0,1).contentEquals("a") &&
					!listenTo.substring(0,1).contentEquals("e") &&
					!listenTo.substring(0,1).contentEquals("i") &&
					!listenTo.substring(0,1).contentEquals("o") &&
					!listenTo.substring(0,1).contentEquals("u")){
					
					listenTo = listenTo.substring(1);
					
				}
				
				if(originalListenTo.length() > 3){
					while(!listenTo.substring(0,1).contentEquals("a") &&
						!listenTo.substring(0,1).contentEquals("e") &&
						!listenTo.substring(0,1).contentEquals("i") &&
						!listenTo.substring(0,1).contentEquals("o") &&
						!listenTo.substring(0,1).contentEquals("u")){
						
						listenTo = listenTo.substring(1);
						
					}
					
					String tempString = listenTo.substring(listenTo.length() - 1);
					if(tempString.contentEquals("a") || 
							tempString.contentEquals("e") ||
							tempString.contentEquals("i") ||
							tempString.contentEquals("o") ||
							tempString.contentEquals("u")){
						listenTo = listenTo.substring(0, listenTo.length() - 1);
					}
				}
				
				
				
				String utteranceType = utteranceList.get(u).getType();
				
				//recorre lo que escucho
				for (int h = 0; h < iHeardThis.size(); h++){
					
					
					String mHeardThisTotal = iHeardThis.get(h).toLowerCase();
					
					
					String arr[] = mHeardThisTotal.split(" ", 2);
					String mHeardThis = arr[0];
					String theRest = "";
					if(arr.length == 2){
						theRest = arr[1];
					}
					
					
					String mHeardThisOriginal = iHeardThis.get(h).toLowerCase();
					
					mHeardThis = mHeardThis.replace("á", "a");
					mHeardThis = mHeardThis.replace("é", "e");
					mHeardThis = mHeardThis.replace("í", "i");
					mHeardThis = mHeardThis.replace("ó", "o");
					mHeardThis = mHeardThis.replace("ú", "u");
					
			
					if(mHeardThis.length() > 3){
						while(!mHeardThis.substring(0,1).contentEquals("a") &&
							!mHeardThis.substring(0,1).contentEquals("e") &&
							!mHeardThis.substring(0,1).contentEquals("i") &&
							!mHeardThis.substring(0,1).contentEquals("o") &&
							!mHeardThis.substring(0,1).contentEquals("u")){
							
							mHeardThis = mHeardThis.substring(1);
							
						}
						
						String tempString = mHeardThis.substring(mHeardThis.length() - 1);
						if(tempString.contentEquals("a") || 
								tempString.contentEquals("e") ||
								tempString.contentEquals("i") ||
								tempString.contentEquals("o") ||
								tempString.contentEquals("u")){
							mHeardThis = mHeardThis.substring(0, mHeardThis.length() - 1);
						}
					}
					
					if(mHeardThis.equals(listenTo)){
						((OnBicycleResults) mActivity).setToast(originalListenTo);
						
						Outcome outcome = commandList.get(i).getOutcomeList().get(0);
						
						if(utteranceType.equalsIgnoreCase("qty")){
							itemsPicked = "0";
							itemsPicked = theRest;
							
							
							String finalNumber = "";
							
							try {
								String number = ConvertWordToNumber.WithSeparator(ConvertWordToNumber.parse(itemsPicked));
								finalNumber = String.valueOf(number);

							} catch (Exception e) {
								
								itemsPicked = itemsPicked.replaceAll("punto",".");
								finalNumber = itemsPicked.replaceAll("[^\\.0123456789]","");
						        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
							}
							
							
							((OnBicycleResults) mActivity).setToast("qty: " + finalNumber);
							
							((OnBicycleResults) mActivity).goToSystemAndSetPicked(finalNumber);
						}
						
						processOutcome(flowModule, outcome);
						return;
						
					} else if(mHeardThis.contains(listenTo)){
						((OnBicycleResults) mActivity).setToast(originalListenTo);
						
						Outcome outcome = commandList.get(i).getOutcomeList().get(0);

						if(utteranceType.equalsIgnoreCase("qty")){
							itemsPicked = "0";
							
							itemsPicked = theRest;
							
							((OnBicycleResults) mActivity).setToast("qty: " + itemsPicked);
							
							((OnBicycleResults) mActivity).goToSystemAndSetPicked(itemsPicked);
						}

						
						// procesa el outcome
						processOutcome(flowModule, outcome);
						return;
						
					}
				}				
			}
		}
		
		processFlowModule(flowModule, "listenTo");
	}
	
	
	private void processDataModule(DataModule dataModule) {
		String action = dataModule.getData().getAction();
		
		((OnBicycleResults) mActivity).goToSystemAndProcessData(action, dataModule);
		
	}
	

	public void processDataCallback(Module module, String processDataResult) {
		
		// vuelve del metodo del sistema con processDataResult true or false
		
		List<Outcome> outcomeList = ((DataModule) module).getData().getOutcomeList();
		
		for (int i = 0; i < outcomeList.size(); i++){
			String result = outcomeList.get(i).getResult().toLowerCase();
			if(result.equalsIgnoreCase(processDataResult.toLowerCase())){
				Outcome outcome = outcomeList.get(i);
				
				// y procesa el outcome
				processOutcome(module, outcome);
				return;
			}
		}

	}
	
	
	private void processOutcome(Module module, Outcome outcome){
		String moduleType = outcome.getModType().toLowerCase();
		
		if(moduleType.equalsIgnoreCase("module")){
			String gotoWhere = outcome.getGotoWhere();
			ride(gotoWhere, false);
			
		} else if(moduleType.equalsIgnoreCase("activity")){
			
			String activityToGo = outcome.getGotoWhere();
			((OnBicycleResults) mActivity).goToSystemAndGoToActivity(activityToGo, module);
			
		} else if(moduleType.equalsIgnoreCase("say")){
			List<Phrase> sayList = outcome.getSay();
			processSay(module, sayList);
			
		} else if(moduleType.equalsIgnoreCase("timeOut")){
			// TODO 
		} else if(moduleType.equalsIgnoreCase("method")){
			String tempMethod = outcome.getGotoWhere();
			((OnBicycleResults) mActivity).goToSystemMethod(tempMethod, module);
		}
	}


}
