package dependencyStats;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.Country;
import util.Number;
import util.Pair;
import main.RuleBasedDriver;
import derbyDatabase.Corpus;
import derbyDatabase.CustomCorpusInformationSpecification;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class DependencyStats {

	public static String corpusPath = "jdbc:derby:/mnt/a99/d0/aman/MultirExperiments/data/numbers_corpus";
	public CustomCorpusInformationSpecification cis;
	public Corpus corpus = null;
	public RuleBasedDriver rbd = null;
	
	public DependencyStats() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		cis = new CustomCorpusInformationSpecification();
		cis = (CustomCorpusInformationSpecification)ClassLoader.getSystemClassLoader().loadClass("derbyDatabase.DefaultCorpusInformationSpecification").newInstance();
		corpus = new Corpus(corpusPath, cis, true);
		corpus.setCorpusToTrain("emptyFile");
		
		rbd = new RuleBasedDriver(false); //no need for units.	
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
				
				ArrayList<Pair<Country, Number>> arguments = rbd.getPairs(sentence, true);
				processSentence(sentence, arguments);
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
		
	public void processSentence(CoreMap sentence, ArrayList<Pair<Country, Number>> arguments){
		
		if(arguments == null || arguments.size() == 0){ //No argument to process.
			return ;
		}
		
		
		
	}
	
}
