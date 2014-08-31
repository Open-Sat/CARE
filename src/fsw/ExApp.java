package fsw;

import ccsds.*;

public class ExApp extends FswApp
{

   static final public Integer CMD_MID      = 0x1863;   
   static final public Integer CMD_FC_RESET = 0;
   static final public Integer CMD_FC_NOOP  = 1;

   static final public int TLM_MID_PKT1   = 0x0F00;
   static final public int TLM_MID_FD     = 0x0F01;
   static final public int TLM_MID_HK_RPY = 0x0F02;
  
   //static final public Integer CMD_MID_SEND_HK = 0x1891;
 
   //static final public Integer TLM_MID_HK = 0x0881;   

   static final public String  PREFIX_STR = "EX";

   public ExApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End ExApp
   
   public void defineCmds() {
    
      cmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      cmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));
/**      
      CmdPkt TestCmd = new CmdPkt(PREFIX_STR, "Test", CMD_MID, 4);
      TestCmd.addParam(new CmdParam("P1",1, 2));
      TestCmd.addParam(new CmdParam("P2",2, 2));
      TestCmd.addParam(new CmdParam("P3",3, 2));
      CmdList.set(4, TestCmd);
**/      
   } // defineCmds
   
   public void defineTlm() {
      
    //TODO - TlmList.add(TLM_MID_PKT1);
    //TODO -   TlmList.add(TLM_MID_FD);
    //TODO -   TlmList.add(TLM_MID_HK_RPY);
         
   } // defineTlm

   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      //return TLM_STR_TBD; 
      //return ParseRawData(TlmMsg.getPacket());
      return DataPntStr.ParseRawData(TlmMsg.getByteArray());
      
   } // getTlmStr

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
    //TODO - loadTlmStrArrayHdr(TlmMsg);
      
      return tlmStrArray;
      
   } // getTlmStrArray()


} // End class ExApp
