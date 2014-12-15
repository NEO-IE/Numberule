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
import org.w3c.dom.Element;

import catalog.QuantityCatalog;
import catalog.Unit;

import util.Country;
import util.Number;
import util.Pair;
import util.Relation;
import util.Word;
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
public class RuleBasedDriver {
	Properties prop;
	StanfordCoreNLP pipeline;
	static Pattern numberPat;
	HashSet<String> countryList;
	private static final String countriesFileName = "data/countries_list";
   // static QuantityCatalog quantDict = null;

	
	RuleBasedDriver() throws Exception {		
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
		/*
		quantDict = new QuantityCatalog((Element) null);
        if(quantDict == null){
                System.err.println("Could not load Quantity Taxonomy file.");
                throw new Exception("Failed to load Quantity Taxonomy file.");
        }
		*/	
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		RuleBasedDriver dprsr = new RuleBasedDriver();
		String fileString = FileUtils.readFileToString(new File("debug"));
		Annotation doc = new Annotation(fileString);
		dprsr.pipeline.annotate(doc);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		int sentId = 1;
		for (CoreMap sentence : sentences) {
			// Get dependency graph
			
			//Step 1 : Get the typed dependencies
			Tree tree = sentence.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Iterator<TypedDependency> tdi = td.iterator();
			
			//Step 2 : Make a graph out of them
			Graph depGraph = Graph.makeDepGraph(tdi);
			
			//Step 3 : Identify all the country number word pairs
			ArrayList< Pair<Country, Number>> pairs = dprsr.getPairs(depGraph, sentence);
			System.out.println(pairs.size());
			
			//Step 4 : Extract the relations that exists in these pairs
			getExtractions(depGraph, pairs);
		}
	}

	static void getExtractions(Graph depGraph, ArrayList< Pair<Country, Number> > pairs) throws IOException {
		for(Pair<Country, Number> pair : pairs) {
			//System.out.println(depGraph.getWordsOnPath(pair.country, pair.number));
			ArrayList<Word> wordsOnDependencyGraphPath = depGraph.getWordsOnPath(pair.first, pair.second);
			ArrayList<Relation> rels = ExtractFromPath.getExtractions(pair, wordsOnDependencyGraphPath);
			/**
			 * TODO : check if the rel extracted is compatible with the unit of the number
			 * 
			 */
			
			for(Relation rel : rels) {
		
				/*
				Unit unit = quantDict.getUnitFromBaseName(pair.second.getUnit());
				if(unit != null){
                    Unit SIUnit = unit.getParentQuantity().getCanonicalUnit();
                    
                    if(!RelationUnitMap.getUnit(rel.getRelName()).equals(SIUnit.getBaseName())){
                    	continue; //Incorrect unit, this cannot be the relation.
                    }
                    
				}else{
					if(!RelationUnitMap.getUnit(rel.getRelName()).equals("")){
						continue; //this cannot be the correct relation.
					}
				}
				*/
				augment(depGraph, rel);
				System.out.println(rel);
			}
			
		}
	}
	
	/**
	 * This is the workhorse, given a relation, checks if the argument or the relation can be augmented, and if so,
	 * returns the augmented relation
	 * @param rel
	 * @return
	 */
	private static void augment(Graph depGraph, Relation rel) {
		Word arg1 = rel.getArg1(), modifier = null;
		System.out.println(depGraph.getModifier(arg1));
		if(null != (modifier = depGraph.getModifier(arg1))) {
			arg1.setVal(modifier.val + " " + arg1.val);
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
				Number num = new Number(depGraph.getIdx(word), word);
			/*
				//check for unit here....
				num.setUnit("metre");
			*/
				numbers.add(num);
				
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
