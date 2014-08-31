package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr; 
import app.CARE;

/**
 * Table Registration File
 *  
 * @author David McComas
 *
 */
public class TblRegLogFile extends CfeFile {

   private static Logger logger=Logger.getLogger(TblRegLogFile.class);

   /** Table Registry Log file entry size defined by CFE_TBL_RegDumpRec_t */
   public static final int ENTRY_SIZE = 160;
   
   public TblRegLogFile () {
      
      // Nothing to do
      
   } // End EsTaskLogFile()

   @Override
   public void loadUserDataStr() {

      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_TBL_MAX_NUM_TABLES; entry++) {
         
         logger.trace("TblRegLogFile: entry " + entry);
 
         userDataStr = userDataStr + parseTaskInfo(fileIndex); 
         fileIndex += ENTRY_SIZE; 
         if (fileIndex >= fileByteArray.length) break;
    
      } // End entry loop

   } // loadUserDataStr()

   @Override
   public void loadUserDataStrArray() {
   
      // Unsupported 
      
   } // End loadUserDataStrArray()
   
   /*
    * 
    * typedef struct 
    * {
    *     uint32                      Size;               /**< \brief Size, in bytes, of Table
    *     CFE_TIME_SysTime_t          TimeOfLastUpdate;   /**< \brief Time when Table was last updated
    *     uint32                      NumUsers;           /**< \brief Number of applications that are sharing the table
    *     int32                       LoadInProgress;     /**< \brief Flag identifies inactive buffer and whether load in progress
    *     uint32                      FileCreateTimeSecs;    /**< \brief File creation time from last file loaded into table
    *     uint32                      FileCreateTimeSubSecs; /**< \brief File creation time from last file loaded into table
    *     uint32                      Crc;                /**< \brief Most recent CRC computed by TBL Services on table contents
    *     boolean                     ValidationFunc;     /**< \brief Flag indicating whether table has an associated Validation func
    *     boolean                     TableLoadedOnce;    /**< \brief Flag indicating whether table has been loaded once or not
    *     boolean                     LoadPending;        /**< \brief Flag indicating an inactive buffer is ready to be copied
    *     boolean                     DumpOnly;           /**< \brief Flag indicating Table is NOT to be loaded
    *     boolean                     DblBuffered;        /**< \brief Flag indicating Table has a dedicated inactive buffer
    *     char                        Name[CFE_TBL_MAX_FULL_NAME_LEN];   /**< \brief Processor specific table name
    *     char                        LastFileLoaded[OS_MAX_PATH_LEN];   /**< \brief Filename of last file loaded into table
    *     char                        OwnerAppName[OS_MAX_API_NAME];     /**< \brief Application Name of App that Registered Table
    *     boolean                     CriticalTable;                     /**< \brief Identifies whether table is Critical or Not
    * } CFE_TBL_RegDumpRec_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String parseTaskInfo(int fileIndex) {

       String entryString;
       
       entryString = "Size   :  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex)    + "\n" +
                     "Seconds:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+4)  + "\n" +
                     "Subsecs:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+8)  + "\n" +
                     "Loading:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+12) + "\n" +
                     "Create Secs:  "   + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+16) + "\n" +
                     "Create Subs:  "   + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+20) + "\n" +
                     "CRC:      "       + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+24) + "\n" +
                     "Validate Func: "  + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+28) + "\n" +
                     "Loaded Once:   "  + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+29) + "\n" +
                     "Load Pending:  "  + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+30) + "\n" +
                     "Dump Only:     "  + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+31) + "\n" +
                     "Double Buf:    "  + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+32) + "\n" +
                     "Name: " + (new String (fileByteArray, fileIndex+33, CARE.CFE_TBL_MAX_FULL_NAME_LEN)).trim() + "\n" +
                     "Last File Loaded: " + (new String (fileByteArray, fileIndex+33+CARE.CFE_TBL_MAX_FULL_NAME_LEN, CARE.CFE_OS_MAX_PATH_LEN)).trim() + "\n" +
                     "Owner App Name: " + (new String (fileByteArray, fileIndex+33+CARE.CFE_TBL_MAX_FULL_NAME_LEN+CARE.CFE_OS_MAX_PATH_LEN, CARE.CFE_OS_MAX_API_NAME)).trim() + "\n" +
                     "Critical:  "  + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+33+CARE.CFE_TBL_MAX_FULL_NAME_LEN+CARE.CFE_OS_MAX_PATH_LEN+CARE.CFE_OS_MAX_API_NAME) + "\n";

       return entryString;
       
   } // End parseTaskInfo()
   

} // End class TblRegLogFile
