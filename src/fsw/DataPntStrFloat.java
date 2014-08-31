package fsw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Provide string conversions for 32-bit floating point data points.  
 * 
 * @author David McComas
 *
 */
public class DataPntStrFloat extends DataPntStr {

   public DataPntStrFloat() {
      
      super();
   
   } // End DataPntStrFloat()
   
   
   @Override
   public String byteToStr(byte[] rawData, int rawIndex) {
      
      final ByteBuffer byteBuf = ByteBuffer.wrap(rawData); // ByteBuffer.wrap(rawData, rawIndex, 4);
      
      byteBuf.order(ByteOrder.LITTLE_ENDIAN);
      //byteBuf.order(ByteOrder.BIG_ENDIAN);
      return String.valueOf(byteBuf.getFloat(rawIndex)); 
 
   } // End byteToStr ()

 } // End class DataPntFloat()
