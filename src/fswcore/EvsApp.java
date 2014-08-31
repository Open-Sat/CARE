package fswcore;

import app.CARE;  
import ccsds.*;
import fsw.CmdPkt;
import fsw.FswApp;
import fsw.DataPntStr;
import fsw.DataPntInt;

/**
 * Preliminary hard coded version of the Event Services application that has been replaced by the XML
 * definition.
 * 
 * TODO - Remove class once all definitions have been migrated to the database. 
 */
public class EvsApp extends FswApp
{

   /* Event Type bit masks */
   static public enum EventType { DEBUG, INFORMATION, ERROR, CRITICAL, UNDEF }
   
   static final public String  PREFIX_STR = "EVS";
      
   static final public int CMD_MID = 0x1801;
   static final public int CMD_FC_NOOP               = 1;
   static final public int CMD_FC_RESET              = 2;
   static final public int CMD_FC_ENA_EVENT_TYPE     = 3;
   static final public int CMD_FC_DIS_EVENT_TYPE     = 4;
   static final public int CMD_FC_SET_FORMAT_MODE    = 5;
   static final public int CMD_FC_ENA_APP_EVENT_TYPE = 6;
   static final public int CMD_FC_DIS_APP_EVENT_TYPE = 7;
   static final public int CMD_FC_ENA_APP_EVENTS     = 8;
   static final public int CMD_FC_DIS_APP_EVENTS     = 9;
   static final public int CMD_FC_RESET_APP_COUNTER  = 10;
   static final public int CMD_FC_ET_FILTERS         = 11;
   static final public int CMD_FC_ENA_PORTS          = 12;
   static final public int CMD_FC_DIS_PORTS          = 13;
   static final public int CMD_FC_RESET_FILTER       = 14;
   static final public int CMD_FC_RESET_ALL_FILTERS  = 15;
   static final public int CMD_FC_ADD_FILTER         = 16;
   static final public int CMD_FC_DEL_FILTER         = 17;
   static final public int CMD_FC_WRITE_APP_INFO     = 18;
   static final public int CMD_FC_WRITE_LOG          = 19;
   static final public int CMD_FC_SET_LOG_MODE       = 20;
   static final public int CMD_FC_CLEAR_LOG          = 21;

   static final public int TLM_MID_HK        = 0x0801;   
   static final public int TLM_MID_EVENT_MSG = 0x0808;

   /** Index into string array of parsed event packet items */
   public static final int IDX_SECS      = 0;
   public static final int IDX_SUB_SECS  = 1;
   public static final int IDX_APP_NAME  = 2;
   public static final int IDX_EVENT_ID  = 3;
   public static final int IDX_EVENT_TYP = 4;
   public static final int IDX_SCRAFT_ID = 5;
   public static final int IDX_PROC_ID   = 6;
   public static final int IDX_MSG_TEXT  = 7;

   /** Number of string return for a parsed event log entry */
   public static final int STR_PER_ENTRY = 8;

   
   public EvsApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End EvsApp
   
