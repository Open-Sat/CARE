package fswcore;

import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import fsw.DataPntStr;
import fsw.FswApp;


/**
 * Base class to represent/model a cFE file. 
 * 
 * <p>Implementation Notes:
 * <ul>
 * <li>This code is compatible with pre-Java 7 SDK</li>
 * <li>See cfe_fs.h for cFE file header definitions</li>
 * <li>Assumes files will be short. In KBs not MBs</li>
 * </ul>
 * 
 * @author David McComas
 */
public abstract class CfeFile {
   
   private static Logger logger=Logger.getLogger(CfeFile.class);

   /**  Count of cFE file types */
   public static final int TYPE_CNT = 11;
   public static final String[] TYPE_STR = {
      "ES  - System Log", "ES  - Exception Log", "ES  - Critical", "ES  - All Apps", "ES  - Tasks", 
      "EVS - App Info",   "EVS - EVS Log", 
      "SB  - Routing",    "SB  - Pipes",         "SB  - Mapping",
      "TBL - Registry"
   };
   
   /** Byte array index to header 'Content Type' char[4] field */
   public static final int HDR_IDX_TYPE     = 0;

   /** Byte array index to header 'Subtype' unit32 field */
   public static final int HDR_IDX_SUBTYPE  = 4;

   /** Byte array index to header 'Length' unit32 field */
   public static final int HDR_IDX_LENGTH   = 8;

   /** Byte array index to header 'Spacecraft ID' unit32 field */
   public static final int HDR_IDX_SC_ID    = 12;

   /** Byte array index to header 'Processor ID' unit32 field */
   public static final int HDR_IDX_PROC_ID  = 16;

   /** Byte array index to header 'Application ID' unit32 field */
   public static final int HDR_IDX_APP_ID   = 20;

   /** Byte array index to header 'Seconds' unit32 field */
   public static final int HDR_IDX_SEC      = 24;

   /** Byte array index to header 'Subseconds' unit32 field */
   public static final int HDR_IDX_SUBSEC   = 28;

   /** Byte array index to header 'Description' char [32] field */
   public static final int HDR_IDX_DESCR    = 32;
   public static final int HDR_DESCR_LEN    = 32;

   /** Number of header fields */
   public static final int HDR_NUM_FIELDS = 9;

   /** String index to start of application's data */
   public static final int FILE_IDX_APP_DATA = HDR_IDX_DESCR + HDR_DESCR_LEN;

   /** String index to header 'Content Type' char[4] field */
   public static final int STR_IDX_TYPE     = 0;

   /** String index to header 'Subtype' unit32 field */
   public static final int STR_IDX_SUBTYPE  = 1;

   /** String index to header 'Length' unit32 field */
   public static final int STR_IDX_LENGTH   = 2;

   /** String index to header 'Spacecraft ID' unit32 field */
   public static final int STR_IDX_SC_ID    = 3;

   /** String index to header 'Processor ID' unit32 field */
   public static final int STR_IDX_PROC_ID  = 4;

   /** String index to header 'Application ID' unit32 field */
   public static final int STR_IDX_APP_ID   = 5;

   /** String index to header 'Seconds' unit32 field */
   public static final int STR_IDX_SEC      = 6;

   /** String index to header 'Subseconds' unit32 field */
   public static final int STR_IDX_SUBSEC   = 7;

   /** String index to header 'Description' char [32] field */
   public static final int STR_IDX_DESCR    = 8;
   
   /** One string for each header field makes it easy for GUI code to load page fields */
   protected String[] hdrValStrArray;
   
   static public String[] hdrLblStrArray = {"File Type", "File Subtype", "Length", "Spacecraft ID", "Processor ID", "Applicaiton ID",
                                            "Seconds", "Subseconcds", "Decsription"};
   
   /** Store file contents */
   protected byte[] fileByteArray; 

   /** String containing user data suitable for display in a text box */
   protected String userDataStr;

   /** Array of strings with each entry containing a string representing a table data point value. */
   protected String[]  userDataStrArray;
   
   
   /**
    * Construct a file with a cFE file header.
    * 
    * @param fileName  Full path and filename of the FSW file
    */
   protected CfeFile () {
 
      fileByteArray = null;
      userDataStr   = null;
      
      hdrValStrArray   = new String[HDR_NUM_FIELDS];
      userDataStrArray = new String[FswApp.TLM_MAX_POINTS];
      
   } // End CfeFile()
   
   /**
    * Format header and user data strings suitable for display.
    * 
    * @param fileName  Full path and filename of the FSW file
    */
   public boolean formatDataStrings (String fileName) {
         
	  boolean RetStatus = false;
	  
      fileByteArray = readFile(fileName);
      
      if (fileByteArray != null) {
      
         loadHdrStr();
         loadUserDataStr();
         loadUserDataStrArray();
         RetStatus = true;
         
      } // End if fileByteArray != null
      
      return RetStatus;
      
   } // End FormatData()

   /**
    * Load the header string array. The fileByteArray must be loaded
    * prior to calling this method.
    *  
    */
   protected void loadHdrStrOld() {
   
      hdrValStrArray[STR_IDX_TYPE]    = new String(fileByteArray, HDR_IDX_TYPE, 4);
      hdrValStrArray[STR_IDX_SUBTYPE] = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_SUBTYPE);
      logger.trace("cFE File: fileByteArray[Subtype] = " +
            Integer.toString(fileByteArray[HDR_IDX_SUBTYPE]) + "," +
            Integer.toString(fileByteArray[HDR_IDX_SUBTYPE+1]) + "," +
            Integer.toString(fileByteArray[HDR_IDX_SUBTYPE+2]) + "," +
            Integer.toString(fileByteArray[HDR_IDX_SUBTYPE+3]));
      //TODO - Use native to process file data. ByteBuffer? hdrValStrArray[STR_IDX_LENGTH]  = Integernew String (fileByteArray, HDR_IDX_LENGTH, 4);
      hdrValStrArray[STR_IDX_LENGTH]  = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_LENGTH);
      hdrValStrArray[STR_IDX_SC_ID]   = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_SC_ID);
      hdrValStrArray[STR_IDX_PROC_ID] = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_PROC_ID);
      hdrValStrArray[STR_IDX_APP_ID]  = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_APP_ID);
      hdrValStrArray[STR_IDX_SEC]     = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_SEC);
      hdrValStrArray[STR_IDX_SUBSEC]  = DataPntStr.Uint32ToStr(fileByteArray, HDR_IDX_SUBSEC);
      hdrValStrArray[STR_IDX_DESCR]   = new String(fileByteArray, HDR_IDX_DESCR, HDR_DESCR_LEN);
      
   } // End processFileHeader()
   
   /**
    * Load the header string array. The fileByteArray must be loaded
    * prior to calling this method.
    *  
    */
   protected void loadHdrStr() {
   
      ByteBuffer byteBuffer = getByteBuffer();
      
      hdrValStrArray[STR_IDX_TYPE]    = new String(fileByteArray, HDR_IDX_TYPE, 4);
      hdrValStrArray[STR_IDX_SUBTYPE] = Integer.toString(byteBuffer.getInt(HDR_IDX_SUBTYPE));
      hdrValStrArray[STR_IDX_LENGTH]  = Integer.toString(byteBuffer.getInt(HDR_IDX_LENGTH));
      hdrValStrArray[STR_IDX_SC_ID]   = Integer.toString(byteBuffer.getInt(HDR_IDX_SC_ID));
      hdrValStrArray[STR_IDX_PROC_ID] = Integer.toString(byteBuffer.getInt(HDR_IDX_PROC_ID));
      hdrValStrArray[STR_IDX_APP_ID]  = Integer.toString(byteBuffer.getInt(HDR_IDX_APP_ID));
      hdrValStrArray[STR_IDX_SEC]     = Integer.toString(byteBuffer.getInt(HDR_IDX_SEC));
      hdrValStrArray[STR_IDX_SUBSEC]  = Integer.toString(byteBuffer.getInt(HDR_IDX_SUBSEC));
      hdrValStrArray[STR_IDX_DESCR]   = new String(fileByteArray, HDR_IDX_DESCR, HDR_DESCR_LEN);
      
   } // End processFileHeader()

   /**
    * This method is intended to simplify the needs of a GUI. Each derived file type
    * defines this function to create a string that is suitable for a text block display.
    * 
    * fileByteArray must be loaded prior to calling this method.
    *
    */
   protected abstract void loadUserDataStr();
   
   /**
    * This method is intended to simplify the needs of a GUI. Each derived file
    * type defines this function to create a string array that is suitable for 
    * a GUI <name,value> pair display.
    *
    * fileByteArray must be loaded prior to calling this method.
    *
    */
   protected abstract void loadUserDataStrArray();
   
   /**
    * Returns the fileByteArray
    * 
    * @return byte array containing cFE file data
    */
   public byte[] getFileByteArray() {
         
      return fileByteArray;
      
   } // End getFileByteArray()
   
   /**
    * Return a ByteBuffer containing the file data
    * 
    * @return
    */
   public ByteBuffer getByteBuffer() {
      final ByteBuffer byteBuf = ByteBuffer.wrap(new byte[fileByteArray.length]);

      byteBuf.put(fileByteArray);
      byteBuf.flip();
   
      return byteBuf;
      
  } // getByteBuffer()

   public String[] getHdrStrArray() {
      
      return hdrValStrArray;
      
   } // End getHdrStrArray()
   
   public String getUserDataStr() {
      
      return userDataStr;
      
   } // End getUserDataStr()

   public String[] getUserDataStrArray() {
      
      return userDataStrArray;
      
   } // End getUserDataStrArray()

   /**
    *  Read the given binary file and return its contents as a byte array.
    *  
    *  @param  Byte array containing file data
    */ 
   protected byte[] readFile(String fileName){

      byte[] result = null;

      File file = new File(fileName);
      try {
         InputStream input =  new BufferedInputStream(new FileInputStream(file));
         result = readAndCloseStream(input);
      }
      catch (FileNotFoundException ex){
         logger.error("cFE File: readFile() exception = " + ex);
      }
      
      return result;
      
   } // End readFile()
   
   /**
    * Read an input stream and return it as a byte array.  This implementation
    * works whether the source is a file or an input stream. The file is closed
    * after it is read.
   */
   protected byte[] readAndCloseStream(InputStream input){
      
      //carries the data from input to output :    
      byte[] bucket = new byte[32*1024]; 
      ByteArrayOutputStream result = null; 
      try  {
         try {
            //Use buffering? No. Buffering avoids costly access to disk or network;
            //buffering to an in-memory stream makes no sense.
            result = new ByteArrayOutputStream(bucket.length);
            int bytesRead = 0;
            while(bytesRead != -1){
              bytesRead = input.read(bucket); // read() returns -1, 0, or more
              if(bytesRead > 0){
                 result.write(bucket, 0, bytesRead);
              }
            } // End while loop
         }
         finally {
            input.close();  //result.close(); this is a no-operation for ByteArrayOutputStream
         }
      }
      catch (IOException ex){
         logger.error("cFE File: readAndCloseStream() exception = " + ex);
      }
      
      return result.toByteArray();
   
   } // End readAndCloseStream()

} // End class CfeFile
