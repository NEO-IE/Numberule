package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import main.RuleBasedDriver;
import util.Relation;

public class ExtractorServer {
	public static void main(String[] args) throws IOException {
		RuleBasedDriver rbd;
		rbd = new RuleBasedDriver(true);
		rbd.setUnitsActive(false);
		Integer PORT = 4080;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println(e);
			System.err.println("Could not listen on port:  " + PORT);
			System.exit(1);
		}
		
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();

			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			System.out.println("Got connection from " + clientSocket);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine = in.readLine();
			String ipSplit[] = inputLine.split(":");
			boolean useUnits = (Integer.parseInt(ipSplit[0])) == 1;
			System.out.println(useUnits);
			rbd.setUnitsActive(useUnits);
			System.out.println("Extracting from " + inputLine );
			ArrayList<Relation> rels = rbd.extract(ipSplit[1]);

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
					true);	
			StringBuffer res = new StringBuffer();
			for (Relation rel : rels) {
				res.append(rel);
				res.append("<br/>");
			}
			out.println(res.toString());
			out.close();
			clientSocket.close();
			
		}
		//serverSocket.close();
	}
}