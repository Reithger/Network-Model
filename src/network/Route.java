package network;

import java.util.ArrayList;

import network.message.Message;

public class Route {

//---  Instance Variables   -------------------------------------------------------------------
	
	private Node endOne;
	private Node endTwo;
	private String name;
	private double speed;
	private double distance;
	private ArrayList<Message> messages;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Route(double spd) {
		speed = spd;
		messages = new ArrayList<Message>();
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
			if(System.currentTimeMillis() - m.getTimeStamp() >= time() * m.getSize()) {
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
	
	public double time() {
		return speed * distance;
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
	
	public double getSpeed() {
		return speed;
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

//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + getName());
		sb.append("Endpoint one - Name: \"" + endOne.getName() + "\", Address: \"" + endOne.getAddressString() + "\"\n");
		sb.append("Endpoint two - Name: \"" + endTwo.getName() + "\", Address: \"" + endTwo.getAddressString() + "\"\n");
		sb.append("Length: " + getDistanceRounded() + ", Speed: " + getSpeed() + "\n");
		return sb.toString();
	}
	
}
