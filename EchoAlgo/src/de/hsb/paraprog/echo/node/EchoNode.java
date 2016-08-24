package de.hsb.paraprog.echo.node;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoNode extends NodeAbstract {
	
	private static Logger logger = LoggerFactory.getLogger(EchoNode.class);

	public EchoNode(String name, boolean initiator, CountDownLatch startLatch) {
		super(name, initiator, startLatch);
		initNode = null;
		msgCnt = 0;
	}
	
	private Node initNode;
	private int msgCnt;
	
	private boolean awake() {
		return !initNode.equals(null) || initiator;
	}
	
	private void printTree() {
		logger.info("all nodes successfully initiated!");
		// TODO print tree
	}
	
	public void printNeighbours() {
		logger.info(name + "\tmy neighbours are: ");
		StringBuilder output = new StringBuilder("");
		for (Node node : neighbours) {
			output.append((node.toString() + " "));
		}
		output.append("\n");
		logger.info(output.toString());
	}

	@Override
	public void hello(Node neighbour) {
		neighbours.add(neighbour);
		logger.debug(this.toString() + ": " + "new hello from neighbour (" + neighbour.toString() + ")");
	}

	@Override
	public void wakeup(Node neighbour) {
		if (!awake()) {
			initNode = neighbour;
			Iterator<Node> it = neighbours.iterator();
			while (it.hasNext()) {
				Node node = it.next();
				if (!node.equals(initNode)) {
					node.wakeup(this);
				}
			}
		}
		++msgCnt;
		if (msgCnt == neighbours.size()) {
			if (initiator) {
				printTree();
			} else {
				echo(initNode, null);
			}
		}
	}
	
	@Override
	public void echo(Node neighbour, Object data) {
		++msgCnt;
		if (initiator && msgCnt == neighbours.size()) {
			printTree();
		}
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		for (Node node : neighbours) {
        	this.neighbours.add(node);
        	logger.debug(this.toString() + ": " + "added new node to my neighbours (" + node.toString() + ")");
        	hello(node);
        }
	}

}
