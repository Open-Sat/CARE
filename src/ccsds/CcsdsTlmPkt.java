package ccsds;

/**
 *  Defines a CCSDS telemetry packet.
 *  
 *  TODO - Add description
 *
 * @author David McComas
 *
*/
public class CcsdsTlmPkt extends CcsdsPkt {
   
   /** Byte array index of start of telemetry header */
   static final public int CCSDS_IDX_TLM_HDR   =  6;
   
   /** Byte array index of start of telemetry data */
   static final public int CCSDS_IDX_TLM_DATA  = 12;

   /** Length in bytes of the telemetry header */
   static final public int CCSDS_TLM_HDR_LEN   = 12;

   /**
    * Construct a packet from raw data stream.
    * 
    * @param tlmMsg  byte array containing the telemetry packet
    */
   public CcsdsTlmPkt(byte[] tlmMsg) {
      
      super(tlmMsg);
      
   } // End CcsdsTlmPkt()

} // End class CcsdsTlmPkt
