//sg
package util.graph;
import java.util.ArrayList;
import java.util.HashMap;

import util.Pair;

/*
 * An unweighted, undirected graph
 */
public class Graph {
	private ArrayList< ArrayList< Integer > > adj; // the adjacency list
	private HashMap<Integer, String> nodeWordMap; // map from node number to
													// the word
	private HashMap<String, Integer> wordNodeMap; // map from node number to
	// the word
	
	private HashMap<Pair<Integer, String>, String> modifierMap; //given a string, checks if there is a modifier
	
	public final static int MAX = 1000;

	public Graph() {
		adj = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < MAX; i++) {
			adj.add(new ArrayList<Integer>());
		}
		nodeWordMap = new HashMap<Integer, String>();
		wordNodeMap = new HashMap<String, Integer>();
		modifierMap = new HashMap<>(); 
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
			res.add(nodeWordMap.get(node).trim().toLowerCase());
		}
		return res;
	}
	
	public String getLabel(int num) {
		return nodeWordMap.get(num);
	}

	public void addModifier(int modifiedIdx, String modifiedVal, String modifier) {
		modifierMap.put(new Pair<Integer, String> (modifiedIdx, modifiedVal), modifier);
	}
	
	public void listModifiers() {
		for(Pair<Integer, String> op : modifierMap.keySet()) {
			System.err.println(op.second + " -> " + modifierMap.get(op));
		}
	}
}