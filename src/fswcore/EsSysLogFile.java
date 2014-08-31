package fswcore;

import app.CARE;

/**
 * Executive Services System Log File
 *  
 * @author David McComas
 *
 */
public class EsSysLogFile extends CfeFile
{

   public EsSysLogFile () {
      
      // Nothing to do
      
   } // End EsSysLogFile()
   
   @Override
   public void loadUserDataStr()
   {
      userDataStr = (new String (fileByteArray, FILE_IDX_APP_DATA, CARE.CFE_ES_SYSTEM_LOG_SIZE)).trim();

   } // End loadUserData()

   @Override
   public void loadUserDataStrArray() {
   
      // Unsupported 
      
   } // End loadUserDataStrArray()
   
} // End class EsSysLogFile
