package echo.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import echo.node.EchoNode;

public class EchoLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(EchoLauncher.class);

	public static void main(String[] args) {
		logger.debug("application start!");
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(7);
		List<EchoNode> echo = new ArrayList<EchoNode>();
		
		echo.add(new EchoNode("0", true, start, end));
		
//		echo.add(new EchoNode("1", false, start, end));
//		echo.add(new EchoNode("2", false, start, end));
//		echo.add(new EchoNode("3", false, start, end));
//		echo.add(new EchoNode("4", false, start, end));
//		echo.add(new EchoNode("5", false, start, end));
//		echo.add(new EchoNode("6", false, start, end));
//		echo.add(new EchoNode("7", false, start, end));
		
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
		
		for (EchoNode echoNode : echo) {
			echoNode.printNeighbours();
			echoNode.start();
		}
		
		logger.info("startLatch go!");
		start.countDown();
	}

}
