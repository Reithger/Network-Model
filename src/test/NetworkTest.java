package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import network.Network;
import network.Node;
import network.Route;

class NetworkTest {

	@Test
	void test() {
		Network net = new Network();
		Node nodeA = new Node("A", "1", 0, 0);
		Node nodeB = new Node("B", "1.1", 5, 0);
		Route rt = new Route(1);
		rt.assign(nodeA, nodeB);
		System.out.println(nodeA);
		System.out.println(nodeB);
		System.out.println(rt);
		net.addNode(nodeA);
		net.addNode(nodeB);
		net.addRoute(rt);
		System.out.println(net);
	}
	
	public void Node() {
		Node a = new Node("a", "1", 0, 0);
	}

}
