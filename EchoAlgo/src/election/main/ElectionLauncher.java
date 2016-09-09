package election.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import election.node.ElectionNode;

public class ElectionLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(ElectionLauncher.class);

	public static void main(String[] args) {
		logger.debug("application start!");
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(7);
		List<ElectionNode> echo = new ArrayList<ElectionNode>();
		
		echo.add(new ElectionNode("0", true, start, end));
		
//		echo.add(new ElectionNode("1", false, start, end));
//		echo.add(new ElectionNode("2", false, start, end));
//		echo.add(new ElectionNode("3", false, start, end));
//		echo.add(new ElectionNode("4", false, start, end));
//		echo.add(new ElectionNode("5", false, start, end));
//		echo.add(new ElectionNode("6", false, start, end));
//		echo.add(new ElectionNode("7", false, start, end));
		
//		echo.get(0).setupNeighbours(echo.get(0));
		
//		echo.get(0).setupNeighbours(echo.get(1));
//		echo.get(0).setupNeighbours(echo.get(2));
//		echo.get(0).setupNeighbours(echo.get(3));
//		echo.get(0).setupNeighbours(echo.get(4));
//		echo.get(1).setupNeighbours(echo.get(2));
////		echo.get(1).setupNeighbours(echo.get(5));
//		echo.get(2).setupNeighbours(echo.get(3));
//		echo.get(2).setupNeighbours(echo.get(5));
//		echo.get(5).setupNeighbours(echo.get(6));
//		echo.get(7).setupNeighbours(echo.get(0));
		
		for (ElectionNode ElectionNode : echo) {
			ElectionNode.printNeighbours();
			ElectionNode.start();
		}
		
		logger.info("startLatch go!");
		start.countDown();
	}

}
