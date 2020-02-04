package network.protocol.message;

import java.util.Collection;

import network.Address;
import network.message.Message;

public class TopToBottom implements SendProtocol{

	public TopToBottom() {
		
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public Address decide(Collection<String> targets, Message m) {
		Address target = m.getDestination();
		Address out = new Address();
		for(String s : targets) {
			Address test = target.prefixMatch(new Address(s));
			if(test.getLength() > out.getLength()) {
				out = test;
			}
		}
		return out;
	}

	@Override
	public int memoryUsage() {
		// TODO Auto-generated method stub
		return 0;
	}

}
