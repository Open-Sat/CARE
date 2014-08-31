package network;

import java.util.Date;  
import java.io.IOException;
import java.net.*;

import org.apache.log4j.Logger;

/**
 * Creates and manages a socket for sending command packets.
 * 
 * @author David McComas
 * 
 */
public class PktWriter
{
   private static Logger logger=Logger.getLogger(PktWriter.class);

   protected  String          ipAddress;
   protected  int             pktPort;
   protected  long            pktCount = 0;
   protected  DatagramSocket  pktSock;
   protected  boolean         createdSocket = false;
   protected  Date            creationDate;
 
   /**
    * Constructor
    * 
    * @param cmdPort
    */
   public PktWriter(String ipAddress, int cmdPort) {
     
	   this.ipAddress = ipAddress;
       this.pktPort = cmdPort;
       
       try {
          
           pktSock = new DatagramSocket();
           createdSocket = true;
           creationDate = new Date();
           logger.trace("PktWriter: Created socket");
       } 
       catch(IOException ex) {
          
           System.err.println("PktWriter: Error creating DatagramSocket");
           ex.printStackTrace();
       }

   } // End PktWriter()
   
   /**
    * Send a command to to the command port.
    * 
    * @param cmdData
    * @param cmdLen
    */
   public void WriteCmdPkt(byte[] cmdData, int cmdLen) {
    
      try {
      
         //DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getLocalHost(),pktPort);
    	 //DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("169.254.0.0"),pktPort);
    	 //DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("192.168.146.128"),pktPort);
    	 //DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("127.0.0.1"),pktPort);pktSock.send(CmdDatagram);
    	 //DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("192.168.211.001"),pktPort);
     	 //VMnet1 DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("192.168.211.128"),pktPort);
     	 //VMnet8 DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("192.168.146.128"),pktPort);
      	 //DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName("10.0-.0.19"),pktPort);
     	 DatagramPacket CmdDatagram = new DatagramPacket(cmdData, cmdLen, InetAddress.getByName(ipAddress),pktPort);
    	 pktSock.send(CmdDatagram);
    	 pktCount++;
         
      } 
      catch(Exception e)
      {
         System.err.println("Pktwriter: Error writing datagram");
         System.err.println(e);
         e.printStackTrace();
      }
        
   } // End WriteCmdPkt()

   /**
    * Get the current status and statistics on the socket 
    * 
    * @return string containing the current socket status.
    */
   public String getStatus() {
   
      String status = "PktWriter Socket: ";

      if (createdSocket) {
         
         status += "Created: " + creationDate.toString() + ", " + 
                   "Port: " + pktSock.getLocalPort() + ", " + 
                   "Sent: " + pktCount;
      }
      else {
         
         status += "Socket not created\n\n";
      
      }
      return status;
      
   } // End getStatus()
   
} // End class PktWriter
