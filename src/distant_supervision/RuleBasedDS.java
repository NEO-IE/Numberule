package distant_supervision;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import catalog.Unit;
import eval.UnitExtractor;
import util.Country;
import util.Number;
import util.Pair;
import util.Word;
import util.graph.Graph;
import main.ExtractFromPath;
import main.RuleBasedDriver;
import meta.KeywordData;
import meta.RelationUnitMap;

public class RuleBasedDS {

	public static void main(String args[]) throws Exception{
		
		String input_file  = "/mnt/a99/d0/aman/numbers_india_sentences_sample.tsv";
		
		RuleBasedDriver rbd = new RuleBasedDriver(true);
		KeywordData kwd = new KeywordData();
		UnitExtractor ue = new UnitExtractor();
		Set<String> relations = RelationUnitMap.getRelations();
		
		BufferedReader br = new BufferedReader(new FileReader(input_file));
		
		HashMap<Pair<Country,String>, EntityRelationDoc> metaGraph = new HashMap<Pair<Country, String>, EntityRelationDoc>();
		
		String line;
		while ((line = br.readLine()) != null) {
		   // process the line.
			
			Graph depGraph = null;
			depGraph = rbd.constructDepGraph(line);
			
			if(depGraph == null){
				continue;
			}
			
			ArrayList<Pair<Country, Number>> pairs = rbd.getPairs(depGraph, line);
			if(pairs == null){
				continue; //no pairs.
			}
			
			for(Pair<Country, Number> pair : pairs){
				ArrayList<Word> wordsOnDependencyGraphPath = depGraph.getWordsOnPath(pair.first, pair.second);
				
				if(! ExtractFromPath.modifierPresent(pair, wordsOnDependencyGraphPath, kwd)){
					//pair doesn't contains modifier in its shortest dependency path
					for(String rel: relations){
					
						Unit unit = ue.quantDict.getUnitFromBaseName(pair.second.getUnit());
						if (unit != null && !unit.getBaseName().equals("")) {
							Unit SIUnit = unit.getParentQuantity().getCanonicalUnit();
							if (
									SIUnit != null && !RelationUnitMap.getUnit(rel).equals(SIUnit.getBaseName()) 
									||
									SIUnit == null && !RelationUnitMap.getUnit(rel).equals(unit.getBaseName())
								) {
								continue; // Incorrect unit, this cannot be the
										// relation.
							}
							}else if(unit == null && !pair.second.getUnit().equals("") && RelationUnitMap.getUnit(rel).equals(pair.second.getUnit())){ //for the cases where units are compound units.
								//do nothing, seems legit
							}else {
								if (!RelationUnitMap.getUnit(rel).equals("")) {
									continue; // this cannot be the correct relation.
							}
						}
						
						//THIS PAIR CAN EXPRESS RELATION REL
						Pair new_pair = new Pair(pair.first, rel);
						if(metaGraph.containsKey(new_pair)){
							EntityRelationDoc erd = metaGraph.get(new_pair);
							if(erd.doc.containsKey(pair.second)){
								erd.doc.get(pair.second).add(line);
							}else{
								ArrayList<String> s = new ArrayList<String>();
								s.add(line);
								erd.doc.put(pair.second, s);
							}
						}else{
							EntityRelationDoc erd = new EntityRelationDoc();
							if(erd.doc.containsKey(pair.second)){
								erd.doc.get(pair.second).add(line);
							}else{
								ArrayList<String> s = new ArrayList<String>();
								s.add(line);
								erd.doc.put(pair.second, s);
							}
							metaGraph.put(new_pair, erd);
						}
					}
				}
			}
		}
		br.close();
		
		//process metaGraph.
		String outFile = "/mnt/a99/d0/aman/graph";
		FileWriter fw = new FileWriter(new File(outFile));
		BufferedWriter bw = new BufferedWriter(fw);
		
		
		for(Pair<Country,String> pair: metaGraph.keySet()){
			System.out.println("Country: "+ pair.first + "\t Relation: "+pair.second);
			bw.write("Country: "+ pair.first + "\t Relation: "+pair.second+"\n");
			EntityRelationDoc erd = metaGraph.get(pair);
			System.out.println("Unique N nodes: "+ erd.doc.keySet().size());
			bw.write("Unique N nodes: "+ erd.doc.keySet().size()+"\n");
			int edges = 0;
			for(Number num: erd.doc.keySet()){
				System.out.println(" num: "+ num + "\t degree: "+ erd.doc.get(num).size());
				edges += erd.doc.get(num).size();
				bw.write(" num: "+ num + "\t degree: "+ erd.doc.get(num).size()+"\n");
				for(String sent: erd.doc.get(num)){
					bw.write(sent+"\n");
				}
			}
			System.out.println("Edges: "+edges+"\n\n");
			bw.write("Edges: "+edges+"\n\n\n");
			
		}
		bw.close();
	}
}
