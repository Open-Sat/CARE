package fsw;

import org.apache.log4j.Logger;


/*
** @todo - Think about adding Value also stored as an integer with a non string constructor
** @todo - More robust NumByte error handling
** @todo - Add radix support
**
*/
public class CmdIntParam extends CmdParam {

   private static Logger logger=Logger.getLogger(CmdIntParam.class);
  
   /*
    * Constructor: Integer Parameter
    */
   public CmdIntParam(String Name, String DefValue, int NumBytes)
   {
      super (Name, ParamType.INT, DefValue, NumBytes);
      
   } // End CmdParam()
   
   protected byte[] loadByteArray() {
      
      switch (NumBytes){
      
      case 1:
         logger.trace("CmdIntParam::loadByteArray - 1 byte parameter"); 
         ByteArray[0] = Integer.valueOf(Value).byteValue();
         logger.trace("ByteArray[0] = " + ByteArray[0]);
         break;
      
      case 2:
         logger.trace("CmdIntParam::loadByteArray - 2 byte parameter"); 
         int Temp2 = Integer.valueOf(Value);
         ByteArray[0] = (byte) (Temp2 & 0xFF);
         ByteArray[1] = (byte)((Temp2 & 0xFF00) >> 8);
         logger.trace("ByteArray[0] = " + ByteArray[0]);
         logger.trace("ByteArray[1] = " + ByteArray[1]);
         break;
      
      case 4:
         logger.trace("CmdIntParam::loadByteArray - 4 byte parameter"); 
         long Temp4 = (long)Integer.valueOf(Value);
         ByteArray[0] = (byte) (Temp4 & 0x000000FF);
         ByteArray[1] = (byte)((Temp4 & 0x0000FF00) >>  8);
         ByteArray[2] = (byte)((Temp4 & 0x00FF0000) >> 16);
         ByteArray[3] = (byte)((Temp4 & 0xFF000000) >> 24);
         logger.trace("ByteArray[0] = " + ByteArray[0]);
         logger.trace("ByteArray[1] = " + ByteArray[1]);
         logger.trace("ByteArray[2] = " + ByteArray[2]);
         logger.trace("ByteArray[3] = " + ByteArray[3]);
         break;

      default:
         logger.error("CmdIntParam::loadByteArray - Unsupported datasize"); 
         
      } // End NumByte Switch

      return ByteArray;
      
   } // End loadByteArray()

} // End CmdIntparam
