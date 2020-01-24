package network.message;

import java.util.LinkedList;

public class Message {
	
//---  Instance Variables   -------------------------------------------------------------------

	private LinkedList<String> target;
	private String body;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Message (String inTarget, String inBody) {
		target = new LinkedList<String>();
		target.add(inTarget);
		body = inBody;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void addDestination(String dest) {
		target.add(dest);
	}
		
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getDestination() {
		return target.poll();
	}
	
	public String getContents() {
		return body;
	}
	
	public int getSize() {
		int size = body.length();
		for(int i = 0; i < target.size(); i++) {
			size += target.get(i).length();
		}
		return size;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Destination Chain: ");
		for (String s : target) {
			sb.append(s + " -> ");
		}
		sb.append("\n");
		sb.append("Message: " + body + "\n");
		return sb.toString();
	}
	
}
