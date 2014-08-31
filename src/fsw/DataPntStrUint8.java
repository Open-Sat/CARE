package fsw;

/**
 * Provide string conversions for 8-bit unsigned integers data points.  
 * 
 * @author David McComas
 *
 */
public class DataPntStrUint8 extends DataPntStr {

   public DataPntStrUint8() {
   
      super();
   
   } // End DataPntStrUint8()
   
   @Override
   public String byteToStr(byte[] rawData, int rawIndex) {

      return Uint8ToStr(rawData, rawIndex);

   } // End byteToStr()
   
} // End class DataPntStrUnit8()
