package network;

import java.util.ArrayList;
import java.util.LinkedList;

import network.message.Message;

public class Route {

//---  Instance Variables   -------------------------------------------------------------------
	
	private Node endOne;
	private Node endTwo;
	private String name;
	private double uploadSpeed;
	private double distance;
	private double streamSpeed;
	private LinkedList<Message> messages;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Route(double upSpd, double strmSpd) {
		uploadSpeed = upSpd;
		streamSpeed = strmSpd;
		messages = new LinkedList<Message>();
		name = "";
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void assign(Node a, Node b) {
		endOne = a;
		endTwo = b;
		distance = a.distance(b);
		name = a.getName() + " <-> " + b.getName();
	}
	
	public void operate() {
		for(int i = 0; i < messages.size(); i++) {
			Message m = messages.get(i);
			if(second(System.currentTimeMillis() - m.getTimeStamp()) >= messageDelay(m)) {
				pick(m.getDestination(), true).receive(m);
				messages.remove(m);
				i--;
			}
		}
	}
	
	public void send(Message m) {
		m.setTimeStamp(System.currentTimeMillis());
		messages.add(m);
	}

	public double second(long in) {
		return in / 1000.0;
	}
	
	public double messageDelay(Message m) {
		return propogationDelay(m) + travelTime();
	}
	
	public double propogationDelay(Message m) {
		return getUploadSpeed() * m.getSize();
	}
	
	public double travelTime() {
		return getDistance() * getStreamSpeed(); 
	}
		
	public double progress(Message m) {
		return second(System.currentTimeMillis() - m.getTimeStamp()) / messageDelay(m);
	}
	
	private Node pick(Address address, boolean same) {
		if(address.equals(endOne.getAddress())) {
			return same ? endOne : endTwo;
		}
		else if(address.equals(endTwo.getAddress())) {
			return same ? endTwo : endOne;
		}
		else {
			return null;
		}
	}
	
	private Node pick(String address, boolean same) {
		if(address.equals(endOne.getAddressString())) {
			return same ? endOne : endTwo;
		}
		else if(address.equals(endTwo.getAddressString())) {
			return same ? endTwo : endOne;
		}
		else {
			return null;
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Node getPartner(String address) {
		return pick(address, false);
	}
	
	public double getDistance() {
		return distance;
	}
	
	public double getDistanceRounded() {
		return (double)((int)(distance * 100)) / 100.0;
	}
	
	public double getUploadSpeed() {
		return uploadSpeed;
	}
	
	public double getStreamSpeed() {
		return streamSpeed;
	}
	
	public String getName() {
		return name;
	}
	
	public Node getFirstNode() {
		return endOne;
	}
	
	public Node getSecondNode() {
		return endTwo;
	}

	public LinkedList<Message> getMessages(){
		return messages;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + getName() + ", Upload Speed: " + getUploadSpeed() + "\n");
		sb.append("Endpoint one - Name: \"" + endOne.getName() + "\", Address: \"" + endOne.getAddressString() + "\"\n");
		sb.append("Endpoint two - Name: \"" + endTwo.getName() + "\", Address: \"" + endTwo.getAddressString() + "\"\n");
		sb.append("Length: " + getDistanceRounded() + ", Stream Speed: " + getStreamSpeed() + "\n");
		return sb.toString();
	}
	
}
