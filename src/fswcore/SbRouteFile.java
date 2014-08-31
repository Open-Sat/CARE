package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr; 

import app.*;

/**
 * Software Bus Routing File
 *  
 * @author David McComas
 *
 */
public class SbRouteFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(SbRouteFile.class);
   
   /** SB Routing Log file entry size defined by CFE_EVS_AppDataFile_t */
   public static final int ENTRY_SIZE = 6 + 2*CARE.CFE_OS_MAX_API_NAME; 

   /** SB Routing Log file maximum size */
   public static final int FILE_SIZE = (CARE.CFE_SB_HIGHEST_VALID_MSGID * ENTRY_SIZE);

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   private static final int STR_PER_REC = 6;

   public SbRouteFile () {
      
      // Nothing to do
      
   } // End SbRouteFile()
   
   
   @Override
   public void loadUserDataStr() {
      
      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_SB_HIGHEST_VALID_MSGID; entry++) {
         
         logger.trace("SbRouteLogFile: entry " + entry);
 
         String eStr[] = new String[STR_PER_REC];
         eStr = parseRoutingFileRec(fileIndex);
         userDataStr = userDataStr + 
                   eStr[4] + ", " + eStr[5] + "\n" +
                   eStr[0] + ", " + eStr[1] + ", " + eStr[2] + ", " + eStr[3] + "\n\n";
        
         fileIndex += ENTRY_SIZE; 
         if (fileIndex >= fileByteArray.length) break;
         
      } // End entry loop
         
   } // End loadUserDataStr()

   @Override
   public void loadUserDataStrArray() {
   
      // Unsupported 
      
   } // End loadUserDataStrArray()
   
   /**
    * 
    * typedef struct{
    *     CFE_SB_MsgId_t      MsgId;/**< \brief Message Id portion of the route
    *     CFE_SB_PipeId_t     PipeId;/**< \brief Pipe Id portion of the route
    *     uint8               State;/**< \brief Route Enabled or Disabled
    *     uint16              MsgCnt;/**< \brief Number of msgs with this MsgId sent to this PipeId
    *     char                AppName[OS_MAX_API_NAME];/**< \brief Pipe Depth Statistics
    *     char                PipeName[OS_MAX_API_NAME];/**< \brief Pipe Depth Statistics
    *  }CFE_SB_RoutingFileEntry_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String[] parseRoutingFileRec(int fileIndex) {

       String[] entryStringArray = new String[STR_PER_REC];
       
       entryStringArray[0] = "Msg ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex); 
       entryStringArray[1] = "Pipe ID: "   + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+2); 
       entryStringArray[2] = "State: "     + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+3); 
       entryStringArray[3] = "Msg Cnt: "   + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+4); 
       entryStringArray[4] = "App Name: "  + (new String (fileByteArray, fileIndex+6, CARE.CFE_OS_MAX_API_NAME)).trim(); 
       entryStringArray[5] = "Pipe Name: " + (new String (fileByteArray, fileIndex+6+CARE.CFE_OS_MAX_API_NAME, CARE.CFE_OS_MAX_API_NAME)).trim(); 
          
       return entryStringArray;
       
   } // End parseRoutingFileRec()
   
  
} // End class SbRouteFile
