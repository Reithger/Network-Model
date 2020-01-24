package network.protocol;

import java.util.Collection;
import network.message.Message;

public interface SendProtocol {
	
//---  Operations   ---------------------------------------------------------------------------

	public abstract String decide(Collection<String> targets, Message m);
	
}
