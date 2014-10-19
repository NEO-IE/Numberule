import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

//sg
public class DepParser {
	Properties prop;
	StanfordCoreNLP pipeline;

	DepParser() {
		prop = new Properties();
		prop.put("annotators", "tokenize, ssplit, pos, lemma , parse");
		pipeline = new StanfordCoreNLP(prop);
	}

	public static void main(String args[]) throws IOException {
		DepParser dprsr = new DepParser();
		String fileString = FileUtils.readFileToString(new File("sampleInput"));
		Annotation doc = new Annotation(fileString);
		dprsr.pipeline.annotate(doc);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			Tree tree = sentence.get(TreeAnnotation.class);
			// Get dependency tree
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Iterator<TypedDependency> tdi = td.iterator();
			Graph depGraph = new Graph();
			
			/**
			 * Add nodes to the graph
			 */
			ArrayList<Integer> adjA = depGraph.getNbr(1);
			for(Integer n : adjA) {
				System.out.println(n);
			}
			while (tdi.hasNext()) {
				TypedDependency td1 = tdi.next();
				System.out.println(td1);
				TreeGraphNode depNode = td1.dep();
				TreeGraphNode govNode = td1.gov();
				depGraph.addNode(depNode.index(),  depNode.value());
				depGraph.addNode(govNode.index(),  govNode.value());
				depGraph.addEdge(depNode.index(), govNode.index());
				depGraph.addEdge(govNode.index(), depNode.index());
				System.out.println(depNode.index() + " -> " +  govNode.index());
			}
		 adjA = depGraph.getNbr(1);
			for(Integer n : adjA) {
				System.out.println(n);
			}
			ArrayList<String> path = depGraph.getWordsOnPath("Australia", "10009");
			System.out.println(path);
		}
	}
	
}

	/*
	 * An unweighted, undirected graph
	 */
	class Graph {
		private ArrayList< ArrayList< Integer > > adj; // the adjacency list
		private HashMap<Integer, String> nodeWordMap; // map from node number to
														// the word
		private HashMap<String, Integer> wordNodeMap; // map from node number to
		// the word

		public final static int MAX = 1000;

		Graph() {
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

	/*
	 * Runs BFS from a given source and returns a list of parents
	 */
	class BFS {
		public static Integer[] run(Graph g, int src) {
			Queue<Integer> q = new LinkedList<Integer>();
			Integer par[] = new Integer[Graph.MAX + 1];
			boolean visited[] = new boolean[Graph.MAX + 1];
			q.add(src);
			visited[src] = true;
			while (!q.isEmpty()) {
				
				int curr = q.poll();
				System.out.println("Extracted : " + curr);
				for (int nbr : g.getNbr(curr)) {
					if (!visited[nbr]) {
						System.out.println("Nbr : " + nbr);
						q.add(nbr);
						visited[nbr] = true;
						par[nbr] = curr;
					}
				}
			}
			return par;
		}

		public static ArrayList<Integer> getPath(Graph g, int src, int dest) {
			Integer par[] = BFS.run(g, src);
			ArrayList<Integer> res = new ArrayList<Integer>();
			res.add(dest);
			int at = dest;
			while (at != src) {
				System.out.println("Par of " + g.getLabel(at) + " = " + g.getLabel(par[at]));
				at = par[at];
				res.add(at);
			}
			Collections.reverse(res);
			return res;
		}
	}
