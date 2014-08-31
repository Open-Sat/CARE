package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr; 

import app.*;

/**
 * Software Bus Pipe File
 *  
 * @author David McComas
 *
 */
public class SbPipeFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(SbPipeFile.class);
   
   /** SB Pipe Log file entry size defined by CFE_SB_PipeD_t */
   public static final int ENTRY_SIZE = 28 + 2* CARE.CFE_OS_MAX_API_NAME; 

   /** SB Routing Log file maximum size */
   public static final int FILE_SIZE = (CARE.CFE_SB_MAX_PIPES * ENTRY_SIZE);

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   private static final int STR_PER_REC = 11;

   public SbPipeFile () {
      
      // Nothing to do
      
   } // End SbPipeFile()
   

   @Override
   public void loadUserDataStr() {
      
      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_SB_MAX_PIPES; entry++) {
         
         logger.trace("SbPipeLogFile: entry " + entry);
 
         String eStr[] = new String[STR_PER_REC];
         eStr = parseRoutingFileRec(fileIndex);
         userDataStr = userDataStr + 
                   eStr[3] + ",  " + eStr[4]  + ",  " + eStr[2] + ",  " + eStr[1] + ",  " + eStr[5] + "\n" +
                   eStr[0] + ",  " + eStr[6]  + ",  " + eStr[7] + ",  " + eStr[8] + "\n"  + 
                   eStr[9] + ",  " + eStr[10] + "\n\n";
         
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
    * Note the contents of the CFE_SB_BufferD_t references is not written, just the pointer values.
    * typedef struct {
    *      uint8              InUse;
    *      CFE_SB_PipeId_t    PipeId;
    *      char               PipeName[OS_MAX_API_NAME];
    *      char               AppName[OS_MAX_API_NAME];
    *      uint16             Spare;
    *      uint32             AppId;
    *      uint32             SysQueueId;
    *      uint32             LastSender;
    *      uint16             QueueDepth;
    *      uint16             SendErrors;
    *      CFE_SB_BufferD_t  *CurrentBuff;
    *      CFE_SB_BufferD_t  *ToTrashBuff;
    * } CFE_SB_PipeD_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String[] parseRoutingFileRec(int fileIndex) {

       String[] entryStringArray = new String[STR_PER_REC];
       
       entryStringArray[0]  = "In Use: "         + DataPntStr.Uint8ToStr(fileByteArray, fileIndex); 
       entryStringArray[1]  = "Pipe ID: "        + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+1); 
       entryStringArray[2]  = "Pipe Name: "      + (new String (fileByteArray, fileIndex+2, CARE.CFE_OS_MAX_API_NAME)).trim(); 
       entryStringArray[3]  = "App Name: "       + (new String (fileByteArray, fileIndex+2+CARE.CFE_OS_MAX_API_NAME, CARE.CFE_OS_MAX_API_NAME)).trim(); 
       entryStringArray[4]  = "App ID: "         + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+4+2*CARE.CFE_OS_MAX_API_NAME); 
       entryStringArray[5]  = "Sys Queue ID: "   + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+8+2*CARE.CFE_OS_MAX_API_NAME); 
       entryStringArray[6]  = "Last Sender: "    + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+12+2*CARE.CFE_OS_MAX_API_NAME); 
       entryStringArray[7]  = "Queue Depth: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+16+2*CARE.CFE_OS_MAX_API_NAME); 
       entryStringArray[8]  = "Send Errors: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+18+2*CARE.CFE_OS_MAX_API_NAME); 
       entryStringArray[9]  = "CurrentBuf Ptr: " + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+20+2*CARE.CFE_OS_MAX_API_NAME); 
       entryStringArray[10] = "ToTrashBuf Ptr: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+22+2*CARE.CFE_OS_MAX_API_NAME); 

       return entryStringArray;
       
   } // End parseRoutingFileRec()
   
} // End class SbPipeFile
