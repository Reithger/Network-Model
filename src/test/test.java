package test;

import display.Map;
import network.Network;
import network.protocol.message.TopToBottom;

public class test {

	public static void main(String[] args) {
		TopToBottom ttB = new TopToBottom();
		Network net2 = new Network();
		net2.addNode("A", "1", 0, 0, ttB);
		net2.addNode("B", "1.1", 1.4, 0, ttB);
		net2.addNode("C", "1.2", 0, 1, ttB);
		net2.addNode("D", "1.1.1", 1, 1, ttB);
		net2.addNode("E", "1.2.1", 3, 2, ttB);
		net2.addRoute("A", "B", 1);
		net2.addRoute("A", "C", 1);
		net2.addRoute("B", "D", 1);
		net2.addRoute("E", "B", 1);

		net2.start();
		Map map = new Map();
		
		map.setNetwork(net2);
		map.updateMap();
	}
	
}
