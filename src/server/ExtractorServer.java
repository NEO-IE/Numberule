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
		Integer PORT = 4080;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println(e);
			System.err.println("Could not listen on port:  " + PORT);
			System.exit(1);
		}
		int i = 20;
		while (i > 0) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();

			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine = in.readLine();
			ArrayList<Relation> rels = rbd.extract(inputLine);

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
					true);
			for (Relation rel : rels) {
				out.println(rel.toString());
			}
			out.close();
			clientSocket.close();
			i--;
		}
		serverSocket.close();
	}
}