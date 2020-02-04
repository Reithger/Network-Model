package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import network.Address;
import network.Network;
import network.Node;
import network.Route;
import network.message.Message;
import network.protocol.message.TopToBottom;

class NetworkTest {

	@Test
	void test() {
		Node nodeA = new Node("A", "1", 0, 0);
		Node nodeB = new Node("B", "1.1", 5, 0);
		Node nodeC = new Node("C", "1.2", 0, 5);
		TopToBottom ttB = new TopToBottom();
		nodeA.setCommunicationProtocol(ttB);
		nodeB.setCommunicationProtocol(ttB);
		nodeC.setCommunicationProtocol(ttB);
		
		Route rt = nodeA.connect(nodeB, 1);
		Route rt2 = nodeA.connect(nodeC, 1);

		Network net = new Network();
		System.out.println(nodeA);
		System.out.println(nodeB);
		System.out.println(rt);
		net.addNode(nodeA);
		net.addNode(nodeB);
		net.addRoute(rt);
		net.addRoute(rt2);
		System.out.println(net);
		
		Address a = new Address("1.1.3.1");
		Address b = new Address("1.2.3.2");
		System.out.println(b.tear());
		System.out.println(a + "\n" + b);
		System.out.println(a.prefixMatch(b));
		
		Message m = new Message("1.2", "Hello");
		Message m2 = new Message("1", "Yo");
		Message m3 = new Message("1.1", "Hm");
		System.out.println(m);
		nodeB.receive(m);
		
		net.start();
		
		long start = System.currentTimeMillis();
		int count = 0;
		while(System.currentTimeMillis() - start < 10000) {
			count++;
			if(count % 10000000 == 0) {
				nodeB.receive(m);
				nodeA.receive(m3);
				nodeC.receive(m2);
			}
		}
		
	}
	
	public void Node() {
		Node a = new Node("a", "1", 0, 0);
	}

}
