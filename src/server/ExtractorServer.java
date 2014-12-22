package server;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ExtractorServer {
	RuleBasedDriver rbsaed;
	
    public static void main(String[] args) throws IOException {
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
        while(i > 0) {
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
            
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        System.out.println("Accepted Connection");
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(Double.toString(Math.random()));
        out.close();
        clientSocket.close();
        i--;
        }
        serverSocket.close();
    }
}