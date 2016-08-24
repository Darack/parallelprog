package de.hsb.paraprog.echo.main;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.paraprog.echo.node.EchoNode;

public class EchoLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(EchoLauncher.class);

	public static void main(String[] args) {
		logger.debug("application start!");
		CountDownLatch start = new CountDownLatch(1);
		EchoNode en1 = new EchoNode("master", true, start);
		EchoNode en2 = new EchoNode("slave", false, start);
		en1.setupNeighbours(en2);
	}

}
