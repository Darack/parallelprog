package de.hsb.paraprog.echo.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(EchoLauncher.class);

	public static void main(String[] args) {
		logger.debug("logger test!");
	}

}
