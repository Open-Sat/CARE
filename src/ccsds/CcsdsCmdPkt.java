package ccsds;

import org.apache.log4j.Logger; 


/**
 *  Defines a CCSDS command packet.
 *  
 *  Secondary Header definition:
 *
 *  bits  shift   ------------ description ----------------
 *  0x00FF    0  : checksum, calculated by ground system     
 *  0x7F00    8  : command function code                     
 *  0x8000   15  : reserved, set to 0                        
 *
 * @author David McComas
 *
 */
public class CcsdsCmdPkt extends CcsdsPkt {

   private static Logger logger=Logger.getLogger(CcsdsCmdPkt.class);
   
   /** Byte array index of start of command secondary header */
   static final public int CCSDS_IDX_CMD_HDR   = 6;

   /** Byte array index of start of command data */
   static final public int CCSDS_IDX_CMD_DATA  = 8;

   /** Length in bytes of the command header */
   static final public int CCSDS_CMD_HDR_LEN   = 8;

   /**
    * Constructor that allocates storage for the packet. The packet is zero
    * filled except for the length in the header. 
    * 
    * @param length total number of bytes in the packet including the header 
    */
   public CcsdsCmdPkt(int length) {
      
      this(0, length);
      
   } // End CcsdsCmdPkt()
   

   /**
    * Constructor that allocates storage for the packet. The packet is zero
    * filled except for the Stream ID and length in the header. 
    * 
    * @param steamId   CCSDS Stream ID  
    * @param length    total number of bytes in the packet including the header 
    */
   public CcsdsCmdPkt(int streamId, int length) {
      
        super(streamId, length);
      
   } // End CcsdsCmdPkt()

   
   /**
    * Constructor that allocates storage for the packet. The packet is zero
    * filled except for the Stream ID and length in the header. 
    * 
    * @param steamId   CCSDS Stream ID  
    * @param length    total number of bytes in the packet including the header
    * @param funcCode  command function code
    */
   public CcsdsCmdPkt(int streamId, int length, int funcCode) {
      
        super(streamId, length);
        InitSecHdr(funcCode);
        
   } // End CcsdsCmdPkt()

   /**
    * Initialize the command packet secondary header.
    * 
    * @param funcCode  command function code
    */
   public void InitSecHdr(int funcCode) {

        byteArray[CCSDS_IDX_CMD_HDR]   = 0;
        byteArray[CCSDS_IDX_CMD_HDR+1] = new Integer(funcCode&0x7F).byteValue();

   } // End InitSecHdr()
   
   /**
    * Returns the command function code.
    * 
    * @return command function code
    */
   public int getFuncCode() {

      return (byteArray[CCSDS_IDX_CMD_HDR+1] & 0x7F);

   } // End getFuncCode()

   /**
    * Return the byteArray's checksum.
    *  
    * @return byteArray checksum
    */
   public int getChecksum()
   {
      return (byteArray[CCSDS_IDX_CMD_HDR] & 0xFF);

   } // End getChecksum()

   /**
    * Compute the packet checksum using a byte-wise exclusive-or.
    * 
    * @return checksum byte
    */
   public byte ComputeChecksum() {

      int  pktLen   = getTotalLength();
      byte checksum = new Integer(0xFF).byteValue();

      byteArray[CCSDS_IDX_CMD_HDR] = 0;
      
      for (int i=0; i < pktLen; i++) {
         checksum ^= byteArray[i];
      }
      
      byteArray[CCSDS_IDX_CMD_HDR] = new Integer((checksum & 0xFF)).byteValue();
      
      logger.trace("Checksum = " + Integer.toHexString(checksum));
      
       return checksum;

   } // End ComputeChecksum()

