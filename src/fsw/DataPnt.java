package fsw;

import org.apache.log4j.Logger;


/*
** @author dmccomas
** 
** TODO - Code: Change BitMask from singed int to unsigned and add code to set it.
*/

public class DataPnt {
   
   private static Logger logger=Logger.getLogger(DataPnt.class);

   static public enum DspType { DSP_HEX, DSP_OCT, DSP_DEC, DSP_FLT, DSP_DBL }; 
   private String    Name;
   private int       ByteOffset;
   private int       NumBytes;
   private int       BitMask;
   private DspType   Display;
   private String    Description = null;
   private DataPntStr StrObj = null;

   public final static String VEC3D_TO_FLOAT = "Vector3dToFloat";

   public DataPnt(String Name, int ByteOffset, int NumBytes,
                 int BitMask, DspType Display, String Description,
                 DataPntStr ByteToStrFunc)
   {
      
      logger.trace("TlmPnt: " + Name);
      
      this.Name          = Name;
      this.ByteOffset    = ByteOffset;
      this.NumBytes      = NumBytes;
      this.BitMask       = BitMask;
      this.Display       = Display;
      this.Description   = Description;
      this.StrObj        = ByteToStrFunc;

   } // End TlmPntModel

   public String getName() {

      return this.Name;

   } // getName()

   public int getByteOffset() {

      return this.ByteOffset;

   } 

   public int getNumBytes()
   {

      return this.NumBytes;

   }

   public int getBitMask()
   {

      return this.BitMask;

   }

   public String getDescription()
   {

      return this.Description;

   }

   public DspType getDisplay()
   {

      return this.Display;

   }

   public DataPntStr getStrObj() {

      return StrObj;

   }
   
} // End class TlmPnt
