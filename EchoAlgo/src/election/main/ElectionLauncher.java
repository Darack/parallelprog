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
		List<ElectionNode> nodes = new ArrayList<ElectionNode>();
		
		nodes.add(new ElectionNode(0, start));
		nodes.add(new ElectionNode(1, start));
		nodes.add(new ElectionNode(2, start));
		nodes.add(new ElectionNode(3, start));
		nodes.add(new ElectionNode(4, start));
		nodes.add(new ElectionNode(5, start));
		nodes.add(new ElectionNode(6, start));
		nodes.add(new ElectionNode(7, start));
		nodes.add(new ElectionNode(8, start));
		nodes.add(new ElectionNode(9, start));
		nodes.add(new ElectionNode(10, start));
		nodes.add(new ElectionNode(11, start));
		

		nodes.get(0).setupNeighbours(nodes.get(1));
		nodes.get(0).setupNeighbours(nodes.get(2));
		nodes.get(1).setupNeighbours(nodes.get(3));
		nodes.get(3).setupNeighbours(nodes.get(4));
		nodes.get(3).setupNeighbours(nodes.get(5));
		nodes.get(3).setupNeighbours(nodes.get(6));
		nodes.get(6).setupNeighbours(nodes.get(7));
		nodes.get(7).setupNeighbours(nodes.get(8));
		nodes.get(8).setupNeighbours(nodes.get(9));
		nodes.get(9).setupNeighbours(nodes.get(10));
		nodes.get(10).setupNeighbours(nodes.get(11));
		
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
		
		for (ElectionNode ElectionNode : nodes) {
			ElectionNode.printNeighbours();
			ElectionNode.start();
		}
		
		logger.info("startLatch go!");
		start.countDown();
	}

}
