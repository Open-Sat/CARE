package fswcore;

import ccsds.*; 
import fsw.*;


/**
 * Preliminary hard coded version of the Telemetry Output Lab application that
 * has been replaced by the XML definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */

public class ToLabApp extends FswApp
{

   // These must match the cFE's test lab TO (UDP based) definitions
   static final public String  IP_ADDR = "192.168.1.81";
   //static final public String  IP_ADDR = "127.0.0.1";
   static final public int     IP_PORT = 1235;

   static final public int CMD_MID = 0x1880;   
   static final public int CMD_FC_RESET           = 0;
   static final public int CMD_FC_NOOP            = 1;
   static final public int CMD_FC_LOAD_PKT_TBL    = 2;
   static final public int CMD_FC_DUMP_PKT_TBL    = 3;
   static final public int CMD_FC_ADD_PKT         = 4;
   static final public int CMD_FC_REM_PKT         = 5;
   static final public int CMD_FC_REM_ALL_PKTS    = 6;
   static final public int CMD_FC_ENA_TLM         = 7;
   static final public int CMD_FC_SEND_DATA_TYPES = 8;

/* Original TO_LAB 
   static final public int CMD_FC_NOOP            = 0;
   static final public int CMD_FC_RESET           = 1;
   static final public int CMD_FC_ADD_PKT         = 2;
   static final public int CMD_FC_SEND_DATA_TYPES = 3;
   static final public int CMD_FC_REM_PKT         = 4;
   static final public int CMD_FC_REM_ALL_PKTS    = 5;
   static final public int CMD_FC_ENA_TLM         = 6;
*/
   
   static final public int CMD_MID_SEND_HK = 0x1881;
 
   static final public int TLM_MID_HK      = 0x0880;   
   static final public int TLM_MID_DAT_TYP = 0x0881;   

   static final public String  PREFIX_STR = "TO";
   
   public ToLabApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End TlmOutput
   
   public void defineCmds() {

      // Command variables used to 
      CmdPkt cmdPkt;
      int    cmdDataLen;
      byte[] cmdDataBuf = null;

      cmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      cmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));
/*     
      cmdDataLen = 15;
      cmdDataBuf = new byte[cmdDataLen];
      cmdDataBuf[0] = 0x31; // 127.
      cmdDataBuf[1] = 0x32;
      cmdDataBuf[2] = 0x37;
      cmdDataBuf[3] = 0x2E;
      cmdDataBuf[4] = 0x30; // 000.
      cmdDataBuf[5] = 0x30;
      cmdDataBuf[6] = 0x30;
      cmdDataBuf[7] = 0x2E;
      cmdDataBuf[8] = 0x30; // 000.
      cmdDataBuf[9] = 0x30;
      cmdDataBuf[10] = 0x30;
      cmdDataBuf[11] = 0x2E;
      cmdDataBuf[12] = 0x30; // 001
      cmdDataBuf[13] = 0x30;
      cmdDataBuf[14] = 0x31;
      cmdPkt = new CmdPkt(PREFIX_STR, "Ena Tlm", CMD_MID, CMD_FC_ENA_TLM, cmdDataBuf, cmdDataLen);
      CmdList.set(CMD_FC_ENA_TLM,cmdPkt);
*/
// The following doesn't work & not sure why - It doesn't load the parameter bytes just the param list
      cmdPkt = new CmdPkt(PREFIX_STR, "Ena Tlm", CMD_MID, CMD_FC_ENA_TLM, 15);  // One IP address parameter
      cmdPkt.addParam(new CmdStrParam("IP Address","127.000.000.002", 15));
      cmdPkt.loadParamList();
      cmdList.set(CMD_FC_ENA_TLM,cmdPkt);
      
      cmdPkt = new CmdPkt(PREFIX_STR, "Add Pkt", CMD_MID, CMD_FC_ADD_PKT, 7);
      cmdPkt.addParam(new CmdIntParam("Message ID","2048", 2));  // // 3840 = 0xF00 (ExApp), 2048 = 0x800 (ES HK)
      cmdPkt.addParam(new CmdIntParam("Pkt Size","50", 2));
      cmdPkt.addParam(new CmdIntParam("SB QoS","0", 2));
      cmdPkt.addParam(new CmdIntParam("Buffer Cnt","1", 1));
      cmdPkt.loadParamList();
      cmdList.set(CMD_FC_ADD_PKT, cmdPkt);
      
      cmdPkt = new CmdPkt(PREFIX_STR, "Rem Pkt", CMD_MID, CMD_FC_REM_PKT, 2); // One 2 byte parameter
      cmdPkt.addParam(new CmdIntParam("Message ID","3840", 2));
      cmdPkt.loadParamList();
      cmdList.set(CMD_FC_REM_PKT, cmdPkt);
      
   } // defineCmds

   public void defineTlm() {
      
    //TODO - TlmList.add(TLM_MID_HK);
         
   } // defineTlm
  
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      //return TLM_STR_TBD; 
      return null;
      
   } // getTlmStr

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
    //TODO - loadTlmStrArrayHdr(TlmMsg);
      
      return tlmStrArray;
      
   } // getTlmStrArray()

   
}