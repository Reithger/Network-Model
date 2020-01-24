package network.protocol;

import java.util.Collection;
import java.util.HashMap;

import network.Node;
import network.message.Message;

public class OpenShortestPathFirst implements SendProtocol{
	
//---  Instance Variables   -------------------------------------------------------------------

	private Node reference;
	private HashMap<String, String> routingTable;

//---  Constructors   -------------------------------------------------------------------------
	
	public OpenShortestPathFirst(Node associate) {
		reference = associate;
		routingTable = new HashMap<String, String>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public String decide(Collection<String> targets, Message m) {
		String target = m.getDestination();
		return null;
	}
	
	public void updateRoutingTable() {
		
	}

}
