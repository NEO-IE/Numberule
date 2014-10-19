import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import util.CountryNumberPair;
import util.Graph;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
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
public class DepParser {
	Properties prop;
	StanfordCoreNLP pipeline;
	Pattern numberPat;

	DepParser() {
		numberPat = Pattern.compile("^[\\+-]?\\d+([,\\.]\\d+)?([eE]-?\\d+)?$");
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
			// Get dependency graph
			Tree tree = sentence.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Iterator<TypedDependency> tdi = td.iterator();
			Graph depGraph = makeDepGraph(tdi);
			//Identify all the token pairs
			ArrayList<CountryNumberPair> pairs = getPairs(sentence);
		}
	}
	
	static Graph makeDepGraph(Iterator<TypedDependency> tdi) {
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
				TreeGraphNode depNode = td1.dep();
			TreeGraphNode govNode = td1.gov();
			depGraph.addNode(depNode.index(),  depNode.value());
			depGraph.addNode(govNode.index(),  govNode.value());
			depGraph.addEdge(depNode.index(), govNode.index());
			depGraph.addEdge(govNode.index(), depNode.index());
		
		}
		return depGraph;
		
	}
	


	private bool isCountry() {
		return false;
	}
	
	private bool isNumber(String token) {
		return numberPat.matcher(token.toString()).matches();
	}
	
ArrayList<CountryNumberPair> getcnpair(CoreMap sentence) {
	ArrayList<String> countries;
	ArrayList<String> number;
	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);       
	 }

}
}
