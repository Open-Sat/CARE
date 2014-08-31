package network;

import java.util.Date;  
import java.io.IOException;
import java.net.*;

import org.apache.log4j.Logger;

/**
 * Create and manage a socket for receiving telemetry. 
 * 
 * @author David McComas
 */
public class PktReader implements Runnable
{

   static final public int DATA_PKT_CNT = 10;
   static final public int DATA_PKT_BYTE_LEN = 1024;

   private static Logger logger=Logger.getLogger(PktReader.class);
   
   private TlmPktEventInterface tlmPktEvent;
   
   protected  String           ipAddress;
   protected  int              pktPort  = 0;
   protected  long             pktCount = 0;
   protected  DatagramSocket   pktSock;
   protected  boolean          createdSocket = false;
   protected  Date             creationDate;
   protected  int              dataPktIndex;
   protected  DatagramPacket[] dataPktArray = new DatagramPacket[DATA_PKT_CNT];
   
   /**
    * Construct a telemetry packet reader.
    * 
    * @param pktPort      port number for receiving packets.
    * @param tlmPktEvent  interface class to receive the packets
    */
   public PktReader(String ipAddress, int pktPort, TlmPktEventInterface tlmPktEvent) {
      
	   /*
       ** Only tested on local host so may need some options:  
       ** byte [] serverAddress = new byte[] {(byte)192,(byte)168,(byte)211,(byte)128};  
       ** pktSock = new DatagramSocket(pktPort,InetAddress.getByAddress(serverAddress));  
	   **
	   ** pktSock.connect(InetAddress.getByName("192.168.211.001"),pktPort);
           //pktSock = new DatagramSocket(pktPort,InetAddress.getByName("localhost")); - Connected but no data received
           //pktSock.bind(InetSocketAddress.getByName("192.168.211.001"));
           //pktSock = new DatagramSocket(pktPort,InetAddress.getByName("192.168.211.1"));
           //pktSock = new DatagramSocket(pktPort,InetAddress.getByName("192.168.211.128"));
           //pktSock = new DatagramSocket(pktPort,InetAddress.getByName("eth5"));
           //
           //pktSock = new DatagramSocket(new InetSocketAddress(pktPort)); // Wildcard IP Address 
	   */
	   
	   // Preallocate data packets for speed
	   for (int i = 0; i < DATA_PKT_CNT; i++) {
	      dataPktArray[i] = new DatagramPacket(new byte[DATA_PKT_BYTE_LEN],DATA_PKT_BYTE_LEN);
  	   }
       dataPktIndex = 0;
       
	   this.ipAddress = ipAddress;
	   this.pktPort = pktPort;
       this.tlmPktEvent = tlmPktEvent; 
       try {
          
           this.pktCount = 0;
           pktSock = new DatagramSocket(pktPort); // Defaults to local host or wild card IP?
           createdSocket = true;
           creationDate = new Date();
           logger.debug("PktReader: Created socket on port " + pktPort + ", " + pktSock.getLocalSocketAddress()+ ", " + pktSock.getRemoteSocketAddress());
       } 
       catch(IOException ex) {
          
           System.err.println("Error creating DatagramSocket on port " + pktPort);
           ex.printStackTrace();
       }

   } // End PktReader()
   
   /**
    * 
    */
   public void run() {
        
      if (createdSocket)
      {
         logger.debug("PktReader: Start while loop");

         // pktSock.receive() will sleep if no packets so no need for manual sleep in loop 
         while (true) {
            
            try {
               
               DatagramPacket dataPacket = dataPktArray[dataPktIndex];
               logger.trace("PktReader: Calling pktSock.receive(), inBuffIndex = " + dataPktIndex +  "...");
               pktSock.receive(dataPacket);
               logger.trace("Received data on address " + dataPacket.getAddress() + ", port " + dataPacket.getPort());
               pktCount++;
               dataPktIndex++;
               if (dataPktIndex >= DATA_PKT_CNT) dataPktIndex = 0;
               tlmPktEvent.addTlmPkt(dataPacket.getData());
               
            }
            catch(Exception e) {
               
               System.err.println("PktReader: Error reading text from the server. Exception Msg = " + e.getMessage() + "\n");
               e.printStackTrace();
            }
              
         } // End while
      } // End if SocketCreated
      else {
         System.out.println("PktReader: Socket creation failure prevented while loop from starting");
      }
      
   } // End run()

   /**
    * Get the current status and statistics on the socket 
    * 
    * @return string containing the current socket status.
    */
   
   public String getStatus() {
   
      String status = "MsgReader Socket: ";

      if (createdSocket) {
         
         status += "Created: "  + creationDate.toString() + ", " + 
                   "Port: "     + pktSock.getLocalPort() + ", " + 
                   "Received: " + pktCount;
      }
      else {
         
         status += "Socket not created\n\n";
      
      }
      
      return status;
      
   } // End getStatus()
   
} // End class PktReader
