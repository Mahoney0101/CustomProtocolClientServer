import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class TMPServerThread implements Runnable {
   static final String endMessage = ".";
   static final String loginMessage = "login";
   static final String logoutMessage = "logout";
   static final String downloadMessage = "download";
   static final String uploadMessage = "upload";


    public Boolean findFile(String name,File file)
    {
        name = name+".txt";
        Boolean check = false;
        File[] list = file.listFiles();
        if(list!=null)
            //return "not null";
        for (File fil : list)
            {
                if (fil.isDirectory())
                {
                    findFile(name,fil);                }
                else if (name.equalsIgnoreCase(fil.getName()))
                {
                    check = true;
                }
            }
        return check;
    }

   MyStreamSocket myDataSocket;

   TMPServerThread(MyStreamSocket myDataSocket) {
      this.myDataSocket = myDataSocket;
   }

    File file = new File("ServerFiles/UserFiles");
    String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

    Boolean exists = false;

   public void run( ) {
       String user = "";
       Boolean loggedIn = false;
      boolean done = false;
      String message;
      try {
         while (!done) {
             message = myDataSocket.receiveMessage( );
             String[] socketMessage = message.split("\\;");
/**/         System.out.println("message received: "+ message);
             if ((socketMessage[0]).equals(endMessage)){
                //Session over; close the data socket.
/**/            System.out.println("Session over.");
                myDataSocket.close( );
                done = true;
             } 
             else if(socketMessage[0].trim().equals(loginMessage)){
               //Session over; close the data socket.
               try{
                   if(loggedIn == true){
                       myDataSocket.sendMessage("104: user already logged in");
                   }
                   for(int i=0;i<directories.length;i++) {
                       if (directories[i].trim().equals(socketMessage[1])) {
                           exists=true;
                           user=directories[i].trim();
                       }
                   }
                   String path = "ServerFiles/UserFiles/"+user;
                   if(exists==true && findFile(socketMessage[2],new File(path)) == true){
                       loggedIn = true;
                       myDataSocket.sendMessage("101: user logged in " + findFile(socketMessage[2],new File(path)) );
                   }
                   else{
                       Path userPath = Paths.get("ServerFiles/UserFiles/"+socketMessage[1]);
                       Files.createDirectory(userPath);
                       Path filePath = Paths.get("ServerFiles/UserFiles/"+socketMessage[1]+"/"+socketMessage[2]+".txt");
                       Files.createFile(filePath);
                       loggedIn = true;
                       myDataSocket.sendMessage("103: account created");
                   }
                   //else if()
               }
               catch(Exception e){
               myDataSocket.sendMessage("102"+ e);
               }
            } 
            else if((socketMessage[0]).equals(logoutMessage)){
               try{
                   loggedIn = false;
                  myDataSocket.sendMessage("201: user logged out");
                  }
                  catch(Exception e){
                  myDataSocket.sendMessage("202: "+e);
                  }
            } 
            else if(socketMessage[0].equals(uploadMessage)){
               try{
                   if(!loggedIn){
                       myDataSocket.sendMessage("303: not logged in");
                   }
                   else{
                       PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ServerFiles/MessageFiles/messages.txt", true)));
                       out.println(user+": "+socketMessage[1]);
                       out.close();
                       myDataSocket.sendMessage("301: upload complete");
                   }
                  }
                  catch(Exception e){
                  myDataSocket.sendMessage("302: "+ e);
                  }
            } 
            else if(socketMessage[0].equals(downloadMessage)){
                String messages = "";
               try{
                   if(!loggedIn){
                       myDataSocket.sendMessage("403: not logged in");
                   }
                   else {
                       try {
                           File myObj = new File("ServerFiles/MessageFiles/messages.txt");
                           Scanner myReader = new Scanner(myObj);
                           while (myReader.hasNextLine()) {
                               String data = myReader.nextLine();
                               messages+=(data+";");
                           }
                           myReader.close();
                           myDataSocket.sendMessage("401: "+messages);
                       } catch (FileNotFoundException e) {
                           System.out.println("An error occurred.");
                           e.printStackTrace();
                       }
                   }
                   }
                  catch(Exception e){
                  myDataSocket.sendMessage("402:" + e);
                  }
            } 
            //end if
             else {
                // Now send the echo to the requestor
                myDataSocket.sendMessage("syntax error "+ Arrays.toString(socketMessage));

             } //end else
          } //end while !done
        }// end try
        catch (Exception ex) {
           System.out.println("Exception caught in thread: " + ex);
        } // end catch
   } //end run
} //end class 
