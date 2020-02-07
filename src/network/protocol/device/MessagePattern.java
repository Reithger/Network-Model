package network.protocol.device;

public interface MessagePattern {

	public abstract String chooseTarget();
	
	public abstract String chooseBody();
	
	public abstract void receiveMessage(String body);
	
}
