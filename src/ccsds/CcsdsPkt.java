package ccsds;

import java.nio.*;

/**
 * Base class for implementing CCSDS command and telemetry packets. It provides
 * constants, types, and methods that are common to all packets. The packet is
 * represented by  
 * 
 * @author David McComas
 *
 */
public class CcsdsPkt 
{

   /** Byte array index of Stream ID*/
   static final public int CCSDS_IDX_STREAM_ID  = 0;
   
   /** Byte array index of packet sequence count */
   static final public int CCSDS_IDX_SEQ_COUNT  = 2;    
   
   /** Byte array index of packet length */
   static final public int CCSDS_IDX_LENGTH     = 4;    
   
   /** Length in bytes of the primary header */
   static final public int CCSDS_PRI_HDR_LENGTH = 6;    
   
   /** Bit mask to retrieve CFS message ID */
   static final public int CCSDS_MSK_MSG_ID  = 0x0000FFFF;
   
   /** Bit mask to retrieve sequence count field */
   static final public int CCSDS_MSK_SEQ_CNT = 0x00003FFF;

   /** CCSDS standard length computation adjustment */
   static final protected int CCSDS_LENGTH_ADJUST = 7;  

   /** Byte array representation of CCSDS packet */
   protected byte[] byteArray; 
   
   /**
    * Constructor that allocates storage for the packet. The packet is zero
    * filled except for the length in the header. 
    * 
    * @param length total number of bytes in the packet including the header 
    */
   public CcsdsPkt(int length) {
      
      this(0,length);
      
   } // End CcsdsPkt()
   
   /**
    * Constructor that allocates storage for the packet. The packet is zero
    * filled except for the Stream ID and length in the header. 
    * 
    * @param steamId   CCSDS Stream ID  
    * @param length    total number of bytes in the packet including the header 
    */
   public CcsdsPkt(int streamId, int length) {
      
      byteArray = new byte[length];
      InitPkt(streamId, length);
      
   } // End CcsdsPkt()

   /**
    * Zero fill a packet except for the Stream ID and length in the header.
    * 
    * @param steamId   CCSDS Stream ID  
    * @param length    total number of bytes in the packet including the header 
    */
   public void InitPkt(int streamId, int length) {

      for (int i=0; i < length; i++) {
         byteArray[i] = 0;
      }
       
      setStreamId(streamId);
      byteArray[CCSDS_IDX_SEQ_COUNT+1] = new Integer(0xC0).byteValue();
      byteArray[CCSDS_IDX_LENGTH]      = new Integer((length-CCSDS_LENGTH_ADJUST)&0xFF).byteValue();
      byteArray[CCSDS_IDX_LENGTH+1]    = new Integer(((length-CCSDS_LENGTH_ADJUST)&0xFF00)>>8).byteValue();
      
   } // End InitPkt()

   /**
    * Construct a packet using the supplied data.  
    * 
    * @param msgData byte array containing 
    */
   public CcsdsPkt(byte[] msgData) {

      byteArray = msgData;
            
   } // End CcsdsPkt()

   /**
    * Returns the total byte length of the packet and not the CCSDS defined length.
    * 
    * @return total byte length of the packet.
    */
   public int getTotalLength() {
      
	  // Mask the lower bits because it will signed extend
      return ( (byteArray[CCSDS_IDX_LENGTH] & 0x00FF) | (int)(byteArray[CCSDS_IDX_LENGTH+1] << 8)) + CCSDS_LENGTH_ADJUST;

   }// End getTotalLength()
      
   /**
    * Returns the packet's Stream ID.
    * 
    * @return CCSDS Stream ID
    */
   public int getStreamId() {
      
      return ( (( (byteArray[CCSDS_IDX_STREAM_ID] & 0x00FF) | (byteArray[CCSDS_IDX_STREAM_ID+1] << 8)) & CCSDS_MSK_MSG_ID) );

   }// End getStreamId()

   /**
    * Set the packet's Stream ID. Typically this is set when the packet is
    * constructed but there may be instances when the message ID is set
    * after the packet is created.  
    * 
    * @return CCSDS Stream ID
    */
   public void setStreamId(int streamId) {
      
      byteArray[CCSDS_IDX_STREAM_ID]   = new Integer(streamId&0xFF).byteValue();
	  byteArray[CCSDS_IDX_STREAM_ID+1] = new Integer((streamId&0xFF00)>>8).byteValue();

   }// End setStreamId()

   /**
    * Returns the packet's sequence count.
    * 
    * @return CCSDS Stream ID
    */
   public int getSeqCount() {
      
      return ( (( (byteArray[CCSDS_IDX_SEQ_COUNT] & 0x00FF) | (byteArray[CCSDS_IDX_SEQ_COUNT+1] << 8)) & CCSDS_MSK_SEQ_CNT) );

   }// End getSeqCount() 

   /**
    * Returns the packet's CCSDS defined length.
    * 
    * @return CCSDS defined length
    */
   public int getLength() {
      
      return ((byteArray[CCSDS_IDX_LENGTH] & 0x00FF) | (byteArray[CCSDS_IDX_LENGTH+1] << 8));

   }// End getLength()

   /**
    * Return a byte array containing the packet.
    * 
    * @return byte array containing the packet
    */
   public byte[] getByteArray() {
      
      return byteArray;

   }// End getByteArray()

   /**
    * Return a ByteBuffer containing the CCSDS packet
    * 
    * @return
    */
   public ByteBuffer getByteBuffer() {
      final ByteBuffer byteBuf = ByteBuffer.wrap(new byte[byteArray.length]);
   
      byteBuf.put(byteArray);
      byteBuf.flip();
   
      return byteBuf;
      
  } // getByteBuffer()

} // End class CcsdsPkt
