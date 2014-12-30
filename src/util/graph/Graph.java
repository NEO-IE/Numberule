//sg
package util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Node;

import meta.ModifyingTypes;
import util.Number;
import util.Pair;
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
	private HashMap<Integer, Word> nodeWordMap; // map from node number to
													// the word
	private HashMap<Word, Integer> wordNodeMap; // map from node number to
	// the word

	private HashMap<Word, HashSet<Word> > modifiersMap; // given a word, checks if there is
												// a modifier
	
	private HashMap<Pair<Word, Word>, Integer> pathLenMap; //all pairs shortest path
	
	public final static int MAX = 1000;
	
	
	private int numNodes; // the number of nodes
	
	private Graph() {
		adj = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < MAX; i++) {
			adj.add(new ArrayList<Integer>());
		}
		nodeWordMap = new HashMap<Integer, Word>();
		wordNodeMap = new HashMap<String, Integer>();
		modifiersMap = new HashMap<>();
		pathLenMap = new HashMap<Pair<Word,Word>, Integer>();
	}
	
	/**
	 * Floyd-Warshall All pairs shortest path
	 */
	private void allPairs() {
		int dist[][] = new int[numNodes + 2][numNodes + 2];
		for(int i = 0; i < adj.size(); i++) {
			ArrayList<Integer> adjI = adj.get(i);
		//	System.out.println(adjI);
			for(int j = 0; j < adjI.size(); j++) {
				dist[i][adjI.get(j)] = 1;
			}
		}
		for(int i = 0; i < numNodes; i++) {
			for(int j = 0; j < numNodes; j++) {
				dist[i][j] = MAX;
			}
		}
		//Arrays.fill(dist, Integer.MAX_VALUE);
		for(int  k = 0; k < numNodes; k++) {
			for(int i = 0; i < numNodes; i++) {
				for(int j = 0; j < numNodes; j++) {
					dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
				}
			}
		}
		
		for(int i = 0; i < numNodes; i++) {
			for(int j = 0; j < numNodes; j++) {
				if(dist[i][j] != MAX) {
					Word w1 = nodeWordMap.get(i);
					Word w2 = nodeWordMap.get(j);
					if(null == w1 || null == w2) continue;
					Pair<Word, Word> keyPair = new Pair<Word, Word>(w1, w2);
					System.out.println(w1.getVal() + " - " + w2.getVal() + " = " + dist[i][j]);
					pathLenMap.put(keyPair, dist[i][j]);
				}
			}
		}
	}

	/**
	 * The factory method that takes the typed dependencies and returns a graph
	 * @param tdi
	 * @return
	 */
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
			//undirected, add both edges
			depGraph.addEdge(depNode.index(), govNode.index());
			depGraph.addEdge(govNode.index(), depNode.index());

			// governor is being modified
			
			if (ModifyingTypes.isModifier(td1.reln().toString())) {
				
				Word govWord = new Word(govNode.index(), govNode.value());
				Word depWord = new Word(depNode.index(), depNode.value());
				//dependencies are bidirectional
				depGraph.addModifier(govWord, depWord);
				depGraph.addModifier(depWord, govWord);
			}
			// System.out.println(govNode.value() + " -> " + depNode.value());
		}
		// depGraph.listModifiers();
		
		depGraph.setNumNodes(100);
		System.out.println(depGraph.wordNodeMap.keySet());
		System.out.println(depGraph.numNodes);
		depGraph.allPairs();
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
		nodeWordMap.put(pos, new Word(pos, word));
		wordNodeMap.put(word, pos);
	}

	public ArrayList<Word> getWordsOnPath(Word src, Word des) {
		int srcNode = src.idx; // wordNodeMap.get(src);
		int desNode = des.idx;// wordNodeMap.get(des);

		ArrayList<Integer> path = BFS.getPath(this, srcNode, desNode);
		ArrayList<Word> res = new ArrayList<Word>();
		for (Integer node : path) {
			res.add(new Word(node, nodeWordMap.get(node).getVal().trim().toLowerCase()));
		}
		return res;
	}

	public String getLabel(int num) {
		if(nodeWordMap.containsKey(num))
			return nodeWordMap.get(num).getVal();
		else
			return null;
	}

	public Integer getIdx(String word) {
		if(wordNodeMap.containsKey(word.toLowerCase()))
			return wordNodeMap.get(word.toLowerCase());
		else
			return null;
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
		if(modifiersMap.containsKey(modifiedWord))
			return modifiersMap.get(modifiedWord);
		
		return null;
	}

	public void listModifiers() {
		for (Word word : modifiersMap.keySet()) {
			System.err.println(word + " -> " + modifiersMap.get(word));
		}
	}


	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public int distance(Word country, Number currNumber) {
		Word num = (Word) currNumber;
		return pathLenMap.get(new Pair<Word, Word>(country, num));
	
	}

}