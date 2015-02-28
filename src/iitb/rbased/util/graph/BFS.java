package iitb.rbased.util.graph;

import iitb.rbased.util.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class BFS {
	public static Word[] run(Graph g, Word src) {
		Queue<Word> q = new LinkedList<Word>(); // bfs queue
		Word par[] = new Word[Graph.MAX + 1]; // to store parent of each node
		boolean visited[] = new boolean[Graph.MAX + 1]; // visited flag, indexed by word index
		q.add(src);
		visited[src.getIdx()] = true;
		while (!q.isEmpty()) {
			Word curr = q.poll();
			ArrayList<Word> neighbors = g.getNbr(curr);
			if(null == neighbors) {
				continue;
			}
			for (Word nbrWord: neighbors) {
				int nbr = nbrWord.getIdx();
				if (!visited[nbr]) {
					q.add(nbrWord);
					visited[nbr] = true;
					par[nbr] = curr;
				}
			}
		}
		return par;
	}

	public static ArrayList<Word> getPath(Graph g, Word src, Word dest) {
		Word par[] = BFS.run(g, src);
		ArrayList<Word> path = new ArrayList<Word>();
		if(null == par[0]) { //nothing found
			return path;
		}
		path.add(dest);
		Word at = dest;
		while (at != src) {
			at = par[at.getIdx()];
			path.add(at);
		}
		Collections.reverse(path);
		return path;
	}
}