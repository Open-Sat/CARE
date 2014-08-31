package fsw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ccsds.CcsdsTlmPkt;

/**
 * Provide a base class for all raw data to string conversions.
 *
 * TODO - Resolve byte flip issue either here or in network classes.
 *
 * @author David McComas
 */
public abstract class DataPntStr {

   static final public int IDX_MSG_ID   = 0;
   static final public int IDX_SEQ_CNT  = 1;
   static final public int IDX_LEN      = 2;
   static final public int IDX_SEC      = 3;
   static final public int IDX_SUBSEC   = 4;
   static final public int HDR_LEN      = 5;
   
   public DataPntStr () {
      
   } // End DataPntstr()
   
   /**
    * 
    * @param rawData
    * @param rawIndex
    * @return
    */
   public abstract String byteToStr(byte[] rawData, int rawIndex);

   static final public void hdrBytesToStr(String[] tlmStrArray, CcsdsTlmPkt TlmMsg) {
      
      tlmStrArray[0] = String.valueOf((TlmMsg.getStreamId()));
      tlmStrArray[IDX_SEQ_CNT] = String.valueOf((TlmMsg.getSeqCount()));
      tlmStrArray[2] = String.valueOf((TlmMsg.getLength()));
      tlmStrArray[3] = "Seconds";    // TODO - Seconds
      tlmStrArray[4] = "SubSeconds"; // TODO - SubSeconds

   } // End hdrBytesToStr()

   /***************************************************************************
    **
    ** Helper methods that provide generic TLM-to-String parsing
    **
    ** TODO - Make length variable with better line formats
    */
    
    static public String ParseRawData(byte[] RawData)
    {
       
       String Message = new String();

       Message = "\n---------------------------------\nPacket: App ID = 0x";
       Message += ByteToHexStr(RawData[1])+ ByteToHexStr(RawData[0]) + "\n";
       //return ( (( (Packet[CCSDS_IDX_STREAM_ID] & 0x00FF) | (Packet[CCSDS_IDX_STREAM_ID+1] << 8)) & CCSDS_MSK_MSG_ID) );

       //Truncate to 16 bytes since it's just a warm fuzzy
       int DataLen = 16;
       if (RawData.length < 16) DataLen = RawData.length;

       for (int i=0; i < DataLen; i++)
       {
          Message += ByteToHexStr(RawData[i]) + " ";
       }

       return Message;

    } // End ParseRawData()

    /*
    ** There's probably an easier way but I was having problems with sign extension
    ** and not knowing Java well I just hacked a solution.
    */
    
    static public String ByteToHexStr(byte Byte)
    {

       String HexStr = Integer.toHexString((short)Byte&0x00FF); // Need to mask to prevent long strings

       if (HexStr.length() == 1)
          HexStr = "0" + HexStr;

       else if (HexStr.length() > 2)
          HexStr = "**";
       
       return HexStr.toUpperCase();

    } // End ByteToHexStr()

    /**
     * Convert unsigned 8-bit data to a string.
     *  
     * @param rawData
     * @param rawIndex
     * @return
     */
    static public String Uint8ToStr(byte[] rawData, int rawIndex) {

       return String.valueOf( (0x00FF & rawData[rawIndex])); // I think this solves unsigned  

    } // End Uint8ToStr()


    /**
     * Convert unsigned 16-bit data to a string.
     *  
     * @param rawData
     * @param rawIndex
     * @return
     */
    static public String Uint16ToStr(byte[] rawData, int rawIndex) {

       int  intResult;   // 32 bit signed integer
       
       // Must mask the low bits because it is promoted to an int and will sign extend. Tried short but no luck
       if (true) {
          intResult = ((rawData[rawIndex] & 0x00FF) | (int)(rawData[rawIndex+1] << 8)); 
       }
       else {
          intResult = (rawData[rawIndex+1]|(( (rawData[rawIndex] & 0x00FF) << 8)));
       }

       return Integer.toString(intResult); 
       
    } // End Uint16ToStr()

    /**
     * Convert unsigned 32-bit data to a string. Java doesn't have unsigned 
     * integers. Use 'long' that should be 64 bits and avoid sign bit
     *
     * @param rawData
     * @param rawIndex
     * @return
     */
    static public String Uint32ToStr(byte[] rawData, int rawIndex)
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
       return Integer.toHexString(intResult);  

    } // End Uint32ToStr ()


} // End class DataPntStr

