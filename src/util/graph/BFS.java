package util.graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class BFS {
		public static Integer[] run(Graph g, int src) {
			Queue<Integer> q = new LinkedList<Integer>();
			Integer par[] = new Integer[Graph.MAX + 1];
			boolean visited[] = new boolean[Graph.MAX + 1];
			q.add(src);
			visited[src] = true;
			while (!q.isEmpty()) {
				int curr = q.poll();
				for (int nbr : g.getNbr(curr)) {
					if (!visited[nbr]) {
						q.add(nbr);
						visited[nbr] = true;
						par[nbr] = curr;
					}
				}
			}
			return par;
		}

		public static ArrayList<Integer> getPath(Graph g, int src, int dest) {
			Integer par[] = BFS.run(g, src);
			ArrayList<Integer> res = new ArrayList<Integer>();
			res.add(dest);
			int at = dest;
			while (at != src) {
				at = par[at];
				res.add(at);
			}
			Collections.reverse(res);
			return res;
		}
	}