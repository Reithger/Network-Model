package network.protocol.message;

import java.util.Collection;

import network.Address;
import network.message.Message;

public class TopToBottom implements SendProtocol{

//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public Address decide(Collection<String> targets, Message m) {
		Address target = m.getDestination();
		return null;
	}

	@Override
	public int memoryUsage() {
		// TODO Auto-generated method stub
		return 0;
	}

}
