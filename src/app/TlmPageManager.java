package app;

import java.util.HashMap;  
import java.util.Map;

import org.apache.log4j.Logger;

import gui.TlmDialog;

/**
 * Manage the open telemetry pages. A page is synonymous with a window.
 *  
 * @author David McComas
 *
 */
public class TlmPageManager {

   private static Logger logger=Logger.getLogger(TlmPageManager.class);

   protected Map<Integer,TlmDialog> tlmPktPageMap = new HashMap<Integer, TlmDialog>(CARE.MAX_OPEN_TLM_PAGES);
   
   /**
    * Construct a telemety page manager.
    */
   public TlmPageManager () {
      
      
   } // End tlmPageManager()
   
   /**
    * 
    * @param msgId
    * @param page
    */
   public void addPage (int msgId, TlmDialog page) {
      
      tlmPktPageMap.put(msgId, page);
      
   } // End addPage()
   
   /**
    * 
    * @param msgId
    * @return
    */
   public TlmDialog getPage (int msgId) {
      
      return tlmPktPageMap.get(msgId);
      
   } // End getPage()
   
   /**
    * Remove a telemetry page
    * 
    * @param msgId Message ID of the page to be removed
    * 
    * @return
    */
   public TlmDialog removePage (int msgId) {
         
      TlmDialog dlg = tlmPktPageMap.remove(msgId);
      if (dlg != null) {
         logger.trace("TlmPageManager: Successfully removed message " + Integer.toHexString(msgId));
      }
      else {
         logger.error("TlmPageManager: Unsuccess attempt to remove message " + Integer.toHexString(msgId));
      }
            
      return dlg; 

   } // End removePage()
         
} // End class tlmPageManager
