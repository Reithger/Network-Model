package network;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Network {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private int refreshRate = 30;
	
//---  Instance Variables   -------------------------------------------------------------------

	private ArrayList<Node> nodes;
	private ArrayList<Device> devices;
	private ArrayList<Route> routes;
	private Timer timer;
	private boolean running;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Network() {
		nodes = new ArrayList<Node>();
		devices = new ArrayList<Device>();
		routes = new ArrayList<Route>();
		timer = new Timer();
		running = false;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void start() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runNetwork();
			}
		}, 0, 10);
		running = true;
	}
	
	public void stop() {
		timer.cancel();
		timer = new Timer();
		running = false;
	}
	
	private void runNetwork() {
		for(Node n : nodes) {
			n.operate();
		}
		for(Route r : routes) {
			r.operate();
		}
		for(Device d : devices) {
			d.operate();
		}
	}
	
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
	
//---  Getter Methods   -----------------------------------------------------------------------

	public boolean getRunningStatus() {
		return running;
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
