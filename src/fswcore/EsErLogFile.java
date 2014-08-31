package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr;  

import app.*;

/**
 * Executive Services Exception-Reset Log File
 *  
 *  TODO - Fix array index exceeded on 13th entry. Also decided on format.
 *  
 * @author David McComas
 *
 */
public class EsErLogFile extends CfeFile {
 
   private static Logger logger=Logger.getLogger(EsErLogFile.class);

   /** ES Exception-Reset Log file entry size defined by CFE_ES_ERLog_t */
   public static final int ENTRY_SIZE = 1160; // TODO - Use config parameters to compute the size

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   public static final int FILE_SIZE = (CARE.CFE_ES_ER_LOG_ENTRIES * ENTRY_SIZE);

   public EsErLogFile () {
      
      // Nothing to do
      
   } // End EsErLogFile()
   

   @Override
   public void loadUserDataStr() {
      
      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_ES_ER_LOG_ENTRIES; entry++) {
         
         logger.trace("EsErLogFile: entry " + entry);
 
         String eStr[] = new String[16];   // 16 strings per entry
         eStr = parseLogEntry(fileIndex);
         userDataStr = userDataStr + 
                       eStr[0]  + eStr[1]  + eStr[2]  + eStr[3]  +
                       eStr[4]  + eStr[5]  + eStr[6]  + eStr[7]  +
                       eStr[8]  + eStr[9]  + eStr[10] + eStr[11] +
                       eStr[12] + eStr[13] + eStr[14] + eStr[15];
        
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
    * typedef struct
    * {
    *    uint32                  LogEntryType;                      /* What type of log entry
    *    uint32                  ResetType;                         /* Main cause for the reset
    *    uint32                  ResetSubtype;                      /* The sub-type for the reset
    *    uint32                  BootSource;                        /* The boot source
    *    uint32                  ProcessorResetCount;               /* The number of processor resets
    *    uint32                  MaxProcessorResetCount;            /* The maximum number before a Power On
    *    CFE_ES_DebugVariables_t DebugVars;                         /* ES Debug variables   
    *       typedef struct
    *       {  
    *          uint32 DebugFlag;
    *          uint32 WatchdogWriteFlag;
    *          uint32 PrintfEnabledFlag;
    *          uint32 LastAppId;
    *       } CFE_ES_DebugVariables_t;
    *    CFE_TIME_SysTime_t      TimeCode;                          /* Time code
    *       uint32 Second
    *       uint32 Subseconds
    *    char                    Description[80];                   /* The ascii data for the event
    *    uint32                  ContextIsPresent;                  /* Indicates the context data is valid
    *    uint32                  AppID;                             /* The application ID
    *    uint32                  Context[CFE_PSP_CPU_CONTEXT_SIZE]; /* cpu  context
    *        uint32 x 32: Depends on PSP 
    *  
    * } CFE_ES_ERLog_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String[] parseLogEntry(int fileIndex) {

       String[] entryStringArray = new String[16];
       
       entryStringArray[0]  = "Log Entry Type: "             + DataPntStr.Uint32ToStr(fileByteArray, fileIndex)    + "\n"; 
       entryStringArray[1]  = "Reset Type: "                 + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+4)  + "\n"; 
       entryStringArray[2]  = "Reset Subtype: "              + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+8)  + "\n"; 
       entryStringArray[3]  = "Boot Source: "                + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+12) + "\n"; 
       entryStringArray[4]  = "Processor Reset Count:  "     + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+16) + "\n"; 
       entryStringArray[5]  = "Max Processor Reset Count:  " + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+20) + "\n"; 
       
       // Debug Variables
       entryStringArray[6]  = "Debug Flag: "          + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+24) + "\n"; 
       entryStringArray[7]  = "Watchdog Write Flag: " + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+28) + "\n"; 
       entryStringArray[8]  = "Printf Enabled Flag: " + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+32) + "\n"; 
       entryStringArray[9]  = "Last App ID: "         + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+36) + "\n"; 
       
       // cFE Time
       entryStringArray[10] = "cFE Time Seconds: "    + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+40) + "\n"; 
       entryStringArray[11] = "cFE Time Subseconds: " + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+44) + "\n"; 
       
       entryStringArray[12] = "Event Decsription: " + (new String (fileByteArray, fileIndex+48, 80)).trim() + "\n"; 
       
       entryStringArray[13] = "Context Is Present: " + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+128) + "\n"; 
       entryStringArray[14] = "App ID: "             + DataPntStr.Uint32ToStr(fileByteArray, fileIndex+132) + "\n"; 
       entryStringArray[15] = "PSP Context Registers not parsed\n";

          
       return entryStringArray;
       
   } // End parseLogEntry()
   
} // End class EsErLogFile
