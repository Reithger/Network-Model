package network;

import java.util.Random;

import network.message.Message;
import network.protocol.device.MessagePattern;

public class Device extends Node{
	
//---  Instance Variables   -------------------------------------------------------------------	

	private MessagePattern mp;
	private Random rand;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Device(String inName, String inAddress, double inX, double inY, MessagePattern inMP) {
		super(inName, inAddress, inX, inY);
		mp = inMP;
		rand = new Random();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public void operate() {
		int seed = rand.nextInt(30);
		if(seed == 15) {
			sendMessage(mp.chooseTarget(), mp.chooseBody());
		}
	}
	
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
