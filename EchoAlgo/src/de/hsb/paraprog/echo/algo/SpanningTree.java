package de.hsb.paraprog.echo.algo;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.paraprog.echo.node.EchoNode;
import de.hsb.paraprog.echo.node.Node;

public class SpanningTree {
	
	private static Logger logger = LoggerFactory.getLogger(EchoNode.class);
	
	class TreeNode {
		public TreeNode(Node node) {
			this.node = node;
			descendants = new HashSet<TreeNode>();
		}
		private Node node;
		private Set<TreeNode> descendants;
		
		public void printSection() {
			logger.info(node.toString());
			for (TreeNode desc : descendants) {
				desc.printSection();
			}
		}
	}
	
	public SpanningTree(Node node) {
		m_Root = new TreeNode(node);
	}
	private TreeNode m_Root;
	
	public void print() {
		logger.info("spanning tree: ");
		m_Root.printSection();
	}
	public void addSubTree(Object tree) {
		m_Root.descendants.add(((SpanningTree) tree).m_Root);
	}
	
}
