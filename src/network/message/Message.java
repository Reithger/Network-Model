package network.message;

import java.util.LinkedList;

import network.Address;

public class Message {
	
//---  Instance Variables   -------------------------------------------------------------------

	private LinkedList<Address> target;
	private String body;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Message (String inTarget, String inBody) {
		target = new LinkedList<Address>();
		target.add(new Address(inTarget));
		body = inBody;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void addDestination(Address dest) {
		target.add(dest);
	}
		
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Address getDestination() {
		return target.poll();
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
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Destination Chain: ");
		for (Address s : target) {
			sb.append(s + " -> ");
		}
		sb.append("\n");
		sb.append("Message: " + body + "\n");
		return sb.toString();
	}
	
}
