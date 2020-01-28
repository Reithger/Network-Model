package network;

import network.message.Message;
import network.protocol.device.MessagePattern;

public class Device extends Node{
	
//---  Instance Variables   -------------------------------------------------------------------	

	private MessagePattern mp;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Device(String inName, String inAddress, int inX, int inY, MessagePattern inMP) {
		super(inName, inAddress, inX, inY);
		mp = inMP;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void sendMessage(String target, String body) {
		Message m = new Message(target, body);
		send(m);
	}
	
	@Override
	public boolean endpoint() {
		return true;
	}

//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		
		return sb.toString();
	}
	
}
