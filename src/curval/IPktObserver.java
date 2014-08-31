package curval;

/**
 * Defines an interface for classes that want to be notified when a telemetry packet is updated.
 *  
 * @author David McComas
 *
 */
public interface IPktObserver {

   /**
    * Method called when a telemetry packet is received. 
    * 
    * @param streamId
    */
   public void update(int streamId);

} // End interface PktObserver
