package fsw;

import org.apache.log4j.Logger;

public class CmdStrParam extends CmdParam {

   private static Logger logger=Logger.getLogger(CmdStrParam.class);

   /*
    * Constructor: String Parameter
    * 
    * NumBytes is the maximum length of the string
    * 
    */
   public CmdStrParam(String Name, String DefValue, int NumBytes)
   {
      super (Name, ParamType.STR, DefValue, NumBytes);
      
   } // End CmdParam()
   
   protected byte[] loadByteArray() {
      
      logger.debug("CmdStrParam::loadByteArray with NumBytes = " + NumBytes); 

      // TODO - Add error protection (null & invalid length). Assumes user ensures '\0' terminated 
      for (int i=0; i < Value.length(); i++) {
          ByteArray[i] = (byte)(Value.codePointAt(i) & 0x0FF);  // Unicode equals ASCII
          logger.trace("ByteArray["+i+"] = " + ByteArray[i]);
      }
      
      return ByteArray;
      
   } // End loadByteArray()

} // End Class CmdStrParam
