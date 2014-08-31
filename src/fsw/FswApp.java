package fsw;

import java.util.ArrayList;      
import java.util.HashMap;   
import java.util.Iterator;
import java.util.Map;   

import org.apache.log4j.Logger;

import ccsds.*;

/**
 * Base class to represent/model FSW applications. The constants defined in
 * this class should be used for any assumptions made about apps such as
 * maximum number of commands.   
 *
 * <p>Notes:
 * <ul>
 * <li> Name Convention: Start all command and telemetry related constants with
 *      "CMD_" and "TLM_". Use second field to ID category like function code
 *      (FC) or message ID (MID). For example "CMD_FC_" and "CMD_MID_"</li>
 * <li> Use ArrayList for speed because it does not allocate a new array on the heap.</li> 
 * </ul>
 * 
 * TODO - Should error message methods be used? Allows app prefix etc. to be used.
 *
 * @author David McComas
 */
public abstract class FswApp {

   private static Logger logger=Logger.getLogger(FswApp.class);

   /** Maximum number of commands per application */
   public static final int CMD_MAX       = 32;
   /** Maximum number of parameters per command */
   public static final int CMD_MAX_PARAM = 10;
   
   /** Maximum number of telemetry packets per application */ 
   public static final int TLM_MAX = 10;
   
   /** Maximum number of tables per application */ 
   public static final int TBL_MAX = 10;

   /** Maximum number of telemetry points per packet */ 
   public static final int TLM_MAX_POINTS = 4046;

   /** Value used for a null command value. Typically  used for functions codes but could be any default uninitialized value */
   public static final int    NULL_CMD_INT = 99;

   public static final String NULL_APP_STR = "Null";
   public static final String NULL_CMD_STR = "Null Command";
   
   public static final String TLM_STR_TBD = "Telemetry message string method not implemented\n";
   public static final String TLM_STR_ERR = "Invalid telemetry message ID for this application";

   /** Abbreviated application name (Upper case without underscore) */
   protected String Prefix;
   
   /** Full application name (Mixed case) */
   protected String Name;
   
   protected ArrayList<CmdPkt> cmdList;
   
   protected Map<Integer,TlmPkt> tlmPktMap = new HashMap<Integer, TlmPkt>(TLM_MAX);
   
   protected Map<String,Table> tableMap = new HashMap<String, Table>(TBL_MAX);

   /** Array of strings with each entry containing a string representing a telemetry data point value. */
   protected String[]  tlmStrArray;

   /**
    * Construct a FSW application. Abstract commands and telemetry definition 
    * methods are called during construction. If these functions are empty 
    * (i.e. packets definitions not known during at time of construction) then
    * packets can be added later.
    *
    * @param Prefix  Upper application abbreviation (no underscore)
    * @param Name    Mixed case description name
    */
   protected FswApp (String Prefix, String Name) {
      
      int i;
      CmdPkt NullCmd = new CmdPkt (NULL_APP_STR, NULL_CMD_STR, NULL_CMD_INT, NULL_CMD_INT, 0);

      this.Prefix = Prefix;
      this.Name   = Name;

      cmdList = new ArrayList<CmdPkt>(CMD_MAX);
      tlmStrArray = new String[TLM_MAX_POINTS];
      
      for (i=0; i < CMD_MAX; i++)
         cmdList.add(NullCmd);
      
      logger.trace("FswApp: Name = " + Name +", Prefix = " + Prefix);
      logger.trace("FswApp:CmdList size = " + cmdList.size());
     
      defineCmds();
      defineTlm();
      
      logger.trace("FswApp: Created " + Name);
      		
   } // End FswApp

   /**
    *  Method called during construction to define command packets. 
    */
   protected abstract void defineCmds();
   
   /**
    *  Method called during construction to define telemetry packets. 
    */
   protected abstract void defineTlm();

   /**
    * This method can be as simple or complex as a child class wants to make it. I 
    * thought about putting a MsgID Hashtable in this base class that could be used
    * to look up the method to generate a string (eventually a table for a GUI) but this
    * may be overkill for some applications. Therefore I left it up to the child class
    * to provide all of the implementation details (if-then, switch, etc.). 
    *
    * There are some constant strings that should be used for reporting information. 
    */
   public abstract String getTlmStr(CcsdsTlmPkt TlmMsg);
  
