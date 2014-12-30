package util.graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import util.Word;


public class BFS {
		public static Word[] run(Graph g, Word src) {
			Queue<Word> q = new LinkedList<Word>(); //bfs queue
			Word par[] = new Word[Graph.MAX + 1]; //to store parent of each node
			boolean visited[] = new boolean[Graph.MAX + 1]; //visited flag, indexed by word index
			q.add(src);
			visited[src.idx] = true;
			while (!q.isEmpty()) {
				Word curr = q.poll();
				for (Word nbrWord : g.getNbr(curr)) {
					int nbr = nbrWord.idx;
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
			path.add(dest);
			Word at = dest;
			while (at != src) {
				at = par[at.idx];
				path.add(at);
			}
			Collections.reverse(path);
			return path;
		}
	}