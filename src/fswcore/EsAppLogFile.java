package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr; 
import app.CARE; 

/**
 * Executive Services Application File
 *  
 * @author David McComas
 *
 */
public class EsAppLogFile extends CfeFile {

   private static Logger logger=Logger.getLogger(EsAppLogFile.class);

   /** ES Application Log file entry size defined by CFE_ES_AppInfo_t */
   public static final int ENTRY_SIZE = (4*15 + 2*2 + (3*CARE.CFE_OS_MAX_API_NAME) + (CARE.CFE_OS_MAX_PATH_LEN));
   
   public EsAppLogFile () {
      
      // Nothing to do
      
   } // End EsAppLogFile()

   @Override
   public void loadUserDataStr()
   {

      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_ES_MAX_APPLICATIONS; entry++) {
         
         logger.trace("EsAppLogFile: entry " + entry);
 
         userDataStr = userDataStr + parseAppInfo(fileIndex); 
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
    *    uint32   AppId;                          /**< \cfetlmmnemonic \ES_APP_ID
    *                                                  \brief Application ID for this Application
    *    uint32   Type;                           /**< \cfetlmmnemonic \ES_APPTYPE
    *                                                  \brief The type of App: CORE or EXTERNAL
    *    
    *    uint8    Name[OS_MAX_API_NAME];          /**< \cfetlmmnemonic \ES_APPNAME
    *                                                  \brief The Registered Name of the Application
    *    uint8    EntryPoint[OS_MAX_API_NAME];    /**< \cfetlmmnemonic \ES_APPENTRYPT
    *                                                  \brief The Entry Point label for the Application
    *    uint8    FileName[OS_MAX_PATH_LEN];      /**< \cfetlmmnemonic \ES_APPFILENAME
    *                                                  \brief The Filename of the file containing the Application
    * 
    *    uint32   StackSize;                      /**< \cfetlmmnemonic \ES_STACKSIZE
    *                                                  \brief The Stack Size of the Application
    *    uint32   ModuleId;                       /**< \cfetlmmnemonic \ES_MODULEID
    *                                                  \brief The ID of the Loadable Module for the Application
    *    uint32   AddressesAreValid;              /**< \cfetlmmnemonic \ES_ADDRVALID
    *                                                  \brief Indicates that the Code, Data, and BSS addresses/sizes are valid
    *    uint32   CodeAddress;                    /**< \cfetlmmnemonic \ES_CODEADDR
    *                                                  \brief The Address of the Application Code Segment
    *    uint32   CodeSize;                       /**< \cfetlmmnemonic \ES_CODESIZE
    *                                                  \brief The Code Size of the Application
    *    uint32   DataAddress;                    /**< \cfetlmmnemonic \ES_DATAADDR
    *                                                  \brief The Address of the Application Data Segment
    *    uint32   DataSize;                       /**< \cfetlmmnemonic \ES_DATASIZE
    *                                                  \brief The Data Size of the Application
    *    uint32   BSSAddress;                     /**< \cfetlmmnemonic \ES_BSSADDR
    *                                                  \brief The Address of the Application BSS Segment
    *    uint32   BSSSize;                        /**< \cfetlmmnemonic \ES_BSSSIZE
    *                                                   \brief The BSS Size of the Application
    *    uint32   StartAddress;                   /**< \cfetlmmnemonic \ES_STARTADDR
    *                                                 \brief The Start Address of the Application
    *    uint16   ExceptionAction;                /**< \cfetlmmnemonic \ES_EXCEPTNACTN
    *                                                  \brief What should occur if Application has an exception
    *                                                  (Restart Application OR Restart Processor)
    *    uint16   Priority;                       /**< \cfetlmmnemonic \ES_PRIORITY
    *                                                  \brief The Priority of the Application
    *    uint32   MainTaskId;                     /**< \cfetlmmnemonic \ES_MAINTASKID
    *                                                  \brief The Application's Main Task ID
    *    uint32   ExecutionCounter;               /**< \cfetlmmnemonic \ES_MAINTASKEXECNT
    *                                                  \brief The Application's Main Task Execution Counter                        
    *    uint8    MainTaskName[OS_MAX_API_NAME];  /**< \cfetlmmnemonic \ES_MAINTASKNAME
    *                                                  \brief The Application's Main Task ID
    *    uint32   NumOfChildTasks;                /**< \cfetlmmnemonic \ES_CHILDTASKS
    *                                                  \brief Number of Child tasks for an App
    * 
    *    
    * } CFE_ES_AppInfo_t;
    * 
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String parseAppInfo(int fileIndex) {

      String entryString;
       
      entryString = "App Id:     "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex)   + "\n" +
                    "Type:       "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+4) + "\n" +
                    "App Name:   "  + (new String (fileByteArray, fileIndex+8, CARE.CFE_OS_MAX_API_NAME)).trim() + "\n" +
                    "Entry Pnt:  "  + (new String (fileByteArray, fileIndex+8+CARE.CFE_OS_MAX_API_NAME, CARE.CFE_OS_MAX_API_NAME)).trim()   + "\n" +
                    "File Name:  "  + (new String (fileByteArray, fileIndex+8+2*CARE.CFE_OS_MAX_API_NAME, CARE.CFE_OS_MAX_PATH_LEN)).trim() + "\n" +
                    "Stack Size: "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+ 8+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Module ID:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+12+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Addr Valid: "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+16+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Code Addr:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+20+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Code Size:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+24+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Data Addr:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+28+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Data Size:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+32+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "BSS Addr:   "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+36+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "BSS Size:   "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+40+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Start Addr: "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+44+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Except Act: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+48+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Priority:   "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+50+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Main TID:   "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+52+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Exec Cntr:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+56+2*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n" +
                    "Main Name:  "  + (new String (fileByteArray, fileIndex+60+2*CARE.CFE_OS_MAX_API_NAME, CARE.CFE_OS_MAX_API_NAME)).trim() + "\n" +
                    "Num Child:  "  + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+60+3*CARE.CFE_OS_MAX_API_NAME+CARE.CFE_OS_MAX_PATH_LEN) + "\n";

      return entryString;
       
   } // End parseAppInfo()
   
   
} // End class EsAppLogFile
