package it.polito.tdp.PremierLeague.model;

public class Evento implements Comparable<Evento>{

	public enum EventType {
		GOAL,
		ESPULSIONE,
		INFORTUNIO
	}
	
	private int action;
	private EventType type;
	
	@Override
	public int compareTo(Evento e) {
		return this.action-e.getAction();
	}
	
	public Evento(int action, EventType type) {
		this.action = action;
		this.type = type;
	}
	
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	
}
