package de.hsb.paraprog.echo.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.paraprog.echo.node.EchoNode;

public class EchoLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(EchoLauncher.class);

	public static void main(String[] args) {
		logger.debug("application start!");
		CountDownLatch start = new CountDownLatch(1);
		List<EchoNode> echo = new ArrayList<EchoNode>();
		
		echo.add(new EchoNode("0", true, start));
		echo.add(new EchoNode("1", false, start));
		echo.add(new EchoNode("2", false, start));
		echo.add(new EchoNode("3", false, start));
		echo.add(new EchoNode("4", false, start));
		echo.add(new EchoNode("5", false, start));
		echo.add(new EchoNode("6", false, start));
		
		echo.get(0).setupNeighbours(echo.get(1));
		echo.get(0).setupNeighbours(echo.get(2));
		echo.get(0).setupNeighbours(echo.get(3));
		echo.get(0).setupNeighbours(echo.get(4));
		echo.get(1).setupNeighbours(echo.get(2));
		echo.get(1).setupNeighbours(echo.get(5));
		echo.get(2).setupNeighbours(echo.get(3));
		echo.get(2).setupNeighbours(echo.get(5));
		echo.get(5).setupNeighbours(echo.get(6));
		
		for (EchoNode echoNode : echo) {
			echoNode.printNeighbours();
			echoNode.start();
		}
		
		start.countDown();
	}

}
