//sg
package util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
	private HashMap<Word, ArrayList<Word> > adjMap; // the adjacency map
	
	//Mapping from index to the corresponding node, required
	//for fast graph ops
	private HashMap<Integer, Word> nodeWordMap;
	private HashMap<Word, Integer> wordNodeMap; 
	
	//A mapping from all the word vals to corresponding indexes
	private HashMap<String, Integer> valIdxMap;
	//Modifiers of the words
	private HashMap<Word, HashSet<Word> > relationModifiersMap;
	private HashMap<Word, HashSet<Word>> keywordModifiersMap;
	
	
	
	public final static int MAX = 1000;
	
	
	private Graph() {
		adjMap = new HashMap<Word, ArrayList<Word> >();
		nodeWordMap = new HashMap<Integer, Word>();
		wordNodeMap = new HashMap<Word, Integer>();
		relationModifiersMap = new HashMap<>();
		valIdxMap = new HashMap<String, Integer>();
		keywordModifiersMap = new HashMap<>();
	}
	
	
	/**
	 * The factory method that takes the typed dependencies and returns a graph
	 * @param tdi
	 * @return
	 */
	
	public static Graph makeDepGraph(Iterator<TypedDependency> tdi) {
		//Graph depGraph = new Graph();

		/**
		 * Add nodes to the graph
		 */
		ArrayList<Pair< String, Pair <Word, Word> > >  pairList = new ArrayList<>(); //java 7 ftw 
		while (tdi.hasNext()) {
			TypedDependency td1 = tdi.next();
			TreeGraphNode depNode = td1.dep();
			TreeGraphNode govNode = td1.gov();

			Word govWord = new Word(govNode.index(), govNode.value().toLowerCase());
			Word depWord = new Word(depNode.index(), depNode.value().toLowerCase());
			pairList.add(new Pair< String, Pair <Word, Word> > (td1.reln().toString(), new Pair<Word, Word>(govWord, depWord)));
			/*
			depGraph.addNode(depNode.index(), depWord);
			depGraph.addNode(govNode.index(), govWord);
			
			//undirected, add edges between depWord and govWord
			depGraph.addEdge(depWord, govWord);
			depGraph.addEdge(govWord, depWord);
	
			// governor is being modified
			
			if (ModifyingTypes.isRelDep(td1.reln().toString()) || ModifyingTypes.isKeywordDep(td1.reln().toString())) {
				
				//dependencies are bidirectional
				depGraph.addModifier(govWord, depWord, td1.reln().toString());
				depGraph.addModifier(depWord, govWord, td1.reln().toString());
			}

			// System.out.println(govNode.value() + " -> " + depNode.value());
		}
		// depGraph.listModifiers();
		
		for(Word w : depGraph.wordNodeMap.keySet()) {
			depGraph.valIdxMap.put(w.getVal(), w.getIdx());
		}
		*/
		}
		//depGraph.allPairs();
		return Graph.makeDepGraphFromList(pairList);

	}

	/**
	 * The list is supposed to be ordered like dependent -> gov node
	 * @param pairList
	 * @return
	 */
	public static Graph makeDepGraphFromList(ArrayList<Pair< String, Pair< Word, Word> > > pairList) {
		Graph depGraph = new Graph();


		for(Pair<String, Pair< Word, Word>> relWordPair: pairList) {
			Pair<Word, Word> wordPair = relWordPair.second;
			Word govWord = wordPair.first;
			Word depWord = wordPair.second;
			String reln = relWordPair.first;
			
			depGraph.addNode(depWord.getIdx(), depWord);
			depGraph.addNode(govWord.getIdx(), govWord);
			
			//undirected, add edges between depWord and govWord
			depGraph.addEdge(depWord, govWord);
			depGraph.addEdge(govWord, depWord);
	
			// governor is being modified
			
			if (ModifyingTypes.isRelDep(reln) || ModifyingTypes.isKeywordDep(reln)) {
				
				//dependencies are bidirectional
				depGraph.addModifier(govWord, depWord, reln);
				depGraph.addModifier(depWord, govWord, reln);
			}

			// System.out.println(govNode.value() + " -> " + depNode.value());
		}
		// depGraph.listModifiers();
		
		for(Word w : depGraph.wordNodeMap.keySet()) {
			depGraph.valIdxMap.put(w.getVal(), w.getIdx());
		}
		//depGraph.allPairs();
		return depGraph;

	}

	// undirected graph
	public void addEdge(Word a, Word b) {

		if(adjMap.keySet().contains(a)) {
			adjMap.get(a).add(b);
		} else {
			ArrayList<Word> adjListA = new ArrayList<Word>();
			adjListA.add(b);
			adjMap.put(a, adjListA);
		}
	}

	/**
	 * Returns the neighboring words of w
	 * @param curr
	 * @return
	 */
	public ArrayList<Word> getNbr(Word curr) {
	
		return adjMap.get(curr);
	}

	/**
	 * All the words are always stored in lower case
	 * 
	 * @param pos
	 * @param word
	 */
	public void addNode(int pos, Word w) {
		nodeWordMap.put(pos, w);
		wordNodeMap.put(w, pos);
	}

	public ArrayList<Word> getWordsOnPath(Word src, Word des) {
		return BFS.getPath(this, src, des);
	}

	public String getLabel(int num) {
		if(nodeWordMap.containsKey(num))
			return nodeWordMap.get(num).getVal();
		else
			return null;
	}

	public Integer getIdx(String ip) {
		ip  = ip.toLowerCase(); //everything is stored in graph in lower case
		if(valIdxMap.containsKey(ip))
			return valIdxMap.get(ip);
		else
			return null;
	}

	public void addModifier(Word moddedWord, Word modifier, String dep) {
		if(ModifyingTypes.isRelDep(dep)) {
			addToMap(moddedWord, modifier, relationModifiersMap);
		}
		if(ModifyingTypes.isKeywordDep(dep)) {
			addToMap(moddedWord, modifier, keywordModifiersMap);
		}
	}
	
	private void addToMap(Word moddedWord, Word modifier, HashMap<Word, HashSet<Word> > modMap) {
		if(modMap.containsKey(moddedWord)) {
			modMap.get(moddedWord).add(modifier);
		} else {
			HashSet<Word> tmp = new HashSet<Word>();
			tmp.add(modifier);
			modMap.put(moddedWord, tmp);
		}
	}

	
	public HashSet<Word> getRelationModifiers(Word modifiedWord) {
		if(relationModifiersMap.containsKey(modifiedWord))
			return relationModifiersMap.get(modifiedWord);
		
		return null;
	}

	public HashSet<Word> getKeywordModifiers(Word modifiedWord) {
		if(keywordModifiersMap.containsKey(modifiedWord))
			return keywordModifiersMap.get(modifiedWord);
		return null;
	}

	public void listModifiers() {
		for (Word word : relationModifiersMap.keySet()) {
			System.err.println(word + " -> " + relationModifiersMap.get(word));
		}
	}

	public int distance(Word country, Number currNumber) {
		//The country argument here might be the augmented country, 
		//and BFS must be called with the original country
		return getWordsOnPath(nodeWordMap.get(country.getIdx()), currNumber).size();
	
	}

}