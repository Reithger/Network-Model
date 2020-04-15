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
		mp = inMP.copy();
		mp.setDevice(this);
		rand = new Random();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public void operate() {
		int seed = rand.nextInt(30);
		if(seed == 15) {
			Message m = new Message();
			m = mp.chooseTarget(m);
			if(m == null) {
				return;
			}
			m = mp.writeHeader(m);
			m = mp.chooseBody(m);
			send(m);
		}
	}
	
	@Override
	public void receive(Message m) {
		mp.receiveMessage(m);
	}
		
	@Override
	public void changeMemoryUsed(int in) {
		return;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(mp.toString());
		return sb.toString();
	}
	
}
