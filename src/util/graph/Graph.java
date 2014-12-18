//sg
package util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import meta.ModifyingTypes;
import util.Word;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * An unweighted, undirected graph The graph is stored in adjacency list
 * representation each of the nodes has a number, and there are maps that store
 * the string value stored at each of the nodes "Word" wraps both the value at a
 * position and the index
 */
public class Graph {
	private ArrayList<ArrayList<Integer>> adj; // the adjacency list
	private HashMap<Integer, String> nodeWordMap; // map from node number to
													// the word
	private HashMap<String, Integer> wordNodeMap; // map from node number to
	// the word

	private HashMap<Word, HashSet<Word> > modifiersMap; // given a word, checks if there is
												// a modifier

	public final static int MAX = 1000;

	private Graph() {
		adj = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < MAX; i++) {
			adj.add(new ArrayList<Integer>());
		}
		nodeWordMap = new HashMap<Integer, String>();
		wordNodeMap = new HashMap<String, Integer>();
		modifiersMap = new HashMap<>();
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
			// System.out.println("dep : " + depNode.value() + " gov : " +
			// govNode.value());
			// governor is being modified
			
			if (ModifyingTypes.isModifier(td1.reln().toString())	) {
				//BUG FIX, DEPENDENCIES HOLD ON BOTH THE SIDES
				Word govWord = new Word(govNode.index(), govNode.value());
				Word depWord = new Word(depNode.index(), depNode.value());
				depGraph.addModifier(govWord, depWord);
				depGraph.addModifier(depWord, govWord);
			}
			// System.out.println(govNode.value() + " -> " + depNode.value());
		}
		// depGraph.listModifiers();
		
		return depGraph;

	}

	// undirected graph
	public void addEdge(int i, int j) {

		(adj.get(i)).add(j);
	}

	public ArrayList<Integer> getNbr(int curr) {
		return adj.get(curr);
	}

	/**
	 * All the words are always stored in lower case
	 * 
	 * @param pos
	 * @param word
	 */
	public void addNode(int pos, String word) {
		word = word.toLowerCase();
		nodeWordMap.put(pos, word);
		wordNodeMap.put(word, pos);
	}

	public ArrayList<Word> getWordsOnPath(Word src, Word des) {
		int srcNode = src.idx; // wordNodeMap.get(src);
		int desNode = des.idx;// wordNodeMap.get(des);

		ArrayList<Integer> path = BFS.getPath(this, srcNode, desNode);
		ArrayList<Word> res = new ArrayList<Word>();
		for (Integer node : path) {
			res.add(new Word(node, nodeWordMap.get(node).trim().toLowerCase()));
		}
		return res;
	}

	public String getLabel(int num) {
		return nodeWordMap.get(num);
	}

	public Integer getIdx(String word) {
		return wordNodeMap.get(word.toLowerCase());
	}

	public void addModifier(Word moddedWord, Word modifier) {
		if(modifiersMap.containsKey(moddedWord)) {
			modifiersMap.get(moddedWord).add(modifier);
		} else {
			HashSet<Word> tmp = new HashSet<Word>();
			tmp.add(modifier);
			modifiersMap.put(moddedWord, tmp);
		}
		
	}

	public HashSet<Word> getModifiers(Word modifiedWord) {
		return modifiersMap.get(modifiedWord);
	}

	public void listModifiers() {
		for (Word word : modifiersMap.keySet()) {
			System.err.println(word + " -> " + modifiersMap.get(word));
		}
	}

}