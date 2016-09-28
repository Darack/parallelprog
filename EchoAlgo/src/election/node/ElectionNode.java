package election.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import election.algo.SpanningTree;


public class ElectionNode extends Thread {
	class Pair<T1, T2>{
		Pair(T1 first, T2 second){
			this.first = first;
			this.second = second;
		}
		public T1 first;
		public T2 second;
	}
	public class ExtendedEchoNode implements Runnable{
		
		public ExtendedEchoNode(String name, CountDownLatch startLatch) {
			initNode = null;
			msgCnt = 0;
			tree = new SpanningTree(this);
			start = startLatch;
		}
		
		private ExtendedEchoNode initNode;
		private int msgCnt;
		private SpanningTree tree;
		private CountDownLatch start;
		private boolean echoInitiator;
		
		public ElectionNode getParent(){
			return ElectionNode.this;
		}
		private boolean awake() {
			return initNode != null || echoInitiator;
		}
		
		private void printTree() {
			logger.info("all nodes successfully initiated!");
			tree.print();
		}
		
		public void printNeighbours() {
			StringBuilder output = new StringBuilder("");
			output.append(id + ": my neighbours are | ");
			for (ElectionNode node : ElectionNode.this.neighbours) {
				output.append((node.toString() + " "));
			}
			logger.info(output.toString());
		}

		public synchronized void wakeup(ExtendedEchoNode neighbour) {
			if (!awake()) {
				initNode = neighbour;
			}
			++msgCnt;
			notifyAll();
		}
		
		public synchronized void echo(ExtendedEchoNode neighbour, Object data) {
			logger.debug(neighbour.toString() + ": echo send!");
			++msgCnt;
			tree.addSubTree(data);
			notifyAll();
		}
		
		private synchronized void echoNodeSetInitiator() {
			echoInitiator = true;
			notifyAll();
		}
		
		public void run() {
			try {
				start.await();
				logger.debug("starting run");
				
				synchronized(this) {
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
				
				if (echoInitiator) {
					printTree();
					echoCompleted();
				} else {
					initNode.echo(this, tree);
				}
//				end.countDown();
//				end.await();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			logger.debug(this.toString() + ": terminating...");
		}
		
		private void wakeupNeighbours() {
			Iterator<ElectionNode> iter = neighbours.iterator();
			while (iter.hasNext()) {
				ExtendedEchoNode current = iter.next().echoNode;
				if (current != this.initNode) {
					current.wakeup(this);
				}
	        }
		}
		
		@Override
		public String toString() {
			return id + "";
		}
	}
	interface Executable{
		public void execute();
	}
	private int id;
	private CountDownLatch startLatch;
	private List<ElectionNode> neighbours;
	
	private ExtendedEchoNode echoNode;
	
	private volatile boolean messageSent;
	
	private int msgCnt;
	
	private boolean initiator;
	
	private static Logger logger = LoggerFactory.getLogger(ElectionNode.class);

	private Queue<Pair<ElectionNode, Integer> > CallerBuffer;
	private Queue<ElectionNode> CallerQueue;
	private Queue<Executable> ExecutableQueue;
	private int electedId;
	
	private Map<Integer, Integer> receivedSet;
	
	private boolean isAllowedToCandidate;
	
	private static final Long ERICS_WEIGHT = Long.MAX_VALUE;
	private static final float INITIATOR_CHANCE = 10f;
	private static final int MAX_WAIT = 10;
	
	private static final boolean RUN = true;
	
	public ElectionNode(int id, CountDownLatch startLatch) {
		this.id = id;
		this.startLatch = startLatch;
		echoNode = new ExtendedEchoNode(String.valueOf(id), startLatch);
		new Thread(echoNode).start();
		neighbours = new LinkedList<>();
		messageSent = false;
		msgCnt = 0;
		initiator = false;
		CallerBuffer = new LinkedList<Pair<ElectionNode, Integer>>();
		CallerQueue = new LinkedList<ElectionNode>();
		ExecutableQueue = new LinkedList<>();
		electedId = -1;
		receivedSet = new TreeMap<>();
		isAllowedToCandidate = true;
	}
	
	@Override
	public void run() {
		// TODO Schleife
		
		logger.debug("starting run");
		
		while (RUN) {
			try {
				startLatch.await();
			} catch (InterruptedException e1) {
				logger.debug(e1.getMessage());
			}
			synchronized (System.out) {
				System.out.print("Node Nr."+id + " msgCnt: " + msgCnt + " callerQueue: " + CallerQueue.size() + " isWhite? "+isAllowedToCandidate);
				for (ElectionNode n : CallerQueue) {
					System.out.print(" | nodeId " + n.id + " nodeElected " + n.electedId + " init? " + n.initiator);
				}
				System.out.print(" Buffer{ ");
				for(Pair<ElectionNode, Integer> n : CallerBuffer){
					System.out.print(" | nodeId " + n.first.id + " currentElected " + electedId+ " offer "+n.second);
				}
				System.out.print(" } ");
				System.out.println();
			}
			
			Queue<Executable> tempExecutableQueue = new LinkedList<>();
			synchronized (this) {
				for(Executable e : ExecutableQueue){
					tempExecutableQueue.add(e);
				}
				ExecutableQueue.clear();
			}
			for(Executable e : tempExecutableQueue){
				e.execute();
			}
			
//			logger.debug("RUN");
			if (!initiator && isAllowedToCandidate && Math.random() * 100 < INITIATOR_CHANCE && ERICS_WEIGHT > Integer.MAX_VALUE) {
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
				int tmpElectedId;
				List<Pair<ElectionNode,Integer> > tmpList = new LinkedList<>();
				synchronized (this) {
					for (Pair<ElectionNode, Integer> p : CallerBuffer) {
						if (electedId < p.second) {
							electedId = p.second;
							node = p.first;
						}
					}
					tmpElectedId = electedId;
					for( Pair<ElectionNode, Integer> p : CallerBuffer ){
						tmpList.add(p);
					}
					CallerBuffer.clear();
				}
				
				if(node != null) {
					for (ElectionNode n : CallerQueue) {
						n.receive(tmpElectedId, this);
					}
					CallerQueue.clear();
					//CallerQueue.add(node);
					sendWave(node, tmpElectedId);
				}
				
				for(Pair<ElectionNode, Integer> n : tmpList){
					if (n.second != tmpElectedId || (node != null && n.first != node)) {
						System.out.println("Me Nr."+this.id+" eliminated wave! kill, kill them all! " + n.second);
						n.first.receive(tmpElectedId, this);
					} else {
						CallerQueue.add(n.first);
					}
					rememberReceived(n.first, n.second);
				}
				
			}
			if (messageSent && msgCnt == 0) {
				sendResult();
				if (initiator && id == electedId) {
					logger.info("Me "+id+" has won the fucking WAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
					echoNode.echoNodeSetInitiator();
				}
			}
			
			try { // TODO entfernen
				Thread.sleep(200);
			} catch (InterruptedException e) {
				logger.debug(e.toString());
			}
		}
	}
	
	private void echoCompleted() {
		System.out.println("Echo Completed by "+id);
		CountDownLatch latchStart = new CountDownLatch(1);
		for(ElectionNode n : echoNode.tree.getTree() ){
			if(n != this){
				n.electedId = -1;
				n.startLatch = latchStart;
				n.isAllowedToCandidate = true;
				n.initiator = false;
				n.messageSent = false;
				n.echoNode.tree.reset(); //= new SpanningTree(n.echoNode);
				n.echoNode.start = latchStart;
				n.echoNode.echoInitiator = false;
				n.echoNode.initNode = null;
				n.echoNode.msgCnt = 0;
//				n.echoNode = new ExtendedEchoNode(n.id + "", latchStart);
				new Thread(n.echoNode).start();
			}
		}
		electedId = -1;
		startLatch = latchStart;
		isAllowedToCandidate = true;
		initiator = false;
		messageSent = false;
		echoNode.tree.reset(); // = new SpanningTree( echoNode );
		echoNode.start = latchStart;
		echoNode.echoInitiator = false;
		echoNode.initNode = null;
		echoNode.msgCnt = 0;
//		echoNode = new ExtendedEchoNode(id + "", latchStart);
		new Thread(echoNode).start();
		latchStart.countDown();
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
			for (ElectionNode electionNode : CallerQueue) {
				System.out.println("blaaa");
				electionNode.receive(electedId, this);
			}
			CallerQueue.clear();
			sendWave(null, id);
		} else {
			logger.debug("close init");
		}
		initiator = true;
	}
	
	private synchronized void receive(int res, ElectionNode n){
		System.out.println("Me "+this.id+" Received a "+res + " from " + n.id);
		boolean wasSmaller = false;
		if (res > electedId) {
			electedId = res;
			wasSmaller = true;		
		}
		notifyAll();
		if(wasSmaller){
			ExecutableQueue.add(new Executable(){
				public void execute(){
					System.out.println("Me "+ElectionNode.this.id+" Sending update with "+electedId);
					boolean wasInside = false;
					for (ElectionNode node : CallerQueue) {
						if (node != n) {
							node.receive(electedId, ElectionNode.this);
						}else{
							wasInside = true;
						}
					}
					CallerQueue.clear();
					if(wasInside){
						CallerQueue.add(n);
					}
					
					sendWave(n, res);
				}
			});
		}
	
		ExecutableQueue.add(new Executable() {
			
			@Override
			public void execute() {
				--msgCnt;
				rememberReceived(n, res);
			}
		});
	}
	
	private void rememberReceived(ElectionNode key, int value) {
		receivedSet.put(key.id, value);
		boolean containsAll = true;
		for (ElectionNode node : neighbours) {
			if (!(receivedSet.containsKey(node.id) && receivedSet.get(node.id) == value)) {
				containsAll = false;
			}
		}
		if (containsAll && value != id) {
			synchronized (System.out) {
				
				System.out.print("CYCLE DEDECTION FROM "+id+" {");
				for(Map.Entry<Integer,Integer> i : receivedSet.entrySet() ){
					System.out.print( "Key: "+i.getKey()+" Value: "+i.getValue()+ " | ");
				}
				System.out.println(" }");
			}
			for (ElectionNode node : CallerQueue) {
				node.receive(value, this);
			}
			CallerQueue.clear();
		}
	}
	
	private void sendResult(){
		System.out.println("Me "+this.id+" Sending rersult with "+electedId);
		messageSent = false;
		isAllowedToCandidate = false;
		for(ElectionNode n : CallerQueue){
			n.receive(electedId, this);
		}
		CallerQueue.clear();
	}
	private void sendWave(ElectionNode caller, int val){
		for(ElectionNode n : neighbours ){
			if(n != caller){
				n.wakeUp(this, val);
				synchronized (this) {
					++msgCnt;
				}
			}
		}
		messageSent = true;
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
