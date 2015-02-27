package dependencyStats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.Country;
import util.Number;
import util.Pair;
import util.Word;
import util.graph.Graph;
import main.ExtractFromPath;
import main.RuleBasedDriver;
import meta.KeywordData;
import meta.RelationUnitMap;
import derbyDatabase.Corpus;
import derbyDatabase.CustomCorpusInformationSpecification;
import derbyDatabase.SentDependencyInformation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

public class DependencyStats {

	public static String corpusPath = "jdbc:derby:/mnt/a99/d0/aman/MultirExperiments/data/numbers_short";
	public CustomCorpusInformationSpecification cis;
	public Corpus corpus = null;
	public RuleBasedDriver rbd = null;
	public KeywordData kwd = null;
	public Set<String> rels = null;
	
	public DependencyStats() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException{
		cis = new CustomCorpusInformationSpecification();
		cis = (CustomCorpusInformationSpecification)ClassLoader.getSystemClassLoader().loadClass("derbyDatabase.DefaultCorpusInformationSpecification").newInstance();
		corpus = new Corpus(corpusPath, cis, true);
		corpus.setCorpusToTrain("emptyFile");
		
		rbd = new RuleBasedDriver(true); //no need for units.
		
		kwd = new KeywordData();
		rels = RelationUnitMap.getRelations();
	}
	
	public void statsProcessor() throws SQLException, IOException{
		
		long start = System.currentTimeMillis();
		if(corpus == null){
			System.out.println("Corpus is null. Please check the database settings.");
			return;
		}
		
		Iterator<Annotation> di = corpus.getDocumentIterator();
		if(null == di) {
			System.out.println("CORPUS IS NULL");
		}
		int count =0;
		long startms = System.currentTimeMillis();
		long timeSpentInQueries = 0;
		while(di.hasNext()){
			Annotation d = di.next();
			if(null == d) {
				
				System.out.println(d);
			}
			List<CoreMap> sentences = d.get(CoreAnnotations.SentencesAnnotation.class);

			for(CoreMap sentence : sentences){
				//int sentGlobalID = sentence.get(SentGlobalID.class);
				processSentence(sentence);
			}
			count++;
			if( count % 1000 == 0){
				long endms = System.currentTimeMillis();
				System.out.println(count + " documents processed");
				System.out.println("Time took = " + (endms-startms));
				startms = endms;
				System.out.println("Time spent in querying db = " + timeSpentInQueries);
				timeSpentInQueries = 0;
			}
		}
    	long end = System.currentTimeMillis();
    	System.out.println("Stats Generation process took " + (end-start) + " millisseconds");
	}
		
	public void processSentence(CoreMap sentence) throws IOException{
		
		List<Triple<Integer, String, Integer>> deps = sentence.get(SentDependencyInformation.DependencyAnnotation.class);
		Graph depGraph = rbd.getDepGraph(deps, sentence);
		ArrayList<Pair<Country, Number>> args = rbd.getPairs(depGraph,sentence);
		if(args == null || args.size() == 0){
			return ;
		}
		
		for(Pair<Country,Number> arg: args){
			ArrayList<Word> wordsOnDependencyGraphPath = depGraph.getWordsOnPath(arg.first, arg.second);
			if(ExtractFromPath.modifierPresent(arg, wordsOnDependencyGraphPath, kwd)){
				continue; //not a valid candidate.
			}
			for(String rel: rels){
				if(rbd.unitRelationMatch(rel, arg)){  //units match for this relation - argument pair.
					dumpKeyword(sentence, wordsOnDependencyGraphPath, rel);
				}
			}
		}
	}
	
	public void dumpKeyword(CoreMap sentence,ArrayList<Word> wordsOnDependencyGraphPath, String rel) throws IOException{
		File file = new File(rel);
		BufferedWriter output = null;
		if(file.exists()){
			output = new BufferedWriter(new FileWriter(file, true));
		}else{
			output = new BufferedWriter(new FileWriter(file));
		}
		List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
		for(Word word: wordsOnDependencyGraphPath){
			CoreLabel token = tokens.get(word.getIdx()-1);
			output.append(token.toString()+"\t"+token.lemma()+"\t"+token.ner()+"\t"+token.tag()+"\n");
		}
		
        output.close();
	}
	
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException{
		DependencyStats ds = new DependencyStats();
		ds.statsProcessor();
	}
}
