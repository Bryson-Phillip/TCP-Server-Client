   import java.io.*;
   import java.net.*;
   import java.util.Scanner;

   public class ServerThread extends Thread {
        int iter;
        String ext;
        public int switch_num;
        public int remote_portnum;
        public String filename;
        ServerThread(int num, String extension){
            iter = num;
            ext = extension;
            switch_num = 0;
            remote_portnum = 0;
            filename = "";
        }

       public void run() {
           try {
               Socket socket = null;// Variables for setting up connection and communication
               PrintWriter out = null; // for writing to ServerRouter
               Scanner in = null; // for reading form ServerRouter
               InetAddress addr = InetAddress.getLocalHost();
               if (addr.isLoopbackAddress()) {System.exit(1);}
               String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name
               int SockNum = 5550; // port number

               // Tries to connect to the ServerRouter
               try {
                   if (iter < 11) {
                       socket = new Socket(addr, SockNum - 1, addr, (SockNum + iter));
                   }
                   else{
                       socket = new Socket(addr, SockNum, addr, (SockNum + iter));
                   }
                   out = new PrintWriter(socket.getOutputStream(), true);
                   in = new Scanner(new InputStreamReader(socket.getInputStream()));
                   out.print(1+" ");
                   out.flush();
               } catch (UnknownHostException e) {
                   System.err.println("Don't know about router: " + routerName);
                   System.exit(1);
               } catch (IOException e) {
                   System.err.println("Couldn't get I/O for the connection to: " + routerName);
                   System.exit(1);
               }

               // Variables for message passing
               String toPeer; // messages sent to ServerRouter
               String fromPeer; // messages received from ServerRouter
               int portnum; // destination IP (Client)

               // Communication process (initial sends/receives)


               int i = 0;
               while (iter > 0) {

                   FileOutputStream fw = null;
                   FileOutputStream fw1 = null;

                   socket.setSoTimeout(500);
                   while (switch_num == 0){
                       try {
                           socket.getInputStream().read();
                           switch_num = 1;
                           break;
                       }catch(Exception io){}
                   }
                   socket.setSoTimeout(0);

                   if (switch_num == 1) {

                       // Communication while loop
                       while ((fromPeer = in.next()) != null) {
                           System.out.println("Incoming: " + fromPeer);
                           if (fromPeer.equals("Bye.")) {
                               out.print(fromPeer + " ");
                               out.flush();
                               break;
                           }
                           if (i == 0) {
                               portnum = Integer.parseInt(fromPeer);
                               out.print((portnum) + " ");// initial send (IP of the destination Client)
                               out.flush();
                               fromPeer = in.next();// initial receive from router (verification of connection)
                               i++;

                           } else if (i == 1) {
                               File file = new File(fromPeer + iter + "." + ext);
                               file.createNewFile();
                               fw = new FileOutputStream(file, false);
                               fw1 = new FileOutputStream(file, true);
                               i++;
                           } else if (i == 2) {
                               fw.write(Integer.parseInt(fromPeer));
                               fw.flush();
                               i++;
                           } else if (i == 3) {
                               fw1.write(Integer.parseInt(fromPeer));
                               fw1.flush();
                           }
                           toPeer = fromPeer; // converting received message to upper case
                           System.out.println("Returning: " + toPeer);
                           out.print(toPeer + " "); // sending the converted message back to the Client via ServerRouter
                           out.flush();
                       }
                   }
                   if (switch_num == 2) {

                       System.out.println("Path of the file to be transmitted for peer at port: "+ (int)(SockNum+iter));
                       System.out.println(filename);
                       System.out.println("The port number of the receiving peer:");
                       System.out.println(remote_portnum);

                       out.print((remote_portnum)+" ");// initial send (IP of the destination Client)
                       out.flush();
                       fromPeer = in.next();// initial receive from router (verification of connection)
                       out.print(5550+iter+" ");
                       out.flush();
                       socket.getInputStream().read();
                       fromPeer = in.next();

                       out.print(filename.substring(0, filename.indexOf('.')) + " ");
                       out.flush();
                       FileInputStream fi = new FileInputStream(filename);

                       // Communication while loop
                       long time = System.currentTimeMillis();
                       long time1 = 0;
                       try {
                           while (!(fromPeer = in.next()).equals("")) {
                               System.out.println("Returned: " + fromPeer);
                               if (fromPeer.equals("Bye.")) { // exit statement
                                   time1 = System.currentTimeMillis() - time;
                                   break;
                               }

                               toPeer = String.valueOf(fi.read()); // reading strings from a file
                               if (!toPeer.equals("-1")) {
                                   System.out.println("Outgoing: " + toPeer);
                                   out.println(toPeer + " "); // sending the strings to the Server via ServerRouter
                                   out.flush();
                               } else {
                                   out.println("Bye. ");
                               }

                           }

                       } catch (Exception E) {
                           out.print("Bye. ");
                           out.flush();
                       }
                       for (int f = 0; f < 100; f++) {
                           System.out.println(time1);
                       }
                   }
                   switch_num = 0;
               }


               // closing connections
               out.close();
               in.close();
               socket.close();
               while(true){}


           }
           catch (Exception io) {
               io.printStackTrace();
           }
       }
   }
