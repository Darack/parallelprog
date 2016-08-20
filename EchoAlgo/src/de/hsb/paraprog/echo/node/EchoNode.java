package de.hsb.paraprog.echo.node;

import java.util.concurrent.CountDownLatch;

public class EchoNode extends NodeAbstract {

	public EchoNode(String name, boolean initiator, CountDownLatch startLatch) {
		super(name, initiator, startLatch);
		initNode = null;
		msgCnt = 0;
	}
	
	private Node initNode;
	private int msgCnt;
	
	private boolean isInitiator() {
		return initiator;
	}

	@Override
	public void hello(Node neighbour) {
		neighbours.add(neighbour);
	}

	@Override
	public void wakeup(Node neighbour) {
		// TODO Auto-generated method stub
		if (initNode.equals(null) && !isInitiator()) {
			initNode = neighbour;
		}
		++msgCnt;
		if (msgCnt == neighbours.size()) {
			// TODO do stuff depending on bool initiator (echo or success msg)
		}
	}
	
	@Override
	public void echo(Node neighbour, Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		// TODO Auto-generated method stub
		
	}

}
