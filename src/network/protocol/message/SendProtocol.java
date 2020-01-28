package network.protocol.message;

import java.util.Collection;

import network.Address;
import network.message.Message;

public interface SendProtocol {
	
//---  Operations   ---------------------------------------------------------------------------

	public abstract Address decide(Collection<String> targets, Message m);
	
	public abstract int memoryUsage();
	
}
