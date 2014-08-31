package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr;  
import app.CARE;

/**
 * Executive Service Task Info File
 *  
 * @author David McComas
 *
 */
public class EsTaskLogFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(EsTaskLogFile.class);

   /** ES Application Log file entry size defined by CFE_ES_TaskInfo_t */
   public static final int ENTRY_SIZE = 4*3 + (2 * CARE.CFE_OS_MAX_API_NAME);
   
   public EsTaskLogFile () {
      
      // Nothing to do
      
   } // End EsTaskLogFile()

   @Override
   public void loadUserDataStr()
   {

      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_OS_MAX_TASKS; entry++) {
         
         logger.trace("EsAppLogFile: entry " + entry);
 
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
    *    typedef struct
    *    {
    *       uint32   TaskId;                    /**< \brief Task Id
    *       uint32   ExecutionCounter;          /**K \brief Task Execution Counter
    *       uint8    TaskName[OS_MAX_API_NAME]; /**< \brief Task Name
    *       uint32   AppId;                     /**< \brief Parent Application ID
    *       uint8    AppName[OS_MAX_API_NAME];  /**< \brief Parent Application Name
    * 
    *    } CFE_ES_TaskInfo_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String parseTaskInfo(int fileIndex) {

       String entryString;
       
       entryString = "Task Id:   "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex) + "\n" +
                     "Excpt Cnt: "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+4) + "\n" +
                     "Task Name: "  + (new String (fileByteArray, fileIndex+8, CARE.CFE_OS_MAX_API_NAME)).trim() + "\n" +
                     "App Id:    "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+8+CARE.CFE_OS_MAX_API_NAME) + "\n" +
                     "App Name:  "  + (new String (fileByteArray, fileIndex+12+CARE.CFE_OS_MAX_API_NAME, CARE.CFE_OS_MAX_API_NAME)).trim() + "\n";

       return entryString;
       
   } // End parseTaskInfo()
   

} // End class EsTaskLogFile
