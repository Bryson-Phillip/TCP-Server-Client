import java.io.*;
import java.util.Scanner;

   public class TCPClient_Server {
           public static ServerRouter Sr1 = null;
           public static ServerRouter Sr2 = null;
           public static String [][] temp;
           public static void main (String[]args) {
               try {
                   temp = new String[10][3];
                   Scanner scan = new Scanner(System.in);
                   Sr1 = new ServerRouter(5549);
                   Sr2 = new ServerRouter(5550);
                   Sr1.start();
                   Sr2.start();
                   Thread.currentThread().sleep(1000);
                   int a = 0;
                   while (true) {
                       System.out.println("Enter port number of a transmitting peer: ");
                       temp[a][0] = scan.nextLine();
                       System.out.println("Enter port number of a receiving peer: ");
                       temp[a][1] = scan.nextLine();
                       System.out.println("Enter the file to be transmitted: ");
                       temp[a][2] = scan.nextLine();
                       System.out.println("Add another pair?");
                       String temp4 = scan.nextLine();;
                       a++;
                       if (temp4.equals("no")) {
                           break;
                       }

                   }

                   ServerThread[] tempstor = new ServerThread[20];
                   for (int i = 1; i < 21; i++) {
                       ServerThread SERVER = new ServerThread(i, "txt");
                       SERVER.start();
                       tempstor[i - 1] = SERVER;
                   }


                   Thread.currentThread().sleep(1000);

                   for (int i = 0; i < a; i++) {
                       tempstor[Integer.parseInt(temp[i][0]) - 5551].remote_portnum = Integer.parseInt(temp[i][1]);
                       tempstor[Integer.parseInt(temp[i][0]) - 5551].filename = temp[i][2];
                       tempstor[Integer.parseInt(temp[i][0]) - 5551].switch_num = 2;
                       Sr1.threadstart(Integer.parseInt(temp[i][0]));
                       Sr2.threadstart(Integer.parseInt(temp[i][0]));
                   }

               }catch (Exception io){}
       }
   }
