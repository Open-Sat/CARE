package fsw;

/**
 * Provide string conversions for 32-bit unsigned integers data points.  
 * 
 * @author David McComas
 *
 */
public class DataPntStrUint32 extends DataPntStr {

   public DataPntStrUint32() {
      
      super();
   
   } // End DataPntStrUint32()
   
   
   @Override
   public String byteToStr(byte[] rawData, int rawIndex) {

      return Uint32ToStr (rawData, rawIndex);
 
   } // End byteToStr ()

 } // End class DataPntStrUint32()