   /**
    * This method is intended to simplify the needs of a GUI. The application provides
    * a function that parses the packet and creates a string for each telemetry packet
    * data point.
    * 
    */
   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) {
      
      int index = DataPntStr.HDR_LEN;  // Start at index past the header 

      logger.trace("FswApp: getTlmStrArray called for " + Integer.toHexString(TlmMsg.getStreamId()));
      DataPntStr.hdrBytesToStr(tlmStrArray, TlmMsg);
      
      TlmPkt tlmPkt = tlmPktMap.get(TlmMsg.getStreamId());
      if (tlmPkt != null) {
         
         logger.trace("getTlmStrArray: dataPnt list length = " + tlmPkt.getDataPntList().size());
         ArrayList<DataPnt> dataPntList = tlmPkt.getDataPntList();
         Iterator<DataPnt> dataPntIt = dataPntList.iterator();
         while ( dataPntIt.hasNext() ) {
            DataPnt dataPnt = dataPntIt.next();
            //logger.trace("getTlmStrArray: " + tlmPnt.getName() + " = " + tlmPnt.getStrObj().byteToStr(TlmMsg.getPacket(), tlmPnt.getByteOffset()));
            tlmStrArray[index] = dataPnt.getStrObj().byteToStr(TlmMsg.getByteArray(), dataPnt.getByteOffset());
            index ++;
         }
         logger.trace("getTlmStrArray:  dataPnt list index after loop = " + index);

      } // End if found packet
      else {
         logger.error("EsApp: Null HashMap entry for MsgId " + Integer.toHexString(TlmMsg.getStreamId()));
         return null;
      }
      
      return tlmStrArray;
      
   } // getTlmStrArray()

   /**
    * 
    * @return
    */
   public ArrayList<CmdPkt> getCmdList() {
      
      return cmdList;
      
   } // getCmdList()

   /**
    * Return the CmdPkt for the function code.
    * 
    * @param funcCode Command function code
    * @return         CmdPkt or null if no command defined for the function code
    */
   public CmdPkt getCmdPkt(int funcCode) {
      
      CmdPkt  cmdPkt = null;

      Iterator<CmdPkt> cmdPktIt = cmdList.iterator();
      while ( cmdPktIt.hasNext() ) {
         CmdPkt cmdPktTmp = cmdPktIt.next();
         if (cmdPktTmp != null) {
            if (cmdPktTmp.getFuncCode() == funcCode) {
               cmdPkt = cmdPktTmp;
               break;
            }
         }
      } // End CmdPkt loop

      return cmdPkt;
      
   } // getCmdPkt()

   /**
    * Typically the message ID is set when the command packet is created and
    * added to the command list. However if the message ID is not known during
    * creation then this allows it to be added to all of the command packets.
    * This assumes one message ID is used for all of an app's commands. 
    * 
    * @param msgId
    * @return
    */
   public void setCmdPktMsgId(int msgId) {
      
	      Iterator<CmdPkt> cmdPktIt = cmdList.iterator();
	      while ( cmdPktIt.hasNext() ) {
	         CmdPkt cmdPktTmp = cmdPktIt.next();
	         if (cmdPktTmp != null) {
	            cmdPktTmp.getCcsdsPkt().setStreamId(msgId);
	            cmdPktTmp.getCcsdsPkt().ComputeChecksum();
	         }
	      } // End CmdPkt loop
      
   } // setCmdPktMsgId()

   /**
    * 
    * @param msgId
    * @return
    */
   public TlmPkt getTlmPkt(int msgId) {
      
      return tlmPktMap.get(msgId);
      
   } // getTlmPkt()

   /**
    * Typically tlm packets are added when the app is created. However if
    * message IDs are not known during creation then this allows the 
    * packet to be added when the IDs are assigned/resolved.
    * 
    * @param msgId
    * @return
    */
   public void addTlmPkt(int tlmMsgId, TlmPkt tlmPkt) {
      
      tlmPktMap.put(tlmMsgId, tlmPkt);
      
   } // addTlmPkt()

   /**
    * 
    * @param msgId
    * @return
    */
   public Table getTable(String name) {
      
      return tableMap.get(name);
      
   } // getTable()


   /**
    * 
    * @param Prefix
    */
   public void setPrefix(String Prefix) {
      
      this.Prefix = Prefix;
      
   } // setPrefix()

   /**
    * 
    * @param FuncCode
    * @param cmdPkt
    */
   public void setCmdPkt(int FuncCode, CmdPkt cmdPkt) {
      
      cmdList.set(FuncCode, cmdPkt);
      
   } // setCmdPkt()

   /**
    * 
    * @return
    */
   public Map<Integer,TlmPkt> getTlmPktMap() {
      
      return tlmPktMap;
      
   } // getTlmPktMap()


   /**
    * 
    * @return
    */
   public Map<String,Table> getTableMap() {
      
      return tableMap;
      
   } // getTableMap()

   /**
    * 
    * @return
    */
   public String getPrefix() {
      
      return Prefix;
      
   } // getPrefix()

   /**
    * 
    * @return
    */
   public String getName() {
      
      return Name;
      
   } // getName()

} // End class FswApp
