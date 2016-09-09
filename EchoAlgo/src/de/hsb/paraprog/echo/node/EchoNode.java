package de.hsb.paraprog.echo.node;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.paraprog.echo.algo.SpanningTree;

public class EchoNode extends NodeAbstract {
	
	private static Logger logger = LoggerFactory.getLogger(EchoNode.class);

	public EchoNode(String name, boolean initiator, CountDownLatch startLatch, CountDownLatch endLatch) {
		super(name, initiator, startLatch);
		initNode = null;
		msgCnt = 0;
		tree = new SpanningTree(this);
		start = startLatch;
		end = endLatch;
	}
	
	private Node initNode;
	private int msgCnt;
	private SpanningTree tree;
	private CountDownLatch start;
	private CountDownLatch end;
	
	private boolean awake() {
		return initNode != null || initiator;
	}
	
	private void printTree() {
		logger.info("all nodes successfully initiated!");
		tree.print();
	}
	
	public void printNeighbours() {
		StringBuilder output = new StringBuilder("");
		output.append(name + ": my neighbours are | ");
		for (Node node : neighbours) {
			output.append((node.toString() + " "));
		}
		logger.info(output.toString());
	}

	@Override
	public void hello(Node neighbour) {
		neighbours.add(neighbour);
		logger.debug(this.toString() + ": " + "new hello from neighbour (" + neighbour.toString() + ")");
	}

	@Override
	public synchronized void wakeup(Node neighbour) {
		if (!awake()) {
			initNode = neighbour;
		}
		++msgCnt;
		notifyAll();
	}
	
	@Override
	public synchronized void echo(Node neighbour, Object data) {
		logger.debug(neighbour.toString() + ": echo send!");
		++msgCnt;
		tree.addSubTree(data);
		notifyAll();
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		for (Node node : neighbours) {
        	this.neighbours.add(node);
        	logger.debug(this.toString() + ": " + "added new node to my neighbours (" + node.toString() + ")");
        	node.hello(this);
        }
	}
	
	@Override
	public void run() {
		try {
			start.await();
			logger.debug("starting run");
			while (true) {
				synchronized(this) {
					if (initiator) {
						logger.debug(this.toString() + ": waiting...");
						wait((long) (Math.random() * 2000) + 1000);
//						wait((long) (10));
					}
					while (!awake()) {
						logger.debug(this.toString() + ": waiting for awakening...");
						wait();
					}
				}
				
				logger.debug(this.toString() + ": I am awake, waking up neighbours...");
				wakeupNeighbours();
				
				synchronized(this) {
					while (msgCnt != neighbours.size()) {
						logger.debug(this.toString() + ": waiting for neighbours...");
						wait();
					}
				}
				
				if (initiator) {
					printTree();
				} else {
					initNode.echo(this, tree);
				}
				end.countDown();
				end.await();
				
				if (initiator) {
					logger.info("starting new cycle!\n");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.debug(this.toString() + ": terminating...");
	}
	
	private void wakeupNeighbours() {
		Iterator<Node> iter = neighbours.iterator();
		while (iter.hasNext()) {
			Node current = iter.next();
			if (current != this.initNode) {
				current.wakeup(this);
			}
        }
	}
	
}
