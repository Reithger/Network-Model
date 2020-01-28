package network;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import network.message.Message;
import network.protocol.message.SendProtocol;

public class Node {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static int FAIL_CAP = 50;

//---  Instance Variables   -------------------------------------------------------------------
	
	private int x;
	private int y;
	
	private Address address;
	private String name;
	private double processing;
	private int memoryMax;
	private int memoryUsed;
	private LinkedList<Message> queue;
	private HashMap<String, Route> contacts;
	private SendProtocol protocolSend;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Node(String inName, String inaddress, int inX, int inY) {
		x = inX;
		y = inY;
		address = new Address(inaddress);
		name = inName;
		processing = 1;
		memoryMax = 100;
		memoryUsed = 0;
		queue = new LinkedList<Message>();
		contacts = new HashMap<String, Route>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public void connect(Node other, double speed) {
		Route rt = new Route(speed);
		rt.assign(this, other);
		addRoute(rt);
		other.addRoute(rt);
	}
	
	public boolean receive(Message m, double time) {
		//TODO: insert delay here, or have some means of making an event take time
		if(m.getSize() + getMemoryUsed() > getMemoryMax()) {
			return false;
		}
		else {
			memoryUsed += m.getSize();
			queue.add(m);
			return true;
		}
	}
	
	public boolean send(Message m) {
		Address target = protocolSend.decide(contacts.keySet(), m);
		double time = m.getSize() * getProcessing();
		//TODO: insert delay here, or have some means of making an event take time
		int failure = 0;
		while(!contacts.get(target.toString()).send(m, getAddress()) && failure < FAIL_CAP) {
			failure++;
		}
		return failure < FAIL_CAP;
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
	
	public void setX(int inX) {
		x = inX;
	}
	
	public void setY(int inY) {
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
	
	public int getX() {
		return x;
	}
	
	public int getY() {
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
	
}
