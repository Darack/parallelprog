package echo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(EchoLauncher.class);
	private static int TESTCASES = 4;
	private static String PROG_NAME = "ECHO_ALGO";

	public static void main(String[] args) {
		logger.debug("application start!");
		
		// command line options
		Options options = new Options();
		String testOption = "test";
		options.addOption(testOption, true, "test case to check, pick a value between [1-" + TESTCASES + "]");
		
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		
		// default values
		int testCase = 1;
		
		// get command line parameters
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			
			logger.debug("debug");
			
			if(cmd.hasOption(testOption)) {
				testCase = Integer.parseInt(cmd.getOptionValue(testOption));
				logger.debug("testcase: " + testCase);
				if (testCase > TESTCASES) {
					throw new ParseException("wrong test case number!");
				}
			} else {
				logger.debug("else");
				formatter.printHelp( PROG_NAME, options, true );
				System.exit(1);
			}
		} catch (ParseException e1) {
			logger.debug("exception");
			formatter.printHelp( PROG_NAME, options, true );
			System.exit(1);
		}
				
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(7);
		List<EchoNode> echo = new ArrayList<EchoNode>();
		
		switch(testCase) {
		case 1:
			// create nodes
			echo.add(new EchoNode("0", true, start, end));
			
			break;
		case 2:
			// create nodes
			echo.add(new EchoNode("0", true, start, end));
			
			// create connections
			echo.get(0).setupNeighbours(echo.get(0));
			
			break;
		case 3:
			// create nodes
			echo.add(new EchoNode("0", true, start, end));
			echo.add(new EchoNode("1", false, start, end));
			echo.add(new EchoNode("2", false, start, end));
			echo.add(new EchoNode("3", false, start, end));
			echo.add(new EchoNode("4", false, start, end));
			echo.add(new EchoNode("5", false, start, end));
			
			// create connections
			echo.get(0).setupNeighbours(echo.get(1));
			echo.get(1).setupNeighbours(echo.get(2));
			echo.get(2).setupNeighbours(echo.get(3));
			echo.get(2).setupNeighbours(echo.get(4));
			echo.get(2).setupNeighbours(echo.get(5));
			
			break;
		case 4:
			// create nodes
			echo.add(new EchoNode("0", true, start, end));
			echo.add(new EchoNode("1", false, start, end));
			echo.add(new EchoNode("2", false, start, end));
			echo.add(new EchoNode("3", false, start, end));
			echo.add(new EchoNode("4", false, start, end));
			echo.add(new EchoNode("5", false, start, end));
			
			// create connections
			echo.get(0).setupNeighbours(echo.get(1));
			echo.get(1).setupNeighbours(echo.get(2));
			echo.get(2).setupNeighbours(echo.get(3));
			echo.get(2).setupNeighbours(echo.get(4));
			echo.get(2).setupNeighbours(echo.get(5));
			echo.get(0).setupNeighbours(echo.get(5));
			
			break;
		}
		
		for (EchoNode echoNode : echo) {
			echoNode.printNeighbours();
			echoNode.start();
		}
		
		logger.info("startLatch go!");
		start.countDown();
	}

}
