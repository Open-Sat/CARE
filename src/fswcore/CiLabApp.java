package fswcore;

import ccsds.CcsdsTlmPkt; 
import fsw.CmdPkt;
import fsw.FswApp;

/**
 * Preliminary hard coded version of the Command Ingest Labapplication that has
 * been replaced by the XML definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class CiLabApp extends FswApp
{

   // These must match the cFE's test lab CI (UDB based) definitions
   static final public Integer IP_PORT = 1234;
   
   static final public Integer CMD_MID = 0x1884;   
   static final public Integer CMD_FC_NOOP              = 0;
   static final public Integer CMD_FC_RESET             = 1;
   static final public Integer CMD_FC_MOD_PDU_FILESIZE  = 2;
   static final public Integer CMD_FC_CORRUPT_PDU_CKSUM = 3;
   static final public Integer CMD_FC_DROP_PDUS         = 4;
   static final public Integer CMD_FC_CAPTURE_PDUS      = 5;
   static final public Integer CMD_FC_STOP_PDU_CAPTURE  = 6;

   static final public Integer CMD_MID_SEND_HK = 0x1891;
   static final public Integer TLM_MID_HK = 0x0884;   

   static final public String  PREFIX_STR = "CI";
   
   public CiLabApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End CmdIngest
   
   public void defineCmds() {
    
      cmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      cmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));
      
   } // defineCmds

   public void defineTlm() {
      
      //TODO - TlmList.add(TLM_MID_HK);
         
   } // defineCmds

   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      return TLM_STR_TBD; 
      
   } // getTlmStr
   
   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
    //TODO - loadTlmStrArrayHdr(TlmMsg);
      
      return tlmStrArray;
      
   } // getTlmStrArray()

} // End class CiLabApp
