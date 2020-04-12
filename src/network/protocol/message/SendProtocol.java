package network.protocol.message;

import java.util.Collection;

import network.Address;
import network.Node;
import network.message.Message;

public interface SendProtocol {
	
//---  Operations   ---------------------------------------------------------------------------

	public abstract Address decideTarget(Collection<String> targets, Address m, Address defaultGateway);
	
	public abstract Message processMessage(Message m);
	
	public abstract Message prepareMessage(Message m);

	public abstract void assignNode(Node n);
	
	public abstract int memoryUsage();
	
	public abstract SendProtocol copy();
	
}
