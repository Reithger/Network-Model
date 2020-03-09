package network;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import network.message.Message;
import network.protocol.message.SendProtocol;

public class Node {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static int FAIL_CAP = 50;
	private final static int DEFAULT_PROCESSING = 6;

//---  Instance Variables   -------------------------------------------------------------------
	
	private double x;
	private double y;
	
	private Address address;
	private String name;
	private double processing;
	private int memoryMax;
	private int memoryUsed;
	private LinkedList<Message> queue;
	private HashMap<String, Route> contacts;
	private SendProtocol protocolSend;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Node(String inName, String inaddress, double inX, double inY) {
		x = inX;
		y = inY;
		address = new Address(inaddress);
		name = inName;
		processing = DEFAULT_PROCESSING;
		memoryMax = 100;
		memoryUsed = 0;
		queue = new LinkedList<Message>();
		contacts = new HashMap<String, Route>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public void operate() {
		if(queue.size() > 0) {
			if(second(System.currentTimeMillis() -  queue.peek().getTimeStamp()) >= (double)queue.peek().getSize() / getProcessing()) {
				Message m = queue.poll();
				send(m);
				memoryUsed -= m.getSize();
			}
		}
	}
	
	public Route connect(Node other, double upSpeed, double strmSpeed) {
		Route rt = new Route(upSpeed, strmSpeed);
		rt.assign(this, other);
		addRoute(rt);
		other.addRoute(rt);
		return rt;
	}
	
	public void receive(Message m) {
		System.out.println(name + " @ " + getAddress() + " received:\n" + m);
		if(m.getSize() + getMemoryUsed() <= getMemoryMax()) {
			memoryUsed += m.getSize();
			while(m.getDestination() != null && m.getDestination().equals(address)) {
				m.removeTopDestination();
			}
			m.setTimeStamp(System.currentTimeMillis());
			if(m.getDestination() != null) {
				queue.add(m);	
			}
			else {
				
			}
		}
	}
	
	public void send(Message m) {
		try {
			Address target = protocolSend.decide(contacts.keySet(), m, getAddress().tear());
			if(!m.getDestination().equals(target)) {
				m.addDestination(target);
			}
			contacts.get(target.getAddress()).send(m);
			System.out.println(getName() + " @ " + getAddress() + " sending:\n" + m + " to " + target + "\n");
		}
		catch(Exception e) {
			System.out.println("Error Sending:\n" + m + "\nfrom " + getName() + " @ " + getAddress() + "\n");
			e.printStackTrace();
		}
	}
	
	public void addRoute(Route rt) {
		contacts.put(rt.getPartner(getAddressString()).getAddressString(), rt);
	}

	public double distance(Node other) {
		return Math.sqrt(Math.pow(getX() - other.getX(), 2) + Math.pow(getY() - other.getY(), 2));
	}
	
	public boolean endpoint() {
		return false;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	public void setCommunicationProtocol(SendProtocol in) {
		protocolSend = in;
	}
	
	public void setX(double inX) {
		x = inX;
	}
	
	public void setY(double inY) {
		y = inY;
	}
	
	public void setAddress(String inAddress) {
		address = new Address(inAddress);
	}
	
	public void setAddress(Address inAddress) {
		address = inAddress;
	}
	
	public void setProcessing(double inProc) {
		processing = inProc;
	}
	
	public void setMemoryMax(int inMa) {
		memoryMax = inMa;
	}
	
	public void setMemoryUsed(int inMe) {
		memoryUsed = inMe;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}
	
	public Address getAddress() {
		return address;
	}

	public String getAddressString() {
		return address.getAddress();
	}
	
	public double getProcessing() {
		return processing;
	}
	
	public int getMemoryMax() {
		return memoryMax;
	}
	
	public int getMemoryUsed() {
		return memoryUsed;
	}

	public SendProtocol getSendProtocol() {
		return protocolSend;
	}
	
	public Collection<Route> getContacts(){
		return contacts.values();
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + getName() + ", Address: " + getAddress() + "\n");
		sb.append("Processing: " + getProcessing() + ", Memory Max: " + getMemoryMax() + "\n");
		sb.append("X: " + getX() + ", Y: " + getY() + "\n");
		return sb.toString();
	}
	
	public double second(long in) {
		return in / 1000.0;
	}
	
}