   /**
    * Load the data portion of the packet and compute the packet checksum.
    * TODO - Add length protection.
    * 
    * @param data        byte array containing data
    * @param dataLength  length in bytes of the data
    */
   public void loadData(byte data[], int dataLength) {
         
      for (int i=0; i < dataLength; i++)
         byteArray[CCSDS_IDX_CMD_DATA+i] = data[i];
      
      ComputeChecksum();
      
   } // End loadData()
   
  /**
   * Unit test CcsdsCmdPkt class.
   * 
   * TODO - Replace with Junit.
   * 
   * @param args
   * @throws Exception
   */
   public static void main(String [] args) throws Exception
   {
    
      boolean BuffersMatch = true;
      Integer Temp, i;
      byte[] DataBuffer = new byte[16];
      byte[] ByteCmdPkt1 = new byte[24]; 
      byte[] ByteCmdPkt2; 
      CcsdsCmdPkt CmdPkt = new CcsdsCmdPkt(0x1880, 24, 6);
      CcsdsCmdPkt ResetCmdPkt = new CcsdsCmdPkt(0x6318, 8, 0);
      CcsdsCmdPkt NoopCmdPkt = new CcsdsCmdPkt(0x6318, 8, 1);
      
      Temp = 0x80;
      ByteCmdPkt1[0] = Temp.byteValue();
      ByteCmdPkt1[1] = 0x18;
      ByteCmdPkt1[2] = 0x00;
      Temp = 0xC0;
      ByteCmdPkt1[3] = Temp.byteValue();
      ByteCmdPkt1[4] = 0x11;
      ByteCmdPkt1[5] = 0x00;
      Temp = 0xE3;  // This is not correct!!
      ByteCmdPkt1[6] = Temp.byteValue();
      ByteCmdPkt1[7] = 0x06;

      ByteCmdPkt1[8] = 0x31;
      ByteCmdPkt1[9] = 0x32;
      ByteCmdPkt1[10] = 0x37;
      ByteCmdPkt1[11] = 0x2E;
      ByteCmdPkt1[12] = 0x30;
      ByteCmdPkt1[13] = 0x30;
      ByteCmdPkt1[14] = 0x30;
      ByteCmdPkt1[15] = 0x2E;
      ByteCmdPkt1[16] = 0x30;
      ByteCmdPkt1[17] = 0x2E;
      ByteCmdPkt1[18] = 0x30;
      ByteCmdPkt1[19] = 0x31;
      ByteCmdPkt1[20] = 0x00;
      ByteCmdPkt1[21] = 0x00;
      ByteCmdPkt1[22] = 0x00;
      ByteCmdPkt1[23] = 0x00;
      
      for (i=0; i < 16; i++)
         DataBuffer[i] = ByteCmdPkt1[CCSDS_IDX_CMD_DATA+i];
     
      CmdPkt.loadData(DataBuffer, 16);
     
      ByteCmdPkt2 = CmdPkt.getByteArray();
      
      for (i=0; i < 24; i++)
      {
         if (ByteCmdPkt1[i] != ByteCmdPkt2[i])
         {
            BuffersMatch = false;
            logger.debug("Buffers miscompare at index " + i +
                         " DataBuffer1 = " + Integer.toHexString(ByteCmdPkt1[i]) +
                         " DataBuffer2 = " + Integer.toHexString(ByteCmdPkt2[i]) );
         }
      }
      
      if (BuffersMatch)
         logger.debug("Buffers Match");

      NoopCmdPkt.ComputeChecksum();
      ByteCmdPkt2 = NoopCmdPkt.getByteArray();
      for (i=0; i <8; i++)
         logger.debug("i: " + i + " " + Integer.toHexString(ByteCmdPkt2[i]));
        
      ResetCmdPkt.ComputeChecksum();
      ByteCmdPkt2 = ResetCmdPkt.getByteArray();
      for (i=0; i < 8; i++)
         logger.debug("i: " + i + " " + Integer.toHexString(ByteCmdPkt2[i]));

   } // End main()

} // End class CcsdsCmdPkt
