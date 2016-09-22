package election.node;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import echo.node.EchoNode;

public class ElectionNode extends Thread {
	class Pair<T1, T2>{
		Pair(T1 first, T2 second){
			this.first = first;
			this.second = second;
		}
		public T1 first;
		public T2 second;
	}
	private int id;
	private CountDownLatch startLatch;
	private List<ElectionNode> neighbours;
	
	private volatile boolean wakeUpCalled; // TODO
	private volatile boolean messageSent;
	
	private int msgCnt;
	
	private boolean initiator;
	
	
	private static Logger logger = LoggerFactory.getLogger(EchoNode.class);
	

	private Queue<Pair<ElectionNode, Integer> > CallerBuffer;
	private Queue<ElectionNode> CallerQueue;
	private int electedId;
	
	private static final Long ERICS_WEIGHT = Long.MAX_VALUE;
	private static final float INITIATOR_CHANCE = 10;
	private static final int MAX_WAIT = 10;
	
	private static final boolean RUN = true;
	
	public ElectionNode(int id, CountDownLatch startLatch, List<ElectionNode> neighbours) {
		this.id = id;
		this.startLatch = startLatch;
		this.neighbours = neighbours;
		messageSent = false;
		msgCnt = 0;
		initiator = false;
		CallerBuffer = new LinkedList();
		CallerQueue = new LinkedList();
		electedId = -1;
	}
	
	@Override
	public void run() {
		try {
			startLatch.await();
		} catch (InterruptedException e1) {
			logger.debug(e1.getMessage());
		}
		logger.debug("starting run");
		
		while (RUN) {
			if (!initiator && Math.random() * 100 < INITIATOR_CHANCE) {
				initiate();
			}
			
			synchronized (this) {
				try {
					if (wakeUpCalled()) {
						wait((int) (Math.random() * MAX_WAIT)); // http://stackoverflow.com/questions/13249835/java-does-wait-release-lock-from-synchronized-block
					}
				} catch (InterruptedException e) {
					logger.debug(e.getMessage());
				} 
			}
			
			if ( wakeUpCalled() ) {
				int greatest = electedId;
				ElectionNode node = null;
				synchronized (this) {
					for (Pair<ElectionNode, Integer> p : CallerBuffer) {
						if (electedId < p.second) {
							electedId = p.second;
							node = p.first;
						}
						CallerQueue.add(p.first);
					}
					CallerBuffer.clear();
				}
				if(node != null) {
					sendWave(node, greatest);
				} else {
					sendResult();
				}
					
			}
			if (messageSent && msgCnt == 0) {
				sendResult();
				if (initiator && id == electedId) {
					// TODO echo
				}
			}
		}
	}
	
	private synchronized boolean wakeUpCalled(){
		return !CallerBuffer.isEmpty();
	}
	private void initiate() {
		
	}
	private synchronized void receive(int res){
		--msgCnt;
		if (res > electedId) {
			electedId = res;
		}
		notifyAll();
	}
	private void sendResult(){
		messageSent = false;
		for(ElectionNode n : CallerQueue){
			n.receive(electedId);
		}
		CallerQueue.clear();
	}
	private void sendWave(ElectionNode caller, int val){
		messageSent = true;
		for(ElectionNode n : neighbours ){
			if(n != caller){
				n.wakeUp(this, val);
				synchronized (this) {
					++msgCnt;
				}
			}
		}
	}
	private synchronized void wakeUp(ElectionNode caller, int id){
		CallerBuffer.add(new Pair<>(caller, id) );
		notifyAll();
	}
	
}
