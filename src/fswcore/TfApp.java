package fswcore;

import ccsds.*; 
import fsw.FswApp;

/**
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class TfApp extends FswApp
{

   static final public String  PREFIX_STR = "TFAPP";
      
   static final public int CMD_FC_RESET     = 0;
   static final public int CMD_FC_NOOP      = 1;
   static final public int CMD_FC_GET_FILE  = 2;
   static final public int CMD_FC_PUT_FILE  = 3;   
   
   public TfApp (String Prefix, String Name) {
      
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

   
} // End class TfApp
