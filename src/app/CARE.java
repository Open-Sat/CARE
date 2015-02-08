package app;

/**
 * CARE.java
 *
 * Define global constants
 * 
 * All classes should use these definitions to reduce coupling to a single point.
 * XML elements and attribute names should be defined here.
 *
 *  Some naming conventions:
 *  1. Write = Write information to a file
 *  2. Send  = Send information in a telemetry packet
 *  3. cFE default file names are log and dat and the distinction is not clear. Table dump files are .dmp. No binary vs text convention.
 *  4. The term 'core' has been used to go beyond the cFE and include items the user should not have to install. For example CI and TO lab.  
 *  
 *  @author David McComas
 */

public class CARE {
   
   /*****************************************************************************
    ** 
    ** Application 
    */ 
   static final public String APP_NAME = "care";

   static final public String PATH_CWD = System.getProperty("user.dir");
   static final public String PATH_SEP = System.getProperty("file.separator"); 
   static final public String PATH_DB_APPS    = "db-apps";
   static final public String PATH_DB_CORE    = "db-core";
   static final public String PATH_CONFIG     = "config";
   static final public String PATH_IMAGES     = "images";
   static final public String PATH_OBJ_LIB    = "lib";
   static final public String PATH_TEMPLATES  = "templates";
   
   static final public int MAX_OPEN_TLM_PAGES = 20;
   static final public int MAX_USER_APPS      = 20;
   
   /*****************************************************************************
    ** 
    ** Application property file
    */ 
   static final public String PROP_APP_FILE         = PATH_CONFIG+PATH_SEP+"care-properties.xml";
   static final public String PROP_APP_VER          = "version";
   static final public String PROP_APP_HELP         = "app-help";
   static final public String PROP_APP_TIME_TICK    = "time-tick-ms";
   static final public String PROP_APP_CFE_HELP     = "cfe-help";
   static final public String PROP_APP_CFE_FILE     = "cfe-file";
   static final public String PROP_APP_CARE_IP_ADDR = "care-ip-address";
   static final public String PROP_APP_CFS_IP_ADDR  = "cfs-ip-address";
   static final public String PROP_APP_CFS_CMD_PORT = "cfs-cmd-port";
   static final public String PROP_APP_CFS_TLM_PORT = "cfs-tlm-port";
   static final public String PROP_APP_CORE_APPS    = "core-apps";
   static final public String PROP_APP_USER_APPS    = "user-apps";
   static final public String PROP_APP_CORE_TOOL_CMDS = "core-tool-cmds";
   static final public String PROP_APP_LUA_STARTUP  = "lua-startup";
   
   /*****************************************************************************
    ** 
    ** Message ID property file
    */ 
   static final public String PROP_MID_FILE = PATH_CONFIG+PATH_SEP+"care-msg-ids.xml";

   /*****************************************************************************
    ** 
    ** cFE configuration parameter property file
    */ 
   static final public String PROP_CFE_FILE  = PATH_CONFIG+PATH_SEP+"care-cfe-config.xml";

   /*****************************************************************************
    ** 
    ** Application General Constants
    */ 

   static final public int    MAX_MSG_ID_CNT  = 4096; // Allow for 12-bit cFE message ID
   static final public int    UNUSED_MSG_ID   = MAX_MSG_ID_CNT + 1; 
   static final public String STR_GUI_SEP     = "-";
   static final public String STR_UNDEFINED   = "UNDEF";
   static final public String STR_UNINIT_VAL  = "UNINITIALIZED";
   static final public String STR_UNINIT_TIME = "YYYY-DOY-HH:MM:ss.mmm";
   
   // Strings used in logging to identify the source of a message. Keep to 3 characters for formatting
   static final public String LOG_FSW     = "FSW";  // Flight Software
   static final public String LOG_USER    = "USR";  // User input
   static final public String LOG_CONSOLE = "GND";  // Ground system

   // Menu strings
   static final public String MENU_COMMANDS   = "Commands";
   static final public String MENU_TABLES     = "Tables";
   static final public String MENU_TELEMTRY   = "Telemetry";
   static final public String MENU_CORE_APPS  = "Core Apps";
   static final public String MENU_CORE_FILES = "Core Files";
   static final public String MENU_USER_APPS  = "User Apps";

   /*****************************************************************************
    * 
    * cFE Configuration Parameters
    * The naming convention for non-cFE constants is to use the exact FSW name 
    * with a "CFE_" prefix
    * 
    * TODO - cFE config parameters should eventually be defined in a startup property or XML file 
    * TODO - Also need to resolve coupling to XML DB
    * TODO - CFE_DEF_FILE_PATH assumes cygwin access & is coupled with DB definitions and cFE file system mappings
    */ 
   
