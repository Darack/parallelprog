package election;

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

public class ElectionLauncher {
	
	private static Logger logger = LoggerFactory.getLogger(ElectionLauncher.class);
	private static int TESTCASES = 2;
	private static String PROG_NAME = "ELECTION_ALGO";

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
		List<ElectionNode> nodes = new ArrayList<ElectionNode>();
		
		switch(testCase) {
		case 1:
			// create nodes
			nodes.add(new ElectionNode(0, start));
			
			break;
		case 2:
			// create nodes
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
			
			// create connections
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
			
			break;
		}
		
		for (ElectionNode ElectionNode : nodes) {
			ElectionNode.printNeighbours();
			ElectionNode.start();
		}
		
		logger.info("startLatch go!");
		start.countDown();
	}

}
