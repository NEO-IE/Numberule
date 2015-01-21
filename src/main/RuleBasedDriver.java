package main;
import iitb.shared.EntryWithScore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import meta.RelationUnitMap;

import org.apache.commons.io.FileUtils;

import util.Country;
import util.Number;
import util.Pair;
import util.Relation;
import util.Word;
import util.graph.Graph;
import catalog.Unit;
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
import eval.UnitExtractor;

//sg
public class RuleBasedDriver {
	private Properties prop;
	private StanfordCoreNLP pipeline;
	private static Pattern numberPat, yearPat;
	private HashSet<String> countryList;
	private  boolean unitsActive;
	private static final String countriesFileName = "data/countries_list";
	private  UnitExtractor ue = null;
	int cumulativeLen; //to obtain sentence offsets
	public RuleBasedDriver(boolean unitsActive) {
		this.unitsActive = unitsActive;
		numberPat = Pattern.compile("^[\\+-]?\\d+([,\\.]\\d+)*([eE]-?\\d+)?$");
		yearPat = Pattern.compile("^19[56789]\\d|20[01]\\d$");
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

		try {
			ue = new UnitExtractor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * quantDict = new QuantityCatalog((Element) null); if(quantDict ==
		 * null){ System.err.println("Could not load Quantity Taxonomy file.");
		 * throw new Exception("Failed to load Quantity Taxonomy file."); }
		 */
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		RuleBasedDriver rbased = new RuleBasedDriver(true);
//		String fileString = FileUtils.readFileToString(new File("entire_sentence_set"));
//		String outFile = "entire_sentence_output";
//		rbased.batchExtract(fileString, outFile);
		System.out.println("here");
		String fileString = FileUtils.readFileToString(new File("debug"));
		System.out.println(rbased.extract(fileString));
	

		
	}
	
	
	/**
	 * takes a tokenized sentence, and the corresponding
	 * typed dependencies. Primarily written to facilitate talking with MultiR
	 */
	public ArrayList<Relation> extractFromMultiRDepString(String tokenizedSent, String deps, CoreMap sentence) {
		TypedDependency = new TypedDependency(reln, gov, dep);
		String depsArr[] = deps.split("|");
		return null;
	}
	
	public ArrayList<Relation> extract(String sentenceString) throws IOException {
		ArrayList<Relation> res = new ArrayList<Relation>();
		Annotation doc = new Annotation(sentenceString);
		pipeline.annotate(doc);
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Get dependency graph
			
			// Step 1 : Get the typed dependencies
			Tree tree = sentence.get(TreeAnnotation.class);
	
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);

			//Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Collection<TypedDependency> td = gs.allTypedDependencies();
			// Collection<TypedDependency> td =
			// gs.typedDependenciesCCprocessed();
			Iterator<TypedDependency> tdi = td.iterator();
			
			while(tdi.hasNext()) {
				System.out.println(tdi.next());
			}
			tdi = td.iterator();
//			// Step 2 : Make a graph out of them
			Graph depGraph = Graph.makeDepGraph(tdi);

			// Step 3 : Identify all the country number word pairs
			ArrayList<Pair<Country, Number>> pairs = getPairs(depGraph,
					sentence);
			
			// Step 4 : Extract the relations that exists in these pairs
			res.addAll(getExtractions(depGraph, pairs));
		}
		return res;
	}

	public void batchExtract(String fileString, String outFile) throws IOException {
		Annotation doc = new Annotation(fileString);
		pipeline.annotate(doc);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		int i = 1;
		PrintWriter pw = new PrintWriter(new FileWriter(outFile));
		for (CoreMap sentence : sentences) {
			// Get dependency graph
			
			// Step 1 : Get the typed dependencies
			Tree tree = sentence.get(TreeAnnotation.class);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);

			//Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Collection<TypedDependency> td = gs.allTypedDependencies();
			// Collection<TypedDependency> td =
			// gs.typedDependenciesCCprocessed();
			Iterator<TypedDependency> tdi = td.iterator();

			// Step 2 : Make a graph out of them
			Graph depGraph = Graph.makeDepGraph(tdi);

			// Step 3 : Identify all the country number word pairs
			ArrayList< Pair<Country, Number> > pairs = getPairs(depGraph,
					sentence);
		
			
			pw.write("\n---\n");
			pw.write("sentence " + i++ + "\n");
			pw.write(sentence + "\n\n");
			System.out.println("\n------------------------------------------------------");
			System.out.println("\nSentence ===> " + sentence);
			// Step 4 : Extract the relations that exists in these pairs
			 
			ArrayList<Relation> res = getExtractions(depGraph, pairs);
			System.out.println("Extractions == > " + res);
			pw.write(res + "\n");
			pw.write("---\n");
			///*/System.out.println(getExtractions(depGraph, pairs) + "\n");
		}
		pw.close();
	}

	private static boolean isYear(String token) {
		return yearPat.matcher(token).matches();
	}

	ArrayList<Relation> getExtractions(Graph depGraph,
			ArrayList<Pair<Country, Number>> pairs) throws IOException {
		ArrayList<Relation> result = new ArrayList<Relation>();
		HashMap< Pair<Word, Word>, Relation> alreadyExtractedRelMap = new HashMap<Pair<Word,Word>, Relation>();
		
		//The hashcode of Relation does not include argument2
		for (Pair<Country, Number> pair : pairs) {
			System.out.println("\nPair == > " + pair);
			// System.out.println(depGraph.getWordsOnPath(pair.country,
			// pair.number));
			ArrayList<Word> wordsOnDependencyGraphPath = depGraph.getWordsOnPath(pair.first, pair.second);
			System.out.println("Path == > " + wordsOnDependencyGraphPath);
			ArrayList<Relation> rels = ExtractFromPath.getExtractions(pair,wordsOnDependencyGraphPath, depGraph);
			for (Relation rel : rels) {
				
				if (unitsActive) {
					Unit unit = ue.quantDict.getUnitFromBaseName(pair.second
							.getUnit());
					if (unit != null && !unit.getBaseName().equals("")) {
						Unit SIUnit = unit.getParentQuantity()
								.getCanonicalUnit();
						if (
								SIUnit != null && !RelationUnitMap.getUnit(rel.getRelName()).equals(SIUnit.getBaseName()) 
								||
								SIUnit == null && !RelationUnitMap.getUnit(rel.getRelName()).equals(unit.getBaseName())
								) {
							continue; // Incorrect unit, this cannot be the
										// relation.
						}
					}else if(unit == null && !pair.second.getUnit().equals("") && RelationUnitMap.getUnit(rel.getRelName()).equals(pair.second.getUnit())){ //for the cases where units are compound units.
						//do nothing, seems legit
					}else {
						if (!RelationUnitMap.getUnit(rel.getRelName()).equals(
								"")) {
							continue; // this cannot be the correct relation.
						}
					}
				}
				rel= augment(depGraph, rel);
				Pair<Word, Word> argRelPairKey = new Pair<Word, Word>(rel.getCountry(),rel.getKeyword());
				if(alreadyExtractedRelMap.containsKey(argRelPairKey)) { //the same arg1, relation, and keyword have already been extracted?
					Number prevNumber = alreadyExtractedRelMap.get(argRelPairKey).getNumber();
					Number currNumber = rel.getNumber();
					if(depGraph.distance(rel.getCountry(), currNumber) < depGraph.distance(rel.getCountry(), prevNumber)) { //the current number is closer?
						alreadyExtractedRelMap.put(argRelPairKey, rel);
					} //else nothing to do, the relation already present in the map is the one that should be there
				} else { //new relation, must extract	
					alreadyExtractedRelMap.put(argRelPairKey, rel);
				}
			}
		}
		for(Relation rel : alreadyExtractedRelMap.values()) {
			result.add(rel);
		}
		return result;
	}

	/**
	 * This is the workhorse, given a relation, checks if the argument or the
	 * relation can be augmented, and if so, returns the augmented relation
	 * 
	 * @param rel
	 * @return
	 */
	private static Relation augment(Graph depGraph, Relation rel) {
		/* Augment the argument */

		boolean hasChaged = false;
		
		Word countryArg = rel.getCountry();
		HashSet<Word> modifiers = null;
		
		/*
		 * Augmenting country first
		 */
		StringBuffer countryValBuffer = new StringBuffer("");
		countryValBuffer.append(rel.getCountry().getVal());
		
		if (null != (modifiers = depGraph.getRelationModifiers(countryArg))) {
			hasChaged = true;
			for (Word modifier : modifiers) {
				//modifier.getVal() + " " + arg1.getVal());
				countryValBuffer.append(" " + modifier.getVal());
			
			}
		}
		
		/* Augment Relation */
		Word relWord = rel.getKeyword();
		StringBuffer relValBuffer = new StringBuffer("");
		relValBuffer.append(rel.getCountry().getVal());
		modifiers = null;
		if (null != (modifiers = depGraph.getRelationModifiers(relWord))) {
			hasChaged = true;
			for (Word modifier : modifiers) {
				//relWord.setVal(modifier.getVal() + " " + relWord.getVal());
				relValBuffer.append(" " + modifier.getVal());
			}
		}
		
		if(hasChaged) { //need to create a new relation
			Word newCountry = new Word(rel.getCountry().getIdx(), countryValBuffer.toString());
			Word newRelWord= new Word(rel.getKeyword().getIdx(), relValBuffer.toString());
			Relation newRel = new Relation(newCountry, rel.getNumber(), newRelWord, rel.getRelName());
			return newRel;
		}
		return rel;
	}

	private boolean isCountry(String token) {
		return countryList.contains(token.toLowerCase());
	}

	private static boolean isNumber(String token) {
		return numberPat.matcher(token.toString()).matches();
	}

	/**
	 * Returns the list of country number pairs in the graph Uses the indexes
	 * defined by the dependency graph which is passed as an argument
	 * 
	 * @param depGraph
	 * @param sentence
	 * @return
	 */
	ArrayList<Pair<Country, Number>> getPairs(Graph depGraph, CoreMap sentence) {
		ArrayList<Country> countries = new ArrayList<Country>();
		ArrayList<Number> numbers = new ArrayList<Number>();
		ArrayList<Pair<Country, Number>> res = new ArrayList<Pair<Country, Number>>();
		float values[][] = new float[1][1];
		for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);
			//System.out.println(word  + " - " + depGraph.getIdx(word) + depGraph.nodeWordMap.get(depGraph.getIdx(word)));
			if (isCountry(word)) {	
				countries.add(new Country(depGraph.getIdx(word), word));
			}
			
			if (isNumber(word) && !isYear(word)) {
				Number num = new Number(depGraph.getIdx(word), word);
				if (unitsActive) {
					/*
					int beginPos = token.beginPosition() - cumulativeLen;
					int endPos = token.endPosition() - cumulativeLen;
					*/
					String sentString = sentence.toString();
					int beginIdx = sentString.indexOf(word);
					int endIdx = beginIdx + word.length();
					String utString = sentString.substring(0, beginIdx) + "<b>" + word + "</b>" + sentString.substring(endIdx); 
					/*unitString = sentence.toString().substring(0, beginPos) + //before 
								"<b>" + token + "</b>"+  //the token
								((sentence.size() == endPos) ? "" : sentence.toString().substring(endPos)); //after*/
				//	System.out.println("Unit String: "+ utString);
					List<? extends EntryWithScore<Unit>> unitsS = ue.parser
							.getTopKUnitsValues(utString, "b", 1, 0, values);

					// check for unit here....
					if (unitsS != null) {
						num.setUnit(unitsS.get(0).getKey().getBaseName());
					//	System.out.println("unit: "+unitsS.toString());
					}
				}
				numbers.add(num);
			}

		}
		for (int i = 0, lc = countries.size(); i < lc; i++) {
			for (int j = 0, ln = numbers.size(); j < ln; j++) {
				res.add(new Pair<Country, Number>(countries.get(i), numbers
						.get(j)));
			}
		}
		cumulativeLen += sentence.toString().length();
		return res;
	}

	public void setUnitsActive(boolean unitsActive) {
		this.unitsActive = unitsActive;
	}
}
