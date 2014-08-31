package fswcore;

import java.util.ArrayList; 
import java.util.Iterator;

import org.apache.log4j.Logger;

import fsw.DataPnt;
import fsw.DataPntStr;


/**
 * cFE table file. 
 * 
 * TODO - Itemize and format table header
 * 
 * @author David McComas
 */
public class CfeTblFile extends CfeFile {

   private static Logger logger=Logger.getLogger(CfeTblFile.class);

   /** Length of table header */
   public static final int TBL_HDR_LEN = 52;

   /** String index to start of application's table data */
   public static final int FILE_IDX_APP_TBL_DATA = HDR_IDX_DESCR + HDR_DESCR_LEN + TBL_HDR_LEN;

   
   fsw.Table table;
   
   public CfeTblFile (fsw.Table table) {
      
      this.table = table;
      
   } // End CfeTblFile()

   
   @Override
   protected void loadUserDataStr() {
      
      // Unsupported
      
   } // End loadUserDataStr()
   
   /**
    * 
    * TODO -  Lengths should be okay but should add boundary checks 
    * 
    */
   @Override
   protected void loadUserDataStrArray() {

      logger.trace("CfeTblFile loading user data for table with " + table.getDataPntList().size() + " data points");

      int arrayIndex = 0; 
      ArrayList<DataPnt> dataPntList = table.getDataPntList();
      Iterator<DataPnt> dataPntIt = dataPntList.iterator();
      while ( dataPntIt.hasNext() ) {
      
         DataPnt dataPnt = dataPntIt.next();
         logger.trace("loadUserDataStrArray: " + dataPnt.getName() + " = " + dataPnt.getStrObj().byteToStr(fileByteArray, (116 + dataPnt.getByteOffset())));
         logger.trace("loadUserDataStrArray: " + DataPntStr.Uint32ToStr(fileByteArray, (116 + dataPnt.getByteOffset())));
         userDataStrArray[arrayIndex] = dataPnt.getStrObj().byteToStr(fileByteArray, (116 + dataPnt.getByteOffset()));
         arrayIndex ++;
      
      } // End data point loop

   } // End loadUserDatStrarray()
   
} // End class CfeTblFile
