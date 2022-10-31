import java.net.*;
import java.io.*;

public class ServerRouter extends Thread {
    private int SockNum;
    private SThread[] threadStore;
    ServerRouter(int num){
        SockNum = num;
        threadStore = new SThread[20];
    }

    public void threadstart(int port){
        try {
            for (SThread i : threadStore) {
                if (i.addr.equals(String.valueOf(port)) && !i.isAlive()) {
                    i.start();
                    break;
                }
            }
        }
        catch (Exception io){}
    }
    public void run() {
        try {
            Socket clientSocket = null; // socket for the thread
            Object[][] RoutingTable = new Object[20][2]; // routing table
            Boolean Running = true;
            int ind = 0; // indext in the routing table

            //Accepting connections
            ServerSocket serverSocket = null; // server socket for accepting connections
            try {
                serverSocket = new ServerSocket(SockNum);
                System.out.println("ServerRouter is Listening on port: " + SockNum +".");
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + SockNum + ".");
                System.exit(1);
            }

            // Creating threads with accepted connections
            while (Running) {
                try {
                    clientSocket = serverSocket.accept();
                    threadStore[ind] = new SThread(RoutingTable, clientSocket, ind, this); // creates a thread with a random port
                    ind++; // increments the index
                    System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    System.err.println("Client/Server failed to connect.");
                    System.exit(1);
                }
            }//end while

            //closing connections
            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException io){

        }

    }
}
