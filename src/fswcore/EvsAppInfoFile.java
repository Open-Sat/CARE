package fswcore;

import org.apache.log4j.Logger;

import fsw.DataPntStr;

import app.*;

/**
 * Event Service Application Info File
 *  
 * @author David McComas
 *
 */
public class EvsAppInfoFile extends CfeFile {
   
   private static Logger logger=Logger.getLogger(EvsAppInfoFile.class);
   
   /** EVS Application Information Log file entry size defined by CFE_EVS_AppDataFile_t */
   public static final int ENTRY_SIZE = CARE.CFE_OS_MAX_API_NAME + 4 + 8*CARE.CFE_EVS_MAX_EVENT_FILTERS; 

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   public static final int FILE_SIZE = (CARE.CFE_ES_MAX_APPLICATIONS * ENTRY_SIZE);

   /** ES Exception-Reset Log file entries defined by CFE_ES_ER_LOG_ENTRIES */
   private static final int STR_PER_REC = 12;


   public EvsAppInfoFile () {
      
      // Nothing to do
      
   } // End EvsAppInfoFile()
   
   @Override
   public void loadUserDataStr() {
      
      int fileIndex = FILE_IDX_APP_DATA;
      
      userDataStr = "";
      for (int entry=0; entry < CARE.CFE_ES_CDS_MAX_NUM_ENTRIES; entry++) {
         
         logger.trace("EvsAppLogFile: entry " + entry);
 
         String eStr[] = new String[STR_PER_REC];
         eStr = parseAppDataRec(fileIndex);
         userDataStr = userDataStr + 
                   eStr[0] + eStr[1] + eStr[2] + eStr[3] + eStr[4]  + eStr[5] +
                   eStr[6] + eStr[7] + eStr[8] + eStr[9] + eStr[10] + eStr[11];
        
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
    * typedef struct {
    *    char                   AppName[OS_MAX_API_NAME];          /* Application name
    *    uint8                  ActiveFlag;                        /* Application event service active flag
    *    uint8                  EventTypesActiveFlag;              /* Application event types active flag
    *    uint16                 EventCount;                        /* Application event counter
    *    CFE_EVS_EVSBinFilter_t Filters[CFE_EVS_MAX_EVENT_FILTERS];/* Application event filters
    * 
    * } CFE_EVS_AppDataFile_t;
    *
    * typedef struct {
    *   int16  EventID;   /* Numerical event identifier
    *   uint16 Mask;      /* Binary filter mask
    *   uint16 Count;     /* Binary filter counter
    *   uint16 Padding;   /* Structure padding
    *
    * } CFE_EVS_EVSBinFilter_t;
    *
    * @param  fileIndex Index into fileByteArray to the start of the entry 
    */
   protected String[] parseAppDataRec(int fileIndex) {

       String[] entryStringArray = new String[STR_PER_REC];
       
       entryStringArray[0] = "Name: "            + (new String (fileByteArray, fileIndex, CARE.CFE_OS_MAX_API_NAME)).trim()   + "\n"; 
       entryStringArray[1] = "Active: "          + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME)    + "\n"; 
       entryStringArray[2] = "Types Active: "    + DataPntStr.Uint8ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+1)  + "\n"; 
       entryStringArray[3] = "Event Count: "     + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+2) + "\n"; 
       entryStringArray[4] = "Filters[0] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+4) + "\n" + 
                             "Filters[0] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+6) + "\n" + 
                             "Filters[0] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+8) + "\n";
       
       entryStringArray[5] = "Filters[1] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+12) + "\n" + 
                             "Filters[1] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+14) + "\n" + 
                             "Filters[1] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+16) + "\n";
       
       entryStringArray[6] = "Filters[2] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+20) + "\n" + 
                             "Filters[2] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+22) + "\n" + 
                             "Filters[2] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+24) + "\n";
       
       entryStringArray[7] = "Filters[3] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+28) + "\n" + 
                             "Filters[3] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+30) + "\n" + 
                             "Filters[3] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+32) + "\n";
       
       entryStringArray[8] = "Filters[4] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+36) + "\n" + 
                             "Filters[4] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+38) + "\n" + 
                             "Filters[4] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+40) + "\n";
       
       entryStringArray[9] = "Filters[5] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+44) + "\n" + 
                             "Filters[5] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+46) + "\n" + 
                             "Filters[5] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+48) + "\n";
       
       entryStringArray[10] = "Filters[6] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+52) + "\n" + 
                              "Filters[6] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+54) + "\n" + 
                              "Filters[6] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+56) + "\n";
       
       entryStringArray[11] = "Filters[7] - ID: "    + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+60) + "\n" + 
                              "Filters[7] - Mask: "  + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+62) + "\n" + 
                              "Filters[7] - Count: " + DataPntStr.Uint16ToStr(fileByteArray, fileIndex+CARE.CFE_OS_MAX_API_NAME+64) + "\n";
          
       return entryStringArray;
       
   } // End parseAppDataRec()
   

} // End class EvsAppInfoFile
