package fswcore;

import ccsds.*; 
import fsw.FswApp;

/**
 * Preliminary hard coded version of the Software Bus application that has been replaced by the XML
 * definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class SbApp extends FswApp
{

   static final public String  PREFIX_STR = "SB";
      
   static final public int CMD_MID = 0x1803;   
   static final public int CMD_FC_NOOP              = 0;
   static final public int CMD_FC_RESET             = 1;
   static final public int CMD_FC_SEND_SB_STATS     = 2;
   static final public int CMD_FC_WRITE_ROUTE_INFO  = 3;
   static final public int CMD_FC_ENA_ROUTE         = 4;
   static final public int CMD_FC_DIS_ROUTE         = 5;  // FC 6 is unused
   static final public int CMD_FC_WRITE_PIPE_INFO   = 7;
   static final public int CMD_FC_WRITE_MAP_INFO    = 8;
   static final public int CMD_FC_ENA_SUB_REPORTING = 9;
   static final public int CMD_FC_DIS_SUB_REPORTING = 10;
   static final public int CMD_FC_SEND_PREV_SUBS    = 11;
   
   static final public int TLM_MID_HK           = 0x0803;   
   static final public int TLM_MID_HK_MID_STATS = 0x080A;
   
   
   public SbApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End SbApp
   
   public void defineCmds() {
    
      // Add commands here if wanted in GUI drop down
      
   } // defineCmds
   
   public void defineTlm() {
      
    //TODO - TlmList.add(TLM_MID_HK);
         
   } // defineTlm
   
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      // Add logic to process strings you want displayed
      return null; 
      
   } // getTlmStr

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
    //TODO - loadTlmStrArrayHdr(TlmMsg);
      
      return tlmStrArray;
      
   } // getTlmStrArray()

   
} // End class SbApp
