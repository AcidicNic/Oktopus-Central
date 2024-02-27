package com.thirtythreelabs.flowmodel;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class Command {
	
	@Attribute 
	public String help;
	
	@ElementList 
	public List<Utterance> inputList = new ArrayList<Utterance>();
	
	
	@ElementList 
	public List<Outcome> outcomeList = new ArrayList<Outcome>();
	
	
	public Command(){
		this.help = "";
	}

	public Command(String help){
		this.help = help;
	}
	
	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
	
	
	public List<Utterance> getInputList() {
		return inputList;
	}

	public void setInputList(List<Utterance> inputList) {
		this.inputList = inputList;
	}

	public List<Outcome> getOutcomeList() {
		return outcomeList;
	}

	public void setOutcomeList(List<Outcome> outcomeList) {
		this.outcomeList = outcomeList;
	}

	
}
