package fswcore;

import org.apache.log4j.Logger;

import ccsds.CcsdsTlmPkt; 
import app.CARE;

/**
 * Event Services Event Log File
 *  
 * @author David McComas
 *
 */
public class EvsLogFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(EvsLogFile.class);

   /** EVS Log file entry size defined by CFE_EVS_Packet_t */
   public static final int ENTRY_SIZE = (CcsdsTlmPkt.CCSDS_TLM_HDR_LEN+CARE.CFE_OS_MAX_API_NAME + 12 + CARE.CFE_EVS_MAX_MESSAGE_LENGTH + 2);
      

   public EvsLogFile () {
         
      // Nothing to do
         
   } // End EvsLogFile()
      

   @Override
   public void loadUserDataStr() {
         
      int fileIndex = FILE_IDX_APP_DATA;
         
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_EVS_LOG_MAX; entry++) {
            
            logger.trace("EvsLogFile: entry " + entry);
    
            /*
            String eStr[] = new String[EvsApp.STR_PER_ENTRY];
            eStr = EvsApp.parseEventPkt(fileByteArray, fileIndex);
            userDataStr = userDataStr + 
                eStr[EvsApp.IDX_SECS]+"." + eStr[EvsApp.IDX_SUB_SECS]+"-" + eStr[EvsApp.IDX_APP_NAME]+": " + 
                eStr[EvsApp.IDX_EVENT_TYP] + " - "+eStr[EvsApp.IDX_EVENT_TYP]+" - "+eStr[EvsApp.IDX_MSG_TEXT]+"\n";  
            */
            userDataStr = userDataStr + EvsApp.formatEventStr(fileByteArray, fileIndex) + "\n";
            fileIndex += ENTRY_SIZE; 
            if (fileIndex >= fileByteArray.length) break;
  
         } // End entry loop
            
   } // End loadUserDataStr()

   @Override
   public void loadUserDataStrArray() {
   
      // Unsupported 
      
   } // End loadUserDataStrArray()
   
   
} // End class EvsLogFile
