package election.algo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import election.node.ElectionNode.ExtendedEchoNode;

public class SpanningTree {
	
	private static Logger logger = LoggerFactory.getLogger(SpanningTree.class);
	
	class TreeNode {
		public TreeNode(ExtendedEchoNode extendedEchoNode) {
			this.node = extendedEchoNode;
			descendants = new HashSet<TreeNode>();
		}
		private ExtendedEchoNode node;
		public String father = "";
		public Set<TreeNode> descendants;
		
		public String toString() {
			return node.toString() + "(" + father + ")";
		}
		public String getName() {
			return node.toString();
		}
	}
	
	public SpanningTree(ExtendedEchoNode extendedEchoNode) {
		m_Root = new TreeNode(extendedEchoNode);
	}
	private TreeNode m_Root;
	Queue<TreeNode> queue = new LinkedList<TreeNode>();
	
	public void print() {
		logger.info("spanning tree: ");
		queue.add(m_Root);
		while (!queue.isEmpty()) {
			printRow();
		}
	}
	
	private void printRow() {
		StringBuilder nodeRow = new StringBuilder("");
		for (TreeNode node : queue) {
			nodeRow.append((node.toString() + " | "));
		}
		logger.info(nodeRow.toString());
		
		for (int i = queue.size(); i > 0; --i) {
			TreeNode node = queue.poll();
			for (TreeNode desc : node.descendants) {
				queue.add(desc);
				desc.father = node.getName();
			}
		}
	}
	
	public void addSubTree(Object tree) {
		m_Root.descendants.add(((SpanningTree) tree).m_Root);
	}
	
}
