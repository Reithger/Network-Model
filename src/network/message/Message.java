package network.message;

import java.util.LinkedList;

import network.Address;

public class Message {
	
//---  Instance Variables   -------------------------------------------------------------------

	private LinkedList<Address> target;
	private String body;
	private long born;
	private long time;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Message (String inTarget, String inBody) {
		target = new LinkedList<Address>();
		target.add(new Address(inTarget));
		body = inBody;
		born = System.currentTimeMillis();
	}
	
	public Message(Address inTarget, String inBody) {
		target = new LinkedList<Address>();
		target.add(inTarget);
		body = inBody;
		born = System.currentTimeMillis();
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void addDestination(Address dest) {
		target.addFirst(dest);
	}
	
	public void removeTopDestination() {
		target.poll();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
		
	public void setTimeStamp(long in) {
		time = in;
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
	
	public String getContents() {
		return body;
	}
	
	public int getSize() {
		int size = body.length();
		for(int i = 0; i < target.size(); i++) {
			size += target.get(i).toString().length();
		}
		return size;
	}
	
	public long getTimeStamp() {
		return time;
	}
	
	public long getBirth() {
		return born;
	}
	
	public double getAge(long t) {
		return (t - getBirth()) / 1000.0;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Destination Chain: ");
		for (int i = 0; i < target.size(); i++) {
			Address s = target.get(i);
			sb.append(s + (i + 1 < target.size() ? " -> " : "\n"));
		}
		sb.append("Time Stamp: " + getAge(time) + " sec\n");
		sb.append("Message: " + body + "\n");
		return sb.toString();
	}
	
}
