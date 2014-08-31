package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr; 

import app.*;

/**
 * Software Bus Message Map File
 *  
 * @author David McComas
 *
 */
public class SbMsgMapFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(SbMsgMapFile.class);
   
   /** SB Map Log file entry size defined by CFE_SB_MsgMapFileEntry_t */
   public static final int ENTRY_SIZE = 4;
   
   /** SB Routing Log file maximum size */
   public static final int FILE_SIZE = (CARE.CFE_SB_HIGHEST_VALID_MSGID * ENTRY_SIZE);

   
   public SbMsgMapFile () {
      
      // Nothing to do
      
   } // End MsgMapFile()
   
   @Override
   public void loadUserDataStr() {
      
      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_SB_HIGHEST_VALID_MSGID; entry++) {
         
         logger.trace("SbPipeLogFile: entry " + entry);
 
         
         userDataStr = userDataStr + parseRoutingFileRec(fileIndex, entry);
        
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
    *     uint16  MsgId;/**< \brief Message Id which has been subscribed to
    *     uint16  Index;/**< \brief Routing table index where pipe destinations are found
    * }CFE_SB_MsgMapFileEntry_t;
    *
    * @param  fileIndex  Index into fileByteArray to the start of the entry
    * @param  entryIndex Map entry index
    */
   protected String parseRoutingFileRec(int fileIndex, int entryIndex) {

       String entryString;
       
       entryString  = "Entry[" + entryIndex + "] " +
                      "Msg ID: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex) +
                      ", Routing Table Index: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+2) + "\n\n";

       return entryString;
       
   } // End parseRoutingFileRec()
   
   
} // End class SbMsgMapFile
