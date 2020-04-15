package network.protocol.device;

import network.Device;
import network.message.Message;

public interface MessagePattern {

	public abstract void setDevice(Device inD);
	
	public abstract Message chooseTarget(Message m);
	
	public abstract Message writeHeader(Message m);
	
	public abstract Message chooseBody(Message m);
	
	public abstract void receiveMessage(Message m);
	
	public abstract MessagePattern copy();

	public abstract String toString();
	
}
