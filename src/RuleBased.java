import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import util.Country;
import util.Number;
import util.Pair;
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
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

//sg
public class RuleBased {
	Properties prop;
	StanfordCoreNLP pipeline;
	static Pattern numberPat;
	HashSet<String> countryList;
	private static final String countriesFileName = "/home/aman/depbased/data/countries_list";
	
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
		String fileString = FileUtils.readFileToString(new File("debug"));
		Annotation doc = new Annotation(fileString);
		dprsr.pipeline.annotate(doc);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		int sentId = 1;
		for (CoreMap sentence : sentences) {
			// Get dependency graph
			
			Tree tree = sentence.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Iterator<TypedDependency> tdi = td.iterator();
			
			Graph depGraph = Graph.makeDepGraph(tdi);
			ArrayList< Pair<Country, Number>> pairs = dprsr.getPairs(depGraph, sentence);
			System.out.println(pairs.size());
			getExtractions(depGraph, pairs);
		}
	}

	static void getExtractions(Graph depGraph, ArrayList< Pair<Country, Number> > pairs) {
		for(Pair<Country, Number> pair : pairs) {
			//System.out.println(depGraph.getWordsOnPath(pair.country, pair.number));
			ArrayList<String> rels = ExtractFromPath.getExtractions(depGraph.getWordsOnPath(pair.first, pair.second));
			/**
			 * TODO : check if the rel extracted is compatible with the unit of the number
			 * 
			 */
			for(String rel : rels) {
				System.out.println(rel + "(" + pair.first.val + ", " + pair.second.val + ")");
			}
			
		}
	}
	

	private boolean isCountry(String token) {
		return countryList.contains(token.toLowerCase());
	}

	private static boolean isNumber(String token) {
		return numberPat.matcher(token.toString()).matches();
	}

	/**
	 * Returns the list of country number pairs in the graph
	 * Uses the indexes defined by the dependency graph which is passed as an argument
	 * @param depGraph
	 * @param sentence
	 * @return
	 */
	ArrayList< Pair<Country, Number> > getPairs(Graph depGraph, CoreMap sentence) {
		ArrayList<Country> countries = new ArrayList<Country>();
		ArrayList<Number> numbers = new ArrayList<Number>();
		ArrayList< Pair<Country, Number> > res = new ArrayList< Pair<Country, Number> >();
		int tokenPos = 0;
		for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);

			if (isCountry(word)) {
				countries.add(new Country(depGraph.getIdx(word), word));
				
			}
			if (isNumber(word)) {
				numbers.add(new Number(depGraph.getIdx(word), word));
				
			}
		
		}
		for (int i = 0, lc = countries.size(); i < lc; i++) {
			for (int j = 0, ln = numbers.size(); j < ln; j++) {
				res.add(new Pair<Country, Number>(countries.get(i), numbers.get(j)));
			}
		}
		
		return res;
	}
}
