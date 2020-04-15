package network.protocol.message;

import java.util.Collection;

import network.Address;
import network.Node;
import network.message.Message;

public class TopToBottom implements SendProtocol{
	
	private Node node;

	public TopToBottom() {
		node = null;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public Address decideTarget(Collection<String> targets, Address target, Address defaultGate) {
		Address out = new Address();
		for(String s : targets) {
			Address test = target.prefixMatch(new Address(s));
			if(test.getLength() > out.getLength() && targets.contains(test.getAddress())) {
				out = test;
			}
		}
		if(out.getLength() == 0) {
			return defaultGate;
		}
		return out;
	}
	
	@Override
	public Message processMessage(Message m) {
		String[] data = m.getHeader().split("\n");
		switch(m.getHeader().split("\n")[0]) {
			case "Explore":
				if(!m.getDestination().equals(node.getAddress())) {
					break;
				}
				int TTD = Integer.parseInt(data[1].replaceAll("TTD=", "")) - 1;
				if(TTD != 0) {
					for(String s : node.getContactAddresses()) {
						Message explore = new Message();
						explore.addDestination(new Address(s));
						explore.setHeader("Explore\nTTD=" + TTD + "\n" + data[data.length-2] + "\n" + data[data.length-1]);
						explore.setBody("Body");
						node.receive(explore);
					}
				}
				return null;
			case "Return Explore":
				break;
			case "Regular":
				break;
			default:
				break;
		}
		while(m.getDestination() != null && m.getDestination().equals(node.getAddress())) {
			m.removeTopDestination();
		}
		return m;
	}
	
	@Override
	public Message prepareMessage(Message m) {
		Address target = decideTarget(node.getContactAddresses(), m.getDestination(), node.getAddress().tear());
		if(!m.getDestination().equals(target)) {
			m.addDestination(target); 
		}
		return m;
	}
	
	public void assignNode(Node in) {
		node = in;
	}
	
	@Override
	public int memoryUsage() {
		// TODO Auto-generated method stub
		return 0;
	}


	public TopToBottom copy() {
		return new TopToBottom();
	}
	
}
