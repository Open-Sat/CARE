package fsw;

/**
 * Provide string conversions for string data points.  
 * 
 * @author David McComas
 *
 */
public class DataPntStrStr extends DataPntStr {

   private int strLen;
   
   public DataPntStrStr(int strLen) {
      
      super();
      this.strLen = strLen;
   
   } // End DataPntStrUint16()
   
   @Override
   public String byteToStr(byte[] rawData, int rawIndex) {
      
      return new String (rawData, rawIndex, strLen); // Use platform's default charset
         
   } // End byteToStr()

} // End class DataPntStrStr
