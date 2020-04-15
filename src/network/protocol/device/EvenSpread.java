package network.protocol.device;

import java.util.ArrayList;
import java.util.Random;

import network.Address;
import network.Device;
import network.Network;
import network.message.Message;

public class EvenSpread implements MessagePattern{

	private final static String TYPE = "EvenSpread";
	
	private Device d;
	private Random rand;
	private ArrayList<String> neighbors;
	private int TTD;
	private int time;
	private int received;
	
	public EvenSpread() {
		d = null;
		rand = new Random();
		neighbors = new ArrayList<String>();
		TTD = 3;
		time = -17;
	}
	
	public void setDevice(Device inD) {
		d = inD;
	}
	
	@Override
	public Message chooseTarget(Message m) {
		if(time == -17 || (Network.getClock() - time) / Network.getRefreshRate() > 20) {
			m.addDestination(Address.WILDCARD);
			time = Network.getClock();
		}
		else if(neighbors.size() != 0){
			m.addDestination(new Address(neighbors.get(rand.nextInt(neighbors.size()))));
		}
		else {
			m = null;
		}
		return m;
	}
	
	/**
	 * 
	 * Header format:
	 * Type
	 * *** Metadata relating to Type ***
	 * Sender Name
	 * Sender Address
	 */
	
	@Override
	public Message writeHeader(Message m) {
		if(m.getDestination().equals(Address.WILDCARD)) {
			m.setHeader("Explore\nTTD=" + TTD + "\n" + d.getName() + "\n" + d.getAddressString());
		}
		else {
			m.setHeader("Regular\n" + d.getName() + "\n" + d.getAddressString());
		}
		return m;
	}
	

	@Override
	public Message chooseBody(Message m) {
		m.setBody("Hello");
		return m;
	}

	@Override
	public void receiveMessage(Message m) {
		String[] data = m.getHeader().split("\n");
		System.out.println("RECEIVED: \n" + m);
		switch(data[0]) {
			case "Explore":
				Message ret = new Message();
				ret.setHeader("Return Explore\n" + d.getName() + "\n" + d.getAddressString());
				ret.setBody("Hello");
				ret.addDestination(new Address(data[data.length-1]));
				d.send(ret);
				if(!neighbors.contains(data[data.length - 1]) && !data[data.length - 1].equals(d.getAddressString())) {
					neighbors.add(data[data.length-1]);
				}
				break;
			case "Explore Return":
				if(!neighbors.contains(data[data.length - 1]) && !data[data.length - 1].equals(d.getAddressString())) {
					neighbors.add(data[data.length-1]);
				}
				break;
			default:
				received++;
				break;
		}
	}

	public MessagePattern copy() {
		return new EvenSpread();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type: " + TYPE + ", Time: " + time);
		sb.append("\nNeighbors: " + neighbors);
		sb.append("\nMessages Received: " + received);
		return sb.toString();
	}
	
}

