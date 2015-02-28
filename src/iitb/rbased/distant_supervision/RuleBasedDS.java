package iitb.rbased.distant_supervision;

import iitb.rbased.main.ExtractFromPath;
import iitb.rbased.main.RuleBasedDriver;
import iitb.rbased.meta.KeywordData;
import iitb.rbased.meta.RelationUnitMap;
import iitb.rbased.util.Country;
import iitb.rbased.util.Number;
import iitb.rbased.util.Pair;
import iitb.rbased.util.Word;
import iitb.rbased.util.graph.Graph;

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

public class RuleBasedDS {

	public static void main(String args[]) throws Exception {

		String input_file = "/mnt/a99/d0/aman/number_sentences_egypt.tsv";

		RuleBasedDriver rbd = new RuleBasedDriver(true);
		KeywordData kwd = new KeywordData();
		UnitExtractor ue = new UnitExtractor();
		Set<String> relations = RelationUnitMap.getRelations();

		BufferedReader br = new BufferedReader(new FileReader(input_file));

		HashMap<Pair<Country, String>, NumberSentenceMap> LRtoGraphMap = new HashMap<Pair<Country, String>, NumberSentenceMap>();

		String line = null;
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {
			try {
				// process the line.
				System.out.println("At : " + lineNumber++ + ", " + line);

				// create the graph from this sentence
				Graph depGraph = null;
				depGraph = rbd.constructDepGraph(line);

				// get all the country number pairs in that sentence
				ArrayList<Pair<Country, Number>> pairs = rbd.getPairs(depGraph,
						line);

				for (Pair<Country, Number> pair : pairs) {
					ArrayList<Word> wordsOnDependencyGraphPath = depGraph
							.getWordsOnPath(pair.first, pair.second);

					if (ExtractFromPath.modifierPresent(pair,
							wordsOnDependencyGraphPath, kwd)) {
						continue;
					}
					// pair doesn't contains modifier in its shortest dependency
					// path
					// go through each relation and check
					for (String rel : relations) {
						Unit unit = ue.quantDict
								.getUnitFromBaseName(pair.second.getUnit());
						if (unit != null && !unit.getBaseName().equals("")) {
							Unit SIUnit = unit.getParentQuantity()
									.getCanonicalUnit();
							if (SIUnit != null
									&& !RelationUnitMap.getUnit(rel).equals(
											SIUnit.getBaseName())
									|| SIUnit == null
									&& !RelationUnitMap.getUnit(rel).equals(
											unit.getBaseName())) {
								continue; // Incorrect unit, this cannot be the
											// relation.
							}
						} else if (unit == null
								&& !pair.second.getUnit().equals("")
								&& RelationUnitMap.getUnit(rel).equals(
										pair.second.getUnit())) { // for the
																	// cases
																	// where
																	// units are
																	// compound
																	// units.
							// do nothing, seems legit
						} else {
							if (!RelationUnitMap.getUnit(rel).equals("")) {
								continue; // this cannot be the correct
											// relation.
							}
						}

						// THIS PAIR CAN EXPRESS RELATION REL
						Pair LRPair = new Pair(pair.first, rel);
						if (LRtoGraphMap.containsKey(LRPair)) {
							NumberSentenceMap erd = LRtoGraphMap.get(LRPair);
							if (erd.doc.containsKey(pair.second)) {
								erd.doc.get(pair.second).add(line);
							} else {
								ArrayList<String> s = new ArrayList<String>();
								s.add(line);
								erd.doc.put(pair.second, s);
							}
						} else {
							NumberSentenceMap erd = new NumberSentenceMap();
							if (erd.doc.containsKey(pair.second)) {
								erd.doc.get(pair.second).add(line);
							} else {
								ArrayList<String> s = new ArrayList<String>();
								s.add(line);
								erd.doc.put(pair.second, s);
							}
							LRtoGraphMap.put(LRPair, erd);
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e); // keep calm and carry on
			}
		}
		br.close();

		// process metaGraph.
		String outFile = "/mnt/a99/d0/aman/graph_egypt";
		FileWriter fw = new FileWriter(new File(outFile));
		BufferedWriter bw = new BufferedWriter(fw);

		for (Pair<Country, String> pair : LRtoGraphMap.keySet()) {
			System.out.println("Country: " + pair.first + "\t Relation: "
					+ pair.second);
			bw.write("Country: " + pair.first + "\t Relation: " + pair.second
					+ "\n");
			NumberSentenceMap erd = LRtoGraphMap.get(pair);
			System.out.println("Unique N nodes: " + erd.doc.keySet().size());
			bw.write("Unique N nodes: " + erd.doc.keySet().size() + "\n");
			int edges = 0;
			for (Number num : erd.doc.keySet()) {
				System.out.println(" num: " + num + "\t degree: "
						+ erd.doc.get(num).size());
				edges += erd.doc.get(num).size();
				bw.write(" num: " + num + "\t degree: "
						+ erd.doc.get(num).size() + "\n");
				for (String sent : erd.doc.get(num)) {
					bw.write(sent + "\n");
				}
			}
			System.out.println("Edges: " + edges + "\n\n");
			bw.write("Edges: " + edges + "\n\n\n");

		}
		bw.close();
	}
}
