import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import util.CountryNumberPair;
import util.graph.Graph;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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
public class RuleBased {
	Properties prop;
	StanfordCoreNLP pipeline;
	static Pattern numberPat;
	HashSet<String> countryList;
	private static final String countriesFileName = "/mnt/a99/d0/aman/MultirExperiments/data/numericalkb/countries_list";

	RuleBased() {
		numberPat = Pattern.compile("^[\\+-]?\\d+([,\\.]\\d+)*([eE]-?\\d+)?$");
		prop = new Properties();
		prop.put("annotators", "tokenize, ssplit, pos, lemma , parse");
		pipeline = new StanfordCoreNLP(prop);

		// Read the countries file

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(countriesFileName));
			String countryName = null;
			countryList = new HashSet<>();
			while ((countryName = br.readLine()) != null) {
				countryList.add(countryName.toLowerCase());
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		RuleBased dprsr = new RuleBased();
		String fileString = FileUtils.readFileToString(new File("sampleInput"));
		Annotation doc = new Annotation(fileString);
		dprsr.pipeline.annotate(doc);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		int sentId = 1;
		for (CoreMap sentence : sentences) {
			// Get dependency graph
			System.out.println(sentId++ + "->");
			Tree tree = sentence.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Iterator<TypedDependency> tdi = td.iterator();
			
			Graph depGraph = makeDepGraph(tdi);
			ArrayList<CountryNumberPair> pairs = dprsr.getPairs(sentence);
			
			getExtractions(depGraph, pairs);
		}
	}

	static void getExtractions(Graph depGraph, ArrayList<CountryNumberPair> pairs) {
		for(CountryNumberPair pair : pairs) {
			//System.out.println(depGraph.getWordsOnPath(pair.country, pair.number));
			ArrayList<String> rels = ExtractFromPath.getExtractions(depGraph.getWordsOnPath(pair.country, pair.number));
			for(String rel : rels) {
				System.out.println(rel + "( " + pair.country + ", " + pair.number + ")");
			}
		}
	}
	static Graph makeDepGraph(Iterator<TypedDependency> tdi) {
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
			//System.out.println(govNode.value() + " -> " + depNode.value());
		}
		return depGraph;

	}

	private boolean isCountry(String token) {
		return countryList.contains(token.toLowerCase());
	}

	private static boolean isNumber(String token) {
		return numberPat.matcher(token.toString()).matches();
	}

	ArrayList<CountryNumberPair> getPairs(CoreMap sentence) {
		ArrayList<String> countries = new ArrayList<String>();
		ArrayList<String> numbers = new ArrayList<String>();
		ArrayList<CountryNumberPair> res = new ArrayList<CountryNumberPair>();
		for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);
			if (isCountry(word)) {
				countries.add(word);
			}
			if (isNumber(word)) {
				numbers.add(word);
			}
		}
		for (int i = 0, lc = countries.size(); i < lc; i++) {
			for (int j = 0, ln = numbers.size(); j < ln; j++) {
				res.add(new CountryNumberPair(countries.get(i), numbers.get(j)));
			}
		}
		return res;

	}
}