   public void defineCmds() {
    
      cmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      cmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));

   } // defineCmds
   
   public void defineTlm() {
      
    //TODO - TlmList.add(TLM_MID_HK);
    //TODO -   TlmList.add(TLM_MID_EVENT_MSG);
         
   } // defineTlm
   
   
   public String getTlmStr(CcsdsTlmPkt TlmMsg) {

      byte[]  tlmPkt = TlmMsg.getByteArray();
      return formatEventStr(tlmPkt, 0);
      
   } // getTlmStr
       

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
    //TODO - loadTlmStrArrayHdr(TlmMsg);
      
      return tlmStrArray;
      
   } // getTlmStrArray()

   /**
    * 
    * @param byteArray  Byte array containing the raw event message packet
    * @param byteIndex  Index into byte array to the start of the event message 
    * @return  Formatted event message string
    */
   static public String formatEventStr(byte[] byteArray,int byteIndex) {

      String[] eStr  = new String[STR_PER_ENTRY];
      
      eStr = parseEventPkt(byteArray, byteIndex);
    
      //13:53:05.594 ERROR CPU=1 APPNAME=DM EVENT ID=37 DPR Housekeeping telemetry : bad APID (0)
      //TODO - Format time
      return    eStr[IDX_SECS] + "." + eStr[IDX_SUB_SECS] + " " + eStr[IDX_EVENT_TYP] + " EVENT CPU=" + eStr[IDX_PROC_ID] +
                " APPNAME="+eStr[IDX_APP_NAME]+" ID=" + eStr[IDX_EVENT_ID] + ", " + eStr[IDX_MSG_TEXT];  
      //return    "HH:MM:SS.sss " + eStr[IDX_EVENT_TYP] + " EVENT CPU=" + eStr[IDX_PROC_ID] +
      //        " APPNAME="+eStr[IDX_APP_NAME]+" ID=" + eStr[IDX_EVENT_ID] + ", " + eStr[IDX_MSG_TEXT];  
      
   } // formatEventStr

   static public String getEventTypeString(int eventType) {
   
      String typeStr = CARE.STR_UNDEFINED;
      
      switch (eventType) {
      case 1:
         typeStr = "DEBUG";
         break;
      case 2:
         typeStr = "INFO";
         break;
      case 3:
         typeStr = "ERROR";
         break;
      case 4:
         typeStr = "CRITICAL";
         break;
      } // Switch
      
      return typeStr;
      
   } // End getEventTypeString()

   /**
    * 
    * Parse an event packet data structure. This same structure is used for telemetry and for recording
    * in on-board files.  Only time is parsed in the header.
    * 
    * typedef struct {
    *    uint8                     TlmHeader[CFE_SB_TLM_HDR_SIZE];
    *    CFE_EVS_PacketID_t        PacketID;   /**< \brief Event packet information
    *    typedef struct {
    *       char    AppName[OS_MAX_API_NAME];  /**< \cfetlmmnemonic \EVS_APPNAME
    *                                               \brief Application name
    *       uint16  EventID;                   /**< \cfetlmmnemonic \EVS_EVENTID
    *                                               \brief Numerical event identifier
    *       uint16  EventType;                 /**< \cfetlmmnemonic \EVS_EVENTTYPE
    *                                               \brief Numerical event type identifier
    *       uint32  SpacecraftID;              /**< \cfetlmmnemonic \EVS_SCID
    *                                               \brief Spacecraft identifier
    *       uint32  ProcessorID;               /**< \cfetlmmnemonic \EVS_PROCESSORID
    *                                               \brief Numerical processor identifier
    *
    *    } CFE_EVS_PacketID_t;
    *    char                      Message[CFE_EVS_MAX_MESSAGE_LENGTH];   /**< \cfetlmmnemonic \EVS_EVENT
    *                                                                          \brief Event message string
    *    uint8                     Spare1;                                /**< \cfetlmmnemonic \EVS_SPARE1
    *                                                                          \brief Structure padding
    *    uint8                     Spare2;                                /**< \cfetlmmnemonic \EVS_SPARE2
    *                                                                          \brief Structure padding
    * } CFE_EVS_Packet_t;
    *
    * @param byteArray  Array of bytes containing an event message structure starting at the byteIndex
    * @param byteIndex  Index into byte array to the start of the packet
    * @return           Array of strings (without '\n')containing the event message information. 
    */
   static public String[] parseEventPkt(byte[] byteArray, int byteIndex) {

      String[] entryStringArray = new String[STR_PER_ENTRY];
      
      // TODO - Lookup event type and create a string
      entryStringArray[IDX_SECS]      = String.format("%08X",DataPntInt.Uint32ToInt(byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_HDR)); 
      entryStringArray[IDX_SUB_SECS]  = String.format("%04X",DataPntInt.Uint16ToInt(byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_HDR+4));
      entryStringArray[IDX_APP_NAME]  = (new String (byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA,CARE.CFE_OS_MAX_API_NAME)).trim();
      entryStringArray[IDX_EVENT_ID]  = DataPntStr.Uint16ToStr(byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME); 
      //entryStringArray[IDX_EVENT_TYP] = TlmPntStr.Uint16ToStr(byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME+2); 
      entryStringArray[IDX_EVENT_TYP] = getEventTypeString(byteArray[byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME+2]);
      entryStringArray[IDX_SCRAFT_ID] = DataPntStr.Uint32ToStr(byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME+4); 
      entryStringArray[IDX_PROC_ID]   = DataPntStr.Uint32ToStr(byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME+8);
      entryStringArray[IDX_MSG_TEXT]  = (new String (byteArray, byteIndex+CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME+12, CARE.CFE_EVS_MAX_MESSAGE_LENGTH)).trim();  
          
      return entryStringArray;
          
   } // End parseEventPkt()
   
   static public EventType getEventType(CcsdsTlmPkt ccsdsTlmPkt) {

      EventType eventType = EventType.UNDEF;
      byte[] byteArray = ccsdsTlmPkt.getByteArray();
      
     /*
      *  #define CFE_EVS_DEBUG_BIT               0x0001
      *  #define CFE_EVS_INFORMATION_BIT         0x0002
      *  #define CFE_EVS_ERROR_BIT               0x0004
      *  #define CFE_EVS_CRITICAL_BIT            0x0008
      */
      
      switch (byteArray[CcsdsTlmPkt.CCSDS_IDX_TLM_DATA+CARE.CFE_OS_MAX_API_NAME+2]) {
         case 1:
            eventType = EventType.DEBUG;
            break;
         case 2:
            eventType = EventType.INFORMATION;
            break;
         case 4:
            eventType = EventType.ERROR;
            break;
         case 8:
            eventType = EventType.CRITICAL;
            break;
         } // Switch
         
         return eventType;
         
      } // End getEventType()
       
} // End class EvsApp
