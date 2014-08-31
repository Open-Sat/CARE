package fsw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ccsds.CcsdsTlmPkt;

/**
 * Provide a base class for all raw data to integer conversions.
 *
 * TODO - Resolve byte flip issue either here or in network classes.
 *
 * @author David McComas
 */
public class DataPntInt {

   static final public int IDX_MSG_ID   = 0;
   static final public int IDX_SEQ_CNT  = 1;
   static final public int IDX_LEN      = 2;
   static final public int IDX_SEC      = 3;
   static final public int IDX_SUBSEC   = 4;
   static final public int HDR_LEN      = 5;
   
   public DataPntInt () {
      
   } // End DataPntInt()
   
   /**
    * Convert unsigned 16-bit data to a string.
    *  
    * @param rawData
    * @param rawIndex
    * @return
    */
   static public int Uint16ToInt(byte[] rawData, int rawIndex) {

      int  intResult;   // 32 bit signed integer
      
      // Must mask the low bits because it is promoted to an int and will sign extend. Tried short but no luck
      if (true) {
         intResult = ((rawData[rawIndex] & 0x00FF) | (int)(rawData[rawIndex+1] << 8)); 
      }
      else {
         intResult = (rawData[rawIndex+1]|(( (rawData[rawIndex] & 0x00FF) << 8)));
      }

      return (intResult & 0x0000FFFF); 
      
   } // End Uint16ToInt()


    /**
     * Convert unsigned 32-bit data to an int. Java doesn't have unsigned 
     * integers. 
     * 
     * @param rawData
     * @param rawIndex
     * @return
     */
    static public int Uint32ToInt(byte[] rawData, int rawIndex)
    {
       int  intA,intB;
       int  intResult;   // 32 bit signed integer

       if (true) {
          intA = ((rawData[rawIndex]   & 0x0000FFFF) | (int)(rawData[rawIndex+1] << 8)); 
          intB = ((rawData[rawIndex+2] & 0x0000FFFF) | (int)(rawData[rawIndex+3] << 8)); 
          intResult = ((intA & 0x0000FFFF) | (intB << 16));
       }
       else {
          // TBD
          intA = ((rawData[rawIndex]   & 0x00FF) | (int)(rawData[rawIndex+1] << 8)); 
          intB = ((rawData[rawIndex+2] & 0x00FF) | (int)(rawData[rawIndex+3] << 8)); 
          intResult = ((intB & 0x0000FFFF) | ((intA << 16)));
       }

       //return Integer.toString(intResult);  
       return intResult;  

    } // End Uint32ToInt ()


} // End class DataPntInt

