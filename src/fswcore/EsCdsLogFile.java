package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr;

import app.*;

/**
 * Executive Services Critical Data Store Registry Log File
 *  
 * @author David McComas
 *
 */
public class EsCdsLogFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(EsCdsLogFile.class);

   /** ES CDS Registry Log file entry size defined by CFE_ES_CDSRegDumpRec_t */
   public static final int ENTRY_SIZE = 10 + CARE.CFE_ES_CDS_MAX_FULL_NAME_LEN; 

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   public static final int FILE_SIZE = (CARE.CFE_ES_CDS_MAX_NUM_ENTRIES * ENTRY_SIZE);

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   private static final int STR_PER_REC = 4;

   public EsCdsLogFile () {
      
      // Nothing to do
      
   } // End EsCdsLogFile()

   @Override
   public void loadUserDataStr() {
      
      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_ES_CDS_MAX_NUM_ENTRIES; entry++) {
         
         logger.trace("EsCdsLogFile: entry " + entry);
 
         String eStr[] = new String[STR_PER_REC];
         eStr = parseCdsRegDumpRec(fileIndex);
         userDataStr = userDataStr + eStr[3] + eStr[0] + eStr[1] + eStr[2];
        
         fileIndex += ENTRY_SIZE; 
         if (fileIndex >= fileByteArray.length) break;
         
      } // End entry loop
         
   } // End loadUserDataStr()

   @Override
   public void loadUserDataStrArray() {
   
      // Unsupported 
      
   } // End loadUserDataStrArray()
   
   
   /*
    * 
    *    typedef struct
    *    {
    *        uint32                Handle;          /**< \brief Handle of CDS
    *        uint32                Size;            /**< \brief Size, in bytes, of the CDS memory block
    *        boolean               Table;           /**< \brief Flag that indicates whether CDS contains a Critical Table
    *        char                  Name[CFE_ES_CDS_MAX_FULL_NAME_LEN]; /**< \brief Processor Unique Name of CDS
    *        uint8                 ByteAlignSpare1; /**< \brief Spare byte to insure structure size is multiple of 4 bytes
    *    } CFE_ES_CDSRegDumpRec_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String[] parseCdsRegDumpRec(int fileIndex) {

       String[] entryStringArray = new String[STR_PER_REC];
       
       entryStringArray[0] = "Handle: "          + DataPntStr.Uint32ToStr(fileByteArray, fileIndex)    + "\n"; 
       entryStringArray[1] = "Size: "            + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+4)  + "\n"; 
       entryStringArray[2] = "Table (bool): "    + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+8)  + "\n"; 
       entryStringArray[3] = "Name: "            + (new String (fileByteArray, fileIndex+9, CARE.CFE_ES_CDS_MAX_FULL_NAME_LEN)).trim() + "\n"; 
          
       return entryStringArray;
       
   } // End parseCdsRegDumpRec()
   
} // End class EsCdsLogFile
