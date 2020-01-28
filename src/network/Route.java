package network;

import network.message.Message;

public class Route {

//---  Instance Variables   -------------------------------------------------------------------
	
	private Node endOne;
	private Node endTwo;
	private double speed;
	private double distance;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Route(double spd) {
		speed = spd;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void assign(Node a, Node b) {
		endOne = a;
		endTwo = b;
		distance = a.distance(b);
	}
	
	public boolean send(Message m, Address address) {
		return(pick(address.toString(), false).receive(m, time()));
	}
	
	public double time() {
		return speed * distance;
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

//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Endpoint one - Name: \"" + endOne.getName() + "\", Address: \"" + endOne.getAddressString() + "\"\n");
		sb.append("Endpoint two - Name: \"" + endTwo.getName() + "\", Address: \"" + endTwo.getAddressString() + "\"\n");
		sb.append("Length: " + getDistanceRounded() + ", Speed: " + getSpeed() + "\n");
		return sb.toString();
	}
	
}
