package network.message;

import java.util.LinkedList;
import network.Network;
import network.Address;

public class Message {
	
//---  Instance Variables   -------------------------------------------------------------------

	private LinkedList<Address> target;
	private String header;
	private String body;
	private int born;
	private int time;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Message() {
		target = new LinkedList<Address>();
		header = "";
		body = "";
		born = Network.getClock();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void addDestination(Address dest) {
		target.addFirst(dest);
	}
	
	public void removeTopDestination() {
		target.poll();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
		
	public void setTimeStamp() {
		time = Network.getClock();
	}
	
	public void setHeader(String head) {
		header = head;
	}
	
	public void setBody(String bod) {
		body = bod;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Address getDestination() {
		try {
			return target.peek();
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public String getHeader() {
		return header;
	}
	
	public String getBody() {
		return body;
	}
	
	public int getSize() {
		int size = body.length();
		for(int i = 0; i < target.size(); i++) {
			size += target.get(i).toString().length();
		}
		return size;
	}
	
	public int getTimeStamp() {
		return time;
	}
	
	public int getBirth() {
		return born;
	}
	
	public double getAge(int t) {
		return (double)(Network.getClock() - getBirth()) / (double)Network.getRefreshRate();
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Destination Chain: ");
		for (int i = 0; i < target.size(); i++) {
			Address s = target.get(i);
			sb.append(s + (i + 1 < target.size() ? " -> " : "\n"));
		}
		sb.append("Born: " + (getBirth() / (double)Network.getRefreshRate()) + " sec, Time Alive: " + getAge(time) + " sec\n");
		sb.append("Header: " + header + "\n");
		sb.append("Body: " + body + "\n");
		return sb.toString();
	}
	
}
