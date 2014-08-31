package fsw;

/**
 * Provide string conversions for 16-bit unsigned integers data points.  
 * 
 * @author David McComas
 *
 */
public class DataPntStrUint16 extends DataPntStr {

   public DataPntStrUint16() {
      
      super();
   
   } // End DataPntStrUint16()
   
   
   /*
   ** Java doesn't have unsigned integers. Use 'long' that should be 64 bits and avoid sign bit
   */
   
   @Override
   public String byteToStr(byte[] rawData, int rawIndex) {
      
      return Uint16ToStr(rawData, rawIndex);  

   } // End byteToStr()

} // End class DataPntStrUnit16