   static final public String CFE_DEF_FILE_PATH = "\\cygwin\\home\\dcmccom2\\opensat\\tmp\\";
   
   static final public int CFE_OS_MAX_TASKS    = 64;
   static final public int CFE_OS_MAX_API_NAME = 20;
   static final public int CFE_OS_MAX_PATH_LEN = 64;  // TODO - Search on 64 and replace
   
   /** ES maximum of a CDS definitions */
   static final public int CFE_ES_CDS_MAX_NUM_ENTRIES = 512;
   /** ES maximum length of a CDS name */
   static final public int CFE_ES_CDS_MAX_NAME_LENGTH = 16;
   /** ES maximum length of a total CDS name including application name */
   static final public int CFE_ES_CDS_MAX_FULL_NAME_LEN = (CFE_ES_CDS_MAX_NAME_LENGTH + CFE_OS_MAX_API_NAME + 2);

   /** ES System Log file size */
   static final public int CFE_ES_SYSTEM_LOG_SIZE  = 2048;
   /** ES maximum number of applications */
   static final public int CFE_ES_MAX_APPLICATIONS = 32;
   /** ES maximum exception-reset log file entries */
   static final public int CFE_ES_ER_LOG_ENTRIES = 20;
   
   
   /** EVS maximum length of event message */
   static final public int CFE_EVS_MAX_MESSAGE_LENGTH = 122;
   /** EVS maximum events in local log */
   static final public int CFE_EVS_LOG_MAX = 20;
   /** EVS maximum events filters per app */
   static final public int CFE_EVS_MAX_EVENT_FILTERS = 8;
   
   /** TBL maximum length of a table name */
   static final public int CFE_TBL_MAX_NAME_LENGTH   = 16;
   /** TBL maximum length of a total table name including application name */
   static final public int CFE_TBL_MAX_FULL_NAME_LEN = (CFE_TBL_MAX_NAME_LENGTH + CFE_OS_MAX_API_NAME + 2);
   /** TBL maximum number of tables */
   static final public int CFE_TBL_MAX_NUM_TABLES = 128;
   
   /** SB maximum message ID */
   static final public int CFE_SB_HIGHEST_VALID_MSGID = 0x1FFF;
   /** SB maximum message ID */
   static final public int CFE_SB_MAX_PIPES = 64;

   /*****************************************************************************
   ** 
   ** Common XML attributes and elements
   */

   // - Common Elements

   static final public String XML_EL_NAME     = "Name";
   static final public String XML_EL_PREFIX   = "Prefix";
   static final public String XML_EL_DESC     = "Description";
   static final public String XML_EL_DIR      = "Directory";
   static final public String XML_EL_MSG_ID   = "MsgId";
   static final public String XML_EL_TYPE     = "Type";
   static final public String XML_EL_LEN      = "Len";
   static final public String XML_EL_DEF      = "Default";
   static final public String XML_EL_DATA_PNT = "DataPnt";
   static final public String XML_EL_CDATA    = "CData";
   
   static final public String XML_AT_TYPE   = "type";
   static final public String XML_AT_LEN    = "len";

   // - Commands
   static final public String XML_EL_CMDS          = "Commands";
   static final public String XML_EL_CMD_PKT       = "CmdPacket";
   static final public String XML_EL_CMD_FC        = "FuncCode";
   static final public String XML_EL_CMD_PARAM     = "CmdParam";
   static final public String XML_EL_CMD_PARAM_LEN = "TotCmdParamLen";
   
   // - Tables
   static final public String XML_EL_TBLS      = "Tables";
   static final public String XML_EL_TBL       = "Table";

   // - Telemetry
   static final public String XML_EL_TLM       = "Telemetry";
   static final public String XML_EL_TLM_PKT   = "TlmPacket";

   
   // - Files
   static final public String XML_EL_CFE_FILES = "cFE-Files";

   // - Values: String used in XML that are referenced in Java code 
   static final public String XML_VAL_APP_ES     = "ES";
   static final public String XML_VAL_APP_SB     = "SB";
   static final public String XML_VAL_APP_EVS    = "EVS";
   static final public String XML_VAL_APP_TBL    = "TBL";
   static final public String XML_VAL_APP_TIME   = "TIME";
   static final public String XML_VAL_APP_LABCMD = "LABCMD";
   static final public String XML_VAL_APP_LABTLM = "LABTLM";
   static final public String XML_VAL_APP_LABSCH = "LABSCH";

   static final public String XML_VAL_FLT     = "Float";
   static final public String XML_VAL_INT     = "Integer";
   static final public String XML_VAL_STR     = "String";
   static final public String XML_VAL_UINT    = "Uint";     // Length attribute used to determine byte length
   
} // End class CARE
