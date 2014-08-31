package fswcore;

import ccsds.*; 
import fsw.FswApp;

/**
 * Preliminary hard coded version of the Time application that has been replaced by the XML
 * definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class TimeApp extends FswApp
{

   static final public String  PREFIX_STR = "TIME";
      
   static final public int CMD_MID = 0x1805;
   static final public int CMD_FC_NOOP          = 0;
   static final public int CMD_FC_RESET         = 1;
   static final public int CMD_FC_SEND_DIAG_PKT = 2;
   static final public int CMD_FC_SET_SOURCE    = 3;
   static final public int CMD_FC_SET_STATE     = 4;
   static final public int CMD_FC_ADD_DELAY     = 5;
   static final public int CMD_FC_SUB_DELAY     = 6;
   static final public int CMD_FC_SET_TIME      = 7;
   static final public int CMD_FC_SET_MET       = 8;
   static final public int CMD_FC_SET_STCF      = 9;
   static final public int CMD_FC_SET_LEAP_SEC  = 10;
   static final public int CMD_FC_ADD_ADJUST    = 11;
   static final public int CMD_FC_SUB_ADJUST    = 12;
   static final public int CMD_FC_ADD_1HZADJ    = 13;
   static final public int CMD_FC_SUB_1HZADJ    = 14;
   static final public int CMD_FC_SET_TONE_SRC  = 15;
   
   static final public int TLM_MID_HK = 0x0805;   

   public TimeApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End TimeApp
   
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


} // End class TimeApp
