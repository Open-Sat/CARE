package curval;

/**
 * Defines an interface for telemetry packet source to provide a telemetry packet 
 * received listener to an observer. 
 *  
 * @author David McComas
 * @see    IPktObserver
 *
 */
public interface IPktSource {

   /**
    * Register an IPktObserver to be notified when telemetry packets are received. 
    *  
    * @param observer class that implements IPktObserver interface  
    */
   public void registerObserver(IPktObserver observer);

   /**
    * Remove a previously registered IPktObserver. 
    *  
    * @param observer class that implements IPktObserver interface  
    */
   public void removeObserver(IPktObserver observer);

   /**
    * Notify registered IPktObservers when telemetry packet is received. 
    *  
    * @param streamId CCSDS Stream ID of the telemetry packet  
    */
   public void notifyObservers(int streamId);

} // End interface PktSource
