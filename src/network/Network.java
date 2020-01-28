package network;

import java.util.ArrayList;

public class Network {
	
//---  Instance Variables   -------------------------------------------------------------------

	private ArrayList<Node> nodes;
	private ArrayList<Device> devices;
	private ArrayList<Route> routes;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Network() {
		nodes = new ArrayList<Node>();
		devices = new ArrayList<Device>();
		routes = new ArrayList<Route>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	public void addNode(Node add) {
		nodes.add(add);
	}

	public void addDevice(Device add) {
		devices.add(add);
	}

	public void addRoute(Route add) {
		routes.add(add);
	}

//---  Remover Methods   -----------------------------------------------------------------------
	
	public void removeNode(Node remove) {
		nodes.remove(remove);
	}
	
	public void removeDevice(Device remove) {
		devices.remove(remove);
	}
	
	public void removeRoute(Route remove) {
		routes.remove(remove);
	}

//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("---Nodes---\n");
		for(Node n : nodes) {
			sb.append(" > " + n.toString());
		}
		sb.append("\n---Devices---\n");
		for(Device d : devices) {
			sb.append(" > " + d.toString());
		}
		sb.append("\n---Routes---\n");
		for(Route r : routes) {
			sb.append(" > " + r.toString());
		}
		return sb.toString();
	}
	
}
