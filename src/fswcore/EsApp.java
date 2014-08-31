package fswcore;

import app.CARE; 
import ccsds.*;
import fsw.CmdIntParam;
import fsw.CmdPkt;
import fsw.CmdStrParam;
import fsw.FswApp;
import fsw.TlmPkt;
import fsw.DataPnt;
import fsw.DataPntStr;
import fsw.DataPntStrUint16;
import fsw.DataPntStrUint32;
import fsw.DataPntStrUint8;
import fsw.DataPnt.DspType;

/**
 * Preliminary hard coded version of the Executive Services application that has been replaced by the XML
 * definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class EsApp extends FswApp
{

   static final public String  PREFIX_STR = "ES";
      
   static final public int CMD_MID = 0x1806;   
   static final public int CMD_FC_NOOP               = 0;
   static final public int CMD_FC_RESET              = 1;
   static final public int CMD_FC_RESTART            = 2;
   static final public int CMD_FC_SHELL_CMD          = 3;
   static final public int CMD_FC_CFE_ES_START_APP   = 4;
   static final public int CMD_FC_STOP_APP           = 5;
   static final public int CMD_FC_RESTART_APP        = 6;
   static final public int CMD_FC_RELOAD_APP         = 7;
   static final public int CMD_FC_QUERY_ONE          = 8;
   static final public int CMD_FC_QUERY_ALL          = 9;
   static final public int CMD_FC_CLEAR_SYSLOG       = 10;  
   static final public int CMD_FC_WRITE_SYSLOG       = 11;
   static final public int CMD_FC_CLEAR_ERLOG        = 12;
   static final public int CMD_FC_WRITE_ERLOG        = 13;
   static final public int CMD_FC_PERF_STARTDATA     = 14;
   static final public int CMD_FC_PERF_STOPDATA      = 15;
   static final public int CMD_FC_PERF_SETFILTERMASK = 16;  
   static final public int CMD_FC_PERF_SETTRIGMASK   = 17;
   static final public int CMD_FC_OVERWRITE_SYSLOG   = 18;
   static final public int CMD_FC_RESET_PR_COUNT     = 19;
   static final public int CMD_FC_SET_MAX_PR_COUNT   = 20;
   static final public int CMD_FC_DELETE_CDS         = 21;
   static final public int CMD_FC_TLM_POOL_STATS     = 22;
   static final public int CMD_FC_WRITE_CDS_REG      = 23;
   static final public int CMD_FC_QUERY_ALL_TASKS    = 24;

   static final public int TLM_MID_HK       = 0x0800;   
   static final public int TLM_MID_APP      = 0x0811;   
   static final public int TLM_MID_SHELL    = 0x0815;   
   static final public int TLM_MID_MEMSTATS = 0x0816;   
   
   public EsApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End EsApp
   
   public void defineCmds() {
    
      cmdList.set(CMD_FC_NOOP,         new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      cmdList.set(CMD_FC_RESET,        new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));
      cmdList.set(CMD_FC_CLEAR_ERLOG,  new CmdPkt(PREFIX_STR, "Clear Err Log", CMD_MID, CMD_FC_CLEAR_ERLOG, 0));

      CmdPkt Cmd = new CmdPkt(PREFIX_STR, "Set Sys Log Mode", CMD_MID, CMD_FC_OVERWRITE_SYSLOG, 4);
      Cmd.addParam(new CmdIntParam("Mode","0", 4));  // 0=Overwrite, 1=Discard
      Cmd.loadParamList();
      cmdList.set(CMD_FC_OVERWRITE_SYSLOG, Cmd);

      Cmd = new CmdPkt(PREFIX_STR, "Write Sys Log to a File", CMD_MID, CMD_FC_WRITE_SYSLOG, 64); // One string parameter
      Cmd.addParam(new CmdStrParam("Path/Filename","/cf/cfe_es_syslog.log", 64));  // Null string uses default, 64 = Max path/filename (OS_MAX_PATH_LEN)
      Cmd.loadParamList();
      cmdList.set(CMD_FC_WRITE_SYSLOG, Cmd);
      
      Cmd = new CmdPkt(PREFIX_STR, "Write Err Log to a File", CMD_MID, CMD_FC_WRITE_ERLOG, 64); // One string parameter
      Cmd.addParam(new CmdStrParam("Path/Filename","/cf/cfe_es_errlog.log", 64));  // Null string uses default, 64 = Max path/filename (OS_MAX_PATH_LEN)
      Cmd.loadParamList();
      cmdList.set(CMD_FC_WRITE_ERLOG, Cmd);

      Cmd = new CmdPkt(PREFIX_STR, "Restart Application", CMD_MID, CMD_FC_RESTART_APP, 20); // One string parameter
      Cmd.addParam(new CmdStrParam("App Name","EX_APP", 20));  // 20 = Max API Name (OS_MAX_API_NAME)
      Cmd.loadParamList();
      cmdList.set(CMD_FC_RESTART_APP, Cmd);

   } // defineCmds
   
   public void defineTlm() {
      
      defineHkPkt();
         
   } // defineCmds
  
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      return DataPntStr.ParseRawData(TlmMsg.getByteArray()); // TODO - Customize for app
      
   } // getTlmStr()


   public void defineHkPkt() {
      
      int     len;
      int     offset  = CcsdsTlmPkt.CCSDS_IDX_TLM_DATA;
      int     bitMask = 0;                  // TODO - Unused
      DspType dspType = DspType.DSP_DEC;    // TODO - Unused
      String  descr   = CARE.STR_UNDEFINED; // TODO - Unused
      
      TlmPkt tlmPkt = new TlmPkt("Housekeeping", TLM_MID_HK);
      
      len = 1;
      tlmPkt.addDataPnt(new DataPnt("CmdCounter", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("ErrCounter", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;

      len = 2;
      tlmPkt.addDataPnt(new DataPnt("CFECoreChecksum", offset, len, bitMask, dspType, descr, new DataPntStrUint16()));
      offset += len;

      len = 1;
      tlmPkt.addDataPnt(new DataPnt("CFEMajorVersion", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("CFEMinorVersion", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("CFERevision", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("CFEMissionRevision", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("OSALMajorVersion", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("OSALMinorVersion", offset, len, bitMask, dspType, descr, new DataPntStrUint8()));
      offset += len;
      
      len=2;
      tlmPkt.addDataPnt(new DataPnt("Unused", offset, len, bitMask, dspType, descr, new DataPntStrUint16()));
      offset += len;

      len=4;
      tlmPkt.addDataPnt(new DataPnt("SysLogBytesUsed", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("SysLogSize", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("SysLogEntries", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("SysLogMode", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("ERLogIndex", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("ERLogEntries", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("RegisteredCoreApps", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("RegisteredExternalApps", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("RegisteredTasks", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("RegisteredLibs", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("ResetType", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("ResetSubtype", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("ProcessorResets", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("MaxProcessorResets", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("BootSource", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfState", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfMode", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfTriggerCount", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfFilterMask[CFE_ES_PERF_MAX_IDS/32]", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfDataStart", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfDataEnd", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfDataCount", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("PerfDataToWrite", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("HeapBytesFree", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("HeapBlocksFree", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));
      offset += len;
      tlmPkt.addDataPnt(new DataPnt("HeapMaxBlockSize", offset, len, bitMask, dspType, descr, new DataPntStrUint32()));

      //TlmList.add(tlmPkt);
      tlmPktMap.put(TLM_MID_HK, tlmPkt);

      
   } // defineHkPkt()

/********
   public void loadTlmStrArrayHk(byte[] RawData) 
   {
                                              // (StrArrayIndex, RawData, RawDataIndex)
      loadTlmStrArrayUint8(5, RawData, 12);   // CmdCounter;
      loadTlmStrArrayUint8(6, RawData, 13);   // ErrCounter; 
      loadTlmStrArrayUint16(7, RawData, 14);  // CFECoreChecksum;    
      loadTlmStrArrayUint8(8, RawData, 16);   // CFEMajorVersion    
      loadTlmStrArrayUint8(9, RawData, 17);   // CFEMinorVersion;    
      loadTlmStrArrayUint8(10, RawData, 18);  // CFERevision;       
      loadTlmStrArrayUint8(11, RawData, 19);  // CFEMissionRevision; 
      loadTlmStrArrayUint8(12, RawData, 20);  // OSALMajorVersion;
      loadTlmStrArrayUint8(13, RawData, 21);  // OSALMinorVersion;   
      //uint8                 Padding1;           
      //uint8                 Padding2;           
      loadTlmStrArrayUint32(14, RawData, 24); // SysLogBytesUsed; 
      loadTlmStrArrayUint32(15, RawData, 28); // SysLogSize;     
      loadTlmStrArrayUint32(16, RawData, 32); // SysLogEntries;   
      loadTlmStrArrayUint32(17, RawData, 36); // SysLogMode;      
      loadTlmStrArrayUint32(18, RawData, 40); // ERLogIndex;       
      loadTlmStrArrayUint32(19, RawData, 44); // ERLogEntries;   
      loadTlmStrArrayUint32(20, RawData, 48); // RegisteredCoreApps;      
      loadTlmStrArrayUint32(21, RawData, 52); // RegisteredExternalApps;  
      loadTlmStrArrayUint32(22, RawData, 56); // RegisteredTasks;         
      loadTlmStrArrayUint32(23, RawData, 60); // RegisteredLibs;  
      loadTlmStrArrayUint32(24, RawData, 64); // ResetType;
      loadTlmStrArrayUint32(25, RawData, 68); // ResetSubtype;    
      loadTlmStrArrayUint32(26, RawData, 72); // ProcessorResets;
      loadTlmStrArrayUint32(27, RawData, 76); // MaxProcessorResets; 
      loadTlmStrArrayUint32(28, RawData, 80); // BootSource;      
      loadTlmStrArrayUint32(29, RawData, 84); // PerfState;       
      loadTlmStrArrayUint32(30, RawData, 88); // PerfMode;        
      loadTlmStrArrayUint32(31, RawData, 92); // PerfTriggerCount;                           
      loadTlmStrArrayUint32(32, RawData, 96); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(33, RawData, 100); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(34, RawData, 104); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(35, RawData, 108); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(36, RawData, 112); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(37, RawData, 116); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(38, RawData, 120); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(39, RawData, 124); // PerfFilterMask[CFE_ES_PERF_MAX_IDS / 32];   
      loadTlmStrArrayUint32(40, RawData, 128); // PerfDataStart;   
      loadTlmStrArrayUint32(41, RawData, 132); // PerfDataEnd;     
      loadTlmStrArrayUint32(42, RawData, 136); // PerfDataCount;   
      loadTlmStrArrayUint32(43, RawData, 140); // PerfDataToWrite; 
      loadTlmStrArrayUint32(44, RawData, 144); // HeapBytesFree;     
      loadTlmStrArrayUint32(45, RawData, 148); // HeapBlocksFree;    
      loadTlmStrArrayUint32(46, RawData, 152); // HeapMaxBlockSize;  

   } // loadTlmStrArrayHk()
***/
   
} // End class EsApp
