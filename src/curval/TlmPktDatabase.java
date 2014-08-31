package curval;

import java.util.ArrayList;    

import org.apache.log4j.Logger;

import app.CARE;
import ccsds.*;

/**
 * Telemetry packet database that contains the most recent copy for every 
 * telemetry packet.
 * 
 * @author David McComas
 *
 */
public class TlmPktDatabase implements IPktSource {

   private static Logger logger=Logger.getLogger(TlmPktDatabase.class);

   final static private CcsdsTlmPkt[]   pktDatabase = new CcsdsTlmPkt[CARE.MAX_MSG_ID_CNT]; 
   final private ArrayList<IPktObserver> observers  = new ArrayList<IPktObserver>();
   
   /**
    * Construct the database.
    */
   public TlmPktDatabase() {
      
      // TODO Arrays.fill(null, pktDatabase);
      
   } // End TlmPktDatabase()
   
   /**
    * Update the packet database and notify registered observers.
    * 
    * @param streamId  CCSDS Stream ID
    * @param tlmPkt    telemetry packet
    */
   public synchronized void updatePkt(int streamId, CcsdsTlmPkt tlmPkt) {
      
      logger.trace("Update Packet " + Integer.toHexString(streamId));
      pktDatabase[streamId] = tlmPkt;
      notifyObservers(streamId);
         
   }  // End updatePkt()
   
   @Override
   public void registerObserver(IPktObserver observer) {
      
      observers.add(observer);

   } // End registerObserver()

   @Override
   public void removeObserver(IPktObserver observer) {
      
      observers.remove(observer);

   } // End removeObserver()

   @Override
   public void notifyObservers(int streamId) {
      
      logger.trace("Notifying Observers");
      for (IPktObserver ob : observers) {
         ob.update(streamId);
      }
      
   } // End notifyObservers()

   /**
    * Get the current packet for the specified Stream ID.
    * 
    * @param streamId  CCSDS Stream ID
    * @return          the CCSDS telemetry packet
    */
   public synchronized CcsdsTlmPkt getPkt(int streamId) {
      
      logger.trace("getTlmPktDatabase for stream ID " + Integer.toHexString(streamId));
      return pktDatabase[streamId];  
      
   } // End getPkt()

   /**
    * Diagnostic method that dumps all non-null telemetry packets in the database.
    */
   public void debugDump() {
      
      for (int i=0; i < pktDatabase.length; i++) {
         if (pktDatabase[i] != null) {
            logger.debug("debugDump: pktDatabase["+Integer.toHexString(i)+"] = " +  Integer.toHexString(pktDatabase[i].getStreamId()));
         }
      }
      
   } // End debugDump()
   
} // End class TlmPktDatabase
