package election.node;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import interfaces.Node;


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
	
	private volatile boolean messageSent;
	
	private int msgCnt;
	
	private boolean initiator;
	
	private static Logger logger = LoggerFactory.getLogger(ElectionNode.class);

	private Queue<Pair<ElectionNode, Integer> > CallerBuffer;
	private Queue<ElectionNode> CallerQueue;
	private int electedId;
	
	private static final Long ERICS_WEIGHT = Long.MAX_VALUE;
	private static final float INITIATOR_CHANCE = 10f;
	private static final int MAX_WAIT = 10;
	
	private static final boolean RUN = true;
	
	public ElectionNode(int id, CountDownLatch startLatch) {
		this.id = id;
		this.startLatch = startLatch;
		neighbours = new LinkedList<>();
		messageSent = false;
		msgCnt = 0;
		initiator = false;
		CallerBuffer = new LinkedList<Pair<ElectionNode, Integer>>();
		CallerQueue = new LinkedList<ElectionNode>();
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
			System.out.println("Node Nr."+id + " msgCnt: " + msgCnt);
//			logger.debug("RUN");
			if (!initiator && Math.random() * 100 < INITIATOR_CHANCE && ERICS_WEIGHT > 1000) {
				logger.debug("INIT");
				initiate();
			}
			
			synchronized (this) {
				try {
					if (!wakeUpCalled()) {
						
						long timeout = (long) (Math.random() * MAX_WAIT);
						if(timeout != 0)
							wait(timeout); // http://stackoverflow.com/questions/13249835/java-does-wait-release-lock-from-synchronized-block
						
					}
				} catch (InterruptedException e) {
					
					logger.debug(e.getMessage());
				}
			}
			
			if ( wakeUpCalled() ) {
				
				ElectionNode node = null;
				synchronized (this) {
					for (Pair<ElectionNode, Integer> p : CallerBuffer) {
						if (electedId < p.second) {
							electedId = p.second;
							node = p.first;
							
						}
						
					}
					
					
					for(Pair<ElectionNode, Integer> n : CallerBuffer){
						if (n.first != node) {
							System.out.println("Me Nr."+this.id+" eliminated wave! kill, kill them all! ");
							n.first.receive(electedId);
						}
					}
					
					CallerBuffer.clear();
				}
				if(node != null) {
					CallerQueue.add(node);
					sendWave(node, electedId);
				}
					
			}
			if (messageSent && msgCnt == 0) {
				sendResult();
				if (initiator && id == electedId) {
					logger.info("I WON!!!!!11!!");
					// TODO echo
				}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				logger.debug(e.toString());
			}
		}
	}
	
	private synchronized boolean wakeUpCalled(){
		return !CallerBuffer.isEmpty();
	}
	
	private void initiate() {
		boolean b;
		synchronized (this) {
			b = id > electedId;
		}
		if (b) {
			synchronized (this) {
				electedId = id;
			}
			sendWave(null, id);
		} else {
			logger.debug("close init");
		}
		initiator = true;
	}
	
	private synchronized void receive(int res){
		System.out.println("Me "+this.id+" Received with "+res);
		--msgCnt;
		if (res > electedId) {
			electedId = res;
		}
		notifyAll();
	}
	
	private void sendResult(){
		System.out.println("Me "+this.id+" Sending rersult with "+electedId);
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
		System.out.println("Me Nr."+this.id+ " was waked up by "+caller.id+" with: "+id);
		CallerBuffer.add(new Pair<>(caller, id) );
		notifyAll();
	}
	
	public void printNeighbours() {
		StringBuilder output = new StringBuilder("");
		output.append(id + ": my neighbours are | ");
		for (ElectionNode node : neighbours) {
			output.append((node.id + " "));
		}
		logger.info(output.toString());
	}
	
	public void setupNeighbours(ElectionNode... neighbours) {
		for (ElectionNode node : neighbours) {
        	this.neighbours.add(node);
        	logger.debug(this.toString() + ": " + "added new node to my neighbours (" + node.toString() + ")");
        	node.hello(this);
        }
	}

	public void hello(ElectionNode neighbour) {
		neighbours.add(neighbour);
		logger.debug(this.toString() + ": " + "new hello from neighbour (" + neighbour.toString() + ")");
	}
}
