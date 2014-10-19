//sg
package util;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * An unweighted, undirected graph
 */
public class Graph {
	private ArrayList< ArrayList< Integer > > adj; // the adjacency list
	private HashMap<Integer, String> nodeWordMap; // map from node number to
													// the word
	private HashMap<String, Integer> wordNodeMap; // map from node number to
	// the word

	public final static int MAX = 1000;

	public Graph() {
		adj = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < MAX; i++) {
			adj.add(new ArrayList<Integer>());
		}
		nodeWordMap = new HashMap<Integer, String>();
		wordNodeMap = new HashMap<String, Integer>();
	}

	// undirected graph
	public void addEdge(int i, int j) {
	
		(adj.get(i)).add(j);
	}

	public ArrayList<Integer> getNbr(int curr) {
		return adj.get(curr);
	}
	
	public void addNode(int pos, String word) {
		nodeWordMap.put(pos, word);
		wordNodeMap.put(word, pos);
	}
	
	public ArrayList<String> getWordsOnPath(String src, String des) {
		int srcNode = wordNodeMap.get(src);
		int desNode = wordNodeMap.get(des);
		ArrayList<Integer> path = BFS.getPath(this, srcNode, desNode);
		ArrayList<String> res = new ArrayList<String>();
		for(Integer node : path) {
			
			res.add(nodeWordMap.get(node));
		}
		return res;
	}
	
	public String getLabel(int num) {
		return nodeWordMap.get(num);
	}
}