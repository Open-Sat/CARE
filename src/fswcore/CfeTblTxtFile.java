package fswcore;

import app.CARE;

/**
 * Text-based Table File
 *  
 * These files contain the binary cFE file header followed by a block
 * of text. The text is assumed to have carriage returns and line feeds
 * 
 * @author David McComas
 *
 */
public class CfeTblTxtFile extends CfeFile
{

   public CfeTblTxtFile () {
      
      super();
      
   } // End CfeTblTxtFile()
   
   @Override
   public void loadUserDataStr()
   {
      // TODO - Need to resolve the table size
      // userDataStr = (new String (fileByteArray, FILE_IDX_APP_DATA, CARE.CFE_ES_SYSTEM_LOG_SIZE)).trim();
      userDataStr = (new String (fileByteArray, FILE_IDX_APP_DATA, (fileByteArray.length - FILE_IDX_APP_DATA))).trim();

   } // End loadUserData()

   @Override
   public void loadUserDataStrArray() {
   
      // Unsupported 
      
   } // End loadUserDataStrArray()
   
} // End class CfeTblTxtFile
