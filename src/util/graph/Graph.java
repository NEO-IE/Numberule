//sg
package util.graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;
import util.Pair;
import util.Word;

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

	private Graph() {
		adj = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < MAX; i++) {
			adj.add(new ArrayList<Integer>());
		}
		nodeWordMap = new HashMap<Integer, String>();
		wordNodeMap = new HashMap<String, Integer>();
		modifierMap = new HashMap<>(); 
	}

	public static Graph makeDepGraph(Iterator<TypedDependency> tdi) {
		Graph depGraph = new Graph();

		/**
		 * Add nodes to the graph
		 */
		while (tdi.hasNext()) {
			TypedDependency td1 = tdi.next();
			
			TreeGraphNode depNode = td1.dep();
			TreeGraphNode govNode = td1.gov();
			
			depGraph.addNode(depNode.index(), depNode.value());
			depGraph.addNode(govNode.index(), govNode.value());
			depGraph.addEdge(depNode.index(), govNode.index());
			depGraph.addEdge(govNode.index(), depNode.index());
			//System.out.println("dep : " + depNode.value() + " gov : " + govNode.value());
			depGraph.addModifier(govNode.index(), govNode.value(), depNode.value());
			
			//System.out.println(govNode.value() + " -> " + depNode.value());
		}
		depGraph.listModifiers();
		return depGraph;

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
	
	public ArrayList<Word> getWordsOnPath(String src, String des) {
		int srcNode = wordNodeMap.get(src);
		int desNode = wordNodeMap.get(des);
		ArrayList<Integer> path = BFS.getPath(this, srcNode, desNode);
		ArrayList<Word> res = new ArrayList<Word>();
		for(Integer node : path) {
			res.add(new Word(node, nodeWordMap.get(node).trim().toLowerCase()));
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