package fsw;

import java.util.ArrayList;   

import org.apache.log4j.Logger;

import ccsds.CcsdsCmdPkt;

/**
 * This class provides a wrapper around or a container for a CCSDS command
 * packet. It can be constructed with or without a set of default command
 * parameters. Storage is allocated during construction.
 * 
 * @author David McComas
 */
public class CmdPkt {
   
   private static Logger logger=Logger.getLogger(CmdPkt.class);

   private String  AppPrefix;
   private String  Name;
   private ArrayList<CmdParam>  ParamList = new ArrayList<CmdParam>();
   private int     ParamByteLen;   // Total number of bytes in ParamList
   
   CcsdsCmdPkt CmdPkt;
   
  
   
   /**
    * Constructor a CmdPkt without a set of command parameters. Note the 
    * data length must still be known so storage can be allocated.
    * 
    * @param AppPrefix  Short string prefix defining the application. See CFS coding standards.
    * @param Name       String providing full name of the application
    * @param MsgId      Command packet message ID
    * @param FuncCode   Command function code. See CFS coding standards for function code usage.
    * @param DataLen    Total length of command parameters.
    */
   public CmdPkt (String AppPrefix, String Name, int MsgId, int FuncCode, int DataLen) {
   
      this.AppPrefix = AppPrefix;
      this.Name      = Name;
      ParamByteLen   = 0;   // Will be computed as parameters added
      
      CmdPkt = new CcsdsCmdPkt(MsgId, CcsdsCmdPkt.CCSDS_CMD_HDR_LEN+DataLen, FuncCode);
      CmdPkt.ComputeChecksum();
      
   } // End CmdPkt()
   
   /**
    * Constructor a CmdPkt with a set of default command parameters.
    * 
    * @param AppPrefix  Short string prefix defining the application. See CFS coding standards.
    * @param Name       String providing full name of the application
    * @param MsgId      Command packet message ID
    * @param FuncCode   Command function code. See CFS coding standards for function code usage.
    * @param DataBuf    Byte array containing default command parameter as binary data
    * @param DataLen    Total length of command parameters.
    */
    public CmdPkt (String AppPrefix, String Name, Integer MsgId, Integer FuncCode, byte[] DataBuf, int DataLen ) {

       this.AppPrefix = AppPrefix;
       this.Name      = Name;
       ParamByteLen   = DataLen;
       
       CmdPkt = new CcsdsCmdPkt(MsgId, CcsdsCmdPkt.CCSDS_CMD_HDR_LEN+DataLen, FuncCode);
       CmdPkt.loadData(DataBuf, DataLen);

    } // End CmdPkt()

   
   /**
    * Load command parameters from a byte array 
    * 
    * @param DataBuf    Byte array containing default command parameter as binary data
    * @param DataLen    Total length of command parameters.
    *
    * @return Complete CCSDS Command packet with checksum computed
    */
   public CcsdsCmdPkt LoadParams(byte[] DataBuf, int DataLen) {      

      if (DataLen > 0)
      {
         CmdPkt.loadData(DataBuf,DataLen);
      }
     
      CmdPkt.ComputeChecksum();

      return CmdPkt;
     
   } // LoadParams()

     
   /**
    * Replace CmdParam array list and load the data. 
    *    
    * @param paramList  An ArrayList of CmdParam objects
    * 
    * @return Complete CCSDS Command packet with checksum computed
    */
   public CcsdsCmdPkt setParamList(ArrayList<CmdParam>  paramList)
   {      

      ParamList = paramList;
      
      return CmdPkt = loadParamList();  // Also computes checksum 

   } // setParamList()
   
   /**
    * Load parameter data from the current parameter list. This is handy when a 
    * individual command parameters have been set (e.g. from a GUI) and the 
    * complete parameter list needs to be loaded. 
    * 
    * @return Complete CCSDS Command packet with checksum computed
    */
   public CcsdsCmdPkt loadParamList()
   {      

      byte[] CmdParamBuffer;
      int    CmdParamBufIndx = 0;
      
      if (!ParamList.isEmpty())
      {
         
         ParamByteLen = 0;
         for (int i=0; i < ParamList.size(); i++)
         {
            ParamByteLen += ParamList.get(i).getNumBytes();
         }
         CmdParamBuffer = new byte[ParamByteLen];
         logger.trace("CmdPkt::loadParamList() - Creating parameter list with byte length " + ParamByteLen);
               
         for (int i=0; i < ParamList.size(); i++)
         {
            byte[] ParamBuffer = ParamList.get(i).getByteArray();
            
            for (int j=0; j < ParamList.get(i).getNumBytes(); j++)
            {
            
               CmdParamBuffer[CmdParamBufIndx++] = ParamBuffer[j];
               logger.trace("CmdParamBuffer["+(CmdParamBufIndx-1)+"] = " + CmdParamBuffer[CmdParamBufIndx-1]);
            
            } // End by
              
         } // End parameter list loop
           
         CmdPkt.loadData(CmdParamBuffer,ParamByteLen);
           
      } // ParamList not empty
      else
      {
         // Currently default to a null buffer             
      }
     
      CmdPkt.ComputeChecksum();

      return CmdPkt;
     
   } // loadParamList()

   
   /**
    * Returns the current CCSDS Command Packet without effecting its state. The
    * checksum is not recomputed.
    * 
    * @return Current CCSDS Command Packet  
    */
   public CcsdsCmdPkt getCcsdsPkt() {
      
      return CmdPkt;
   
   } // getCcsdsPkt()
   
   /**
    * Return the applicationa's prefix string.
    * 
    * @return String containing the application prefix
    */
   public String getAppPrefix() {
      
      return AppPrefix;
   
   } // getAppPrefix()

   /**
    * Return the applicationa's name string.
    * 
    * @return String containing the application name
    */
   public String getName() {
      
      return Name;
   
   } // getName()

   /**
    * Return the applicationa's prefix string.
    * 
    * @return String containing the application prefix
    */
   public int getFuncCode() {
      
      return CmdPkt.getFuncCode();
   
   } // getCcsdsPkt()

   public void addParam(CmdParam Param) {
      
      ParamList.add(Param);
   
   } // addParam()

   public ArrayList<CmdParam> getParamList()
   {      
      return ParamList;
   
   } // getParamList()

   public boolean hasParam()
   {      
      return !ParamList.isEmpty();
   
   } // hasParam()

   public void setParam(int paramNum, String paramValue)
   {      
      ParamList.get(paramNum).setValue(paramValue);
   
   } // addParam()
   /*
    * @todo - Can this be deleted? No references.
    * 
    * Create a command parameter byte array that can easily be used with CCSDS Packet
    * methods.
    * 
    * CmdParam   - String of comma separated parameters
    * ParamBytes - Number of bytes in each parameter
    * ParamCnt   - Number of parameters  
    */
   byte[] createCmdByteArray(String CmdParam, int ParamBytes[], int ParamCnt) {
      
      byte[] DataBuffer = null;

      String [] Param = CmdParam.split(",");
     
      if (Param.length == ParamCnt)
      {
         int i, DataLen=0, DataIndex=0;
      
           
         for (i=0; i < ParamCnt; i++)
         {
            DataLen += ParamBytes[i];
            logger.trace("Param["+i+"]="+Param[i]);
         }
               
         DataBuffer = new byte[DataLen];
        
         for (i=0; i < ParamCnt; i++)
         {
            if (ParamBytes[i] == 1)
            {
               DataBuffer[DataIndex++] = new Integer(Integer.parseInt(Param[i])).byteValue();
               logger.trace("DataBuffer"+(DataIndex-1)+"]="+DataBuffer[DataIndex-1]);
            }
            else if (ParamBytes[i] == 2)
            {
               int Temp = Integer.parseInt(Param[i]);
               DataBuffer[DataIndex++] = new Integer(Temp & 0xFF).byteValue();
               DataBuffer[DataIndex++] = new Integer((Temp & 0xFF00 ) >> 8).byteValue();
               logger.trace("DataBuffer"+(DataIndex-2)+"]="+DataBuffer[DataIndex-2]);
               logger.trace("DataBuffer"+(DataIndex-1)+"]="+DataBuffer[DataIndex-1]);
            }
            else
            {
            
               // @todo - Resolve illegal parameter bytes definition              
            
            }
              
           } // End parameter loop
           
      }
      else
      {
         // Currently default to a null buffer             
      }

     return DataBuffer;
       
   } //  createCmdByteArray()
   
} // End class CmdPkt