import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Pattern;


public class SThread extends Thread 
{
	private Object [][] RTable; // routing table
	private PrintWriter out, outTo; // writers (for writing back to the machine and to destination)
   private Scanner in, inFrom; // reader (for reading from the machine connected to)
	public String inputLine, outputLine, destination, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // indext in the routing table
	private ServerRouter Parent;

	// Constructor
	SThread(Object [][] Table, Socket toClient, int index, ServerRouter parent) throws IOException
	{
			out = new PrintWriter(toClient.getOutputStream(), true);
			in = new Scanner(new InputStreamReader(toClient.getInputStream()));
			RTable = Table;
			addr = String.valueOf(toClient.getPort());
			RTable[index][0] = addr; // IP addresses 
			RTable[index][1] = toClient; // sockets for communication
			ind = index;
			Parent = parent;
			int hold = Integer.parseInt(in.next());
			if (hold == 1){
				return;
			}
			else{
				this.start();
			}
	}

	public void outside_connect(int port){
		try {
			InetAddress addr1 = InetAddress.getLocalHost();
			Socket Socket = new Socket(addr1, port, addr1, 0);
			outSocket = (Socket) Socket;
			outTo = new PrintWriter(outSocket.getOutputStream(), true);
			outTo.print(2+" ");
			outTo.flush();

			outTo.print(destination + " ");// initial send (IP of the destination Client)
			outTo.flush();
			inFrom = new Scanner(new InputStreamReader(outSocket.getInputStream()));
			inFrom.next();// initial receive from router (verification of connection)

			while ((inputLine = in.next()) != null) {
				outputLine = inputLine;
				if (outSocket != null) {
					if (outputLine.equals("Bye.")) { // exit statement
						outTo.print(outputLine + " ");
						outTo.flush();
						break;
					}
					// passes the input from the machine to the output string for the destination
					outTo.print(outputLine + " "); // writes to the destination
					outTo.flush();
				}


			}// end while
		}
		catch (UnknownHostException io){}
		catch (IOException io){}
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run()
	{
		try {
			// Initial sends/receives
			destination = in.next(); // initial read (the destination for writing)
			System.out.println("Forwarding to " + destination);
			out.print("Connected_to_the_router. ");
			out.flush(); // confirmation of connection

		// waits 10 seconds to let the routing table fill with all machines' information
		try{
    		Thread.currentThread().sleep(100);
	   }
		catch(InterruptedException ie){
		System.out.println("Thread interrupted");
		}
		int found = 0;
		// loops through the routing table to find the destination
		for ( int i=0; i<19; i++)
		{
			if (destination.equals((String) RTable[i][0])) {
				found = 1;
				outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
				System.out.println("Found destination: " + destination);
				outTo = new PrintWriter(outSocket.getOutputStream(), true); // assigns a writer
				break;
			}
		}

		if (found == 0){
			if (Integer.parseInt(destination) > 5560){
				outside_connect(5550);
			}
			else{
				outside_connect(5549);
			}
			return;
		}
		// Communication loop
			int i = 0;
			outTo.print('r');
			outTo.flush();
		while ((inputLine = in.next()) != null) {
            System.out.println("Client/Server said: " + inputLine);
			outputLine = inputLine;
			if (outSocket != null){
				if (outputLine.equals("Bye.")) { // exit statement
					outTo.print(outputLine+ " ");
					outTo.flush();
					break;
				}
				// passes the input from the machine to the output string for the destination
				outTo.print(outputLine + " "); // writes to the destination
				outTo.flush();
			}
			i++;
			if (i == 1){

				Parent.threadstart(Integer.parseInt(destination));
			}
       }// end while

		 }// end try
			catch (Exception e) {
               System.err.println("Could not listen to socket.");
               System.exit(1);
         }
	}
}