/*******************************************************************************
 * 
 * This class provides a bridge between the network package (has no knowledge of CCSDS)
 * and the application/GUI.
 * 
 *******************************************************************************/
package app;

import java.util.concurrent.ConcurrentLinkedQueue;   

import org.apache.log4j.Logger;

import ccsds.CcsdsTlmPkt;
import network.*;
import curval.*;

/**
 * Manage receiving packets from the FSW host platform using a PktReader.
 * 
 * @author David McComas
 *
 */
public class FswTlmNetwork implements TlmPktEventInterface {

   private static Logger logger=Logger.getLogger(FswTlmNetwork.class);

   private TlmPktDatabase    tlmDatabase;
   private static PktReader  pktInput;
   private static Thread     pktInputThread;
   private static ConcurrentLinkedQueue<CcsdsTlmPkt> tlmPktQ = new ConcurrentLinkedQueue<CcsdsTlmPkt>();

   FswTlmNetwork(TlmPktDatabase tlmDatabase, String ipAddress, int tlmPort) {
      
	  this.tlmDatabase = tlmDatabase;
      pktInput = new PktReader(ipAddress, tlmPort, this);

      pktInputThread = new Thread(pktInput);
      pktInputThread.start();

   } // End FswTlmNetwork()

   /**
    * This method queues the message so it can be processed safely (WRT threads)
    * in the GUI event loop.
    * 
    * @param TlmData Byte array containing the entire telemetry packet 
    */
    public void addTlmPkt(byte[] tlmData) {
       
       CcsdsTlmPkt tlmPkt = new CcsdsTlmPkt(tlmData);
       
       logger.trace("addTlmMsg() - " + Integer.toHexString(tlmPkt.getStreamId()));
       synchronized (tlmDatabase) {
          tlmDatabase.updatePkt(tlmPkt.getStreamId(), tlmPkt);
          tlmPktQ.add(tlmPkt);
       }
       //TlmDatabase.notifyObservers(TlmPkt.getStreamId());

   } // End addTlmPkt()
   
   /**
    * Return the queue containing telemetry packets received from the FSW 
    * 
    * @return  Telemetry packet Q
    */
   public ConcurrentLinkedQueue<CcsdsTlmPkt> getTlmPktQ()
   {
      return tlmPktQ;
      
   } // End getTlmPktQ()
   
   /**
    * Get the next telemetry packet on the telemetry queue 
    * 
    * @return CCSDS telemetry packet
    */
   public CcsdsTlmPkt getTlmPkt() {
      
      return tlmPktQ.remove();
      
   } // End getTlmPkt()

   /**
    * Get the Packet Reader used to receive the FSW telemetry packets   
    * 
    * @return PktReader
    */
   public PktReader getPktReader() {
      
      return pktInput;
      
   } // End getPktReader()
      
   /**
    * Get the status of the current network PktReader
    * 
    * @return String containing status of the network PktReader
    */
   public String getStatus() {
      
      return pktInput.getStatus();
   
   } // End getStatus()
   
} // End class FswTlmNetwork
