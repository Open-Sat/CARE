package fswcore;

import ccsds.*;
import fsw.FswApp;

/**
 * Preliminary hard coded version of the Table Services application that has been replaced by the XML
 * definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class TblApp extends FswApp
{
   static final public String  PREFIX_STR = "TBL";
      
   static final public int CMD_MID             = 0x1804;   
   static final public int CMD_FC_NOOP         = 0;
   static final public int CMD_FC_RESET        = 1;
   static final public int CMD_FC_TBL_LOAD     = 2;
   static final public int CMD_FC_TBL_DUMP     = 3;
   static final public int CMD_FC_TBL_VALIDATE = 4;
   static final public int CMD_FC_TBL_ACTIVATE = 5;
   static final public int CMD_FC_DUMP_REG     = 6;
   static final public int CMD_FC_TBL_TLM_REG  = 7;
   static final public int CMD_FC_TBL_DEL_CDS  = 8;
   static final public int CMD_FC_ABORT_LOAD   = 9;
   
   
   static final public int TLM_MID_HK   = 0x0804;   

   public TblApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End TblApp
   
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

   
} // End class TblApp
