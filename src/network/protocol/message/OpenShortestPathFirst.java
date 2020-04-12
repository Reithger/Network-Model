package network.protocol.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import network.Address;
import network.Node;
import network.message.Message;

public class OpenShortestPathFirst implements SendProtocol{
	
//---  Instance Variables   -------------------------------------------------------------------

	private Node reference;
	/** List of Nodes in the same area as the associated Node*/
	private ArrayList<Address> neighborTable;
	/** Mapping of */
	private HashMap<String, Address> routingTable;
	private Address defaultGateway;

//---  Constructors   -------------------------------------------------------------------------
	
	public OpenShortestPathFirst(Node associate) {
		reference = associate;
		routingTable = new HashMap<String, Address>();
	}
	
	public OpenShortestPathFirst(Node associate, Address gateway) {
		reference = associate;
		routingTable = new HashMap<String, Address>();
		defaultGateway = gateway;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public Address decideTarget(Collection<String> targets, Address m, Address defaultGateway) {
		
		return null;
	}
	
	public void updateRoutingTable() {
		
	}

	public int memoryUsage() {
		
		return 1;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setDefaultGateway(Address in) {
		defaultGateway = in;
	}

	@Override
	public Message processMessage(Message m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message prepareMessage(Message m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignNode(Node n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SendProtocol copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
