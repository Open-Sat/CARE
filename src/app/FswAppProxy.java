package app;

import java.util.ArrayList;            
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.*;

import fsw.*;
import fswcore.CfeTblFile;
import fswcore.CfeTblTxtFile;
import gui.CmdParamDialog;
import gui.TlmDialog;
import gui.TblFileDialog;
import gui.TblTxtFileDialog;
import gui.CfeFileDialog;
import util.*;


/**
 * Instantiates a FSW application and provides a reference/representation of a 
 * FSW application to the console. Architecturally it serves as a delegate 
 * between the FSWApp 'model', the XML database, and the GUI.  This allows 
 * the FswApp class to be decoupled from the XML and GUI.   
 * 
 * @author David McComas
 * @version 
 */
public class FswAppProxy {

   private static Logger logger=Logger.getLogger(FswAppProxy.class);

   private Shell          guiShell = null;
   private Document       Doc;
   private FswXmlApp      fswXmlApp;
   private FswCmdNetwork  cmdWriter;  // Reference to application's command writer object
   private String         coreFilePath;
   private boolean        verifyUndefMsgIds;
   
   // COMMANDS
   
   /**
    * Array of command names that can be used for GUIs. The index
    * does not necessarily correspond with App's Function Code 
    */
   private String[]     CmdNameList = new String[FswApp.CMD_MAX];
   private CmdAction[]  actionCmd   = new CmdAction[FswApp.CMD_MAX];
   private MenuManager  cmdMenu; 

   // TABLES
   
   /**
    *  Array of table names that can be used for GUIs. The index
    *  is from 0..N that is in the order of packets defined in the XML 
    */
   private String[]       tableNameList = new String[FswApp.TBL_MAX];
   private TblAction[]    actionTbl     = new TblAction[FswApp.TBL_MAX];
   private MenuManager    tblMenu; 

   // TELEMETRY
   
   /**
    *  Array of telemetry packet names that can be used for GUIs. The index
    *  is from 0..N that is in the order of packets defined in the XML 
    */
   private String[]       tlmMsgIdNameList = new String[FswApp.TLM_MAX];
   private TlmAction[]    actionTlm        = new TlmAction[FswApp.TLM_MAX]; 
   private MenuManager    tlmMenu; 
   private TlmPageManager tlmPageManager; 
   private ConcurrentLinkedQueue<StatusMsg> proxyStatusMsgQ;
   
   /**
    * Construct a FSW Application Proxy
    * 
    * @param fileName         XML file that defines the application ground interface
    * @param tlmPageManager   Instance of CARE's telemetry page manager
    * @param midConfig        User defined Message IDs
    * @param proxyStatusMsgQ  CARE's status message queue
    */
   public FswAppProxy (String fileName, TlmPageManager tlmPageManager, Properties midConfig, ConcurrentLinkedQueue<StatusMsg> proxyStatusMsgQ) {
      
      this.tlmPageManager  = tlmPageManager;
      this.proxyStatusMsgQ = proxyStatusMsgQ;
      verifyUndefMsgIds = true;
      
      try {
         
         // Open the file
         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         Doc = documentBuilder.parse(fileName);
         
      } catch (Exception e) {
        
         logger.error("FswAppProxy could not open file " + fileName);
         e.printStackTrace();

      } // End try block
      
      fswXmlApp = new FswXmlApp(Doc, midConfig);
      
      fswXmlApp.defineCmdsFromXml();
      createCmdMenu();

      fswXmlApp.defineTlmFromXml();
      createTlmMenu();
      
      fswXmlApp.defineTblsFromXml();
      createTblMenu();

   } // End FswAppProxy()

   /**
    * FSW Application defined by the XML
    * 
    * @return FSW Application defined by the XML
    */
   public FswApp getFswXmlApp() {
      
      return fswXmlApp;
      
   } // End getFswApp()
   
   public String[] getCmdNameList() {
      
      return CmdNameList;
      
   } // End getCmdNameList()

   public String[] getTlmMsgIdNameList() {
      
      return tlmMsgIdNameList;
      
   } // End getTlmMsgIdNameList()

   public MenuManager getCmdMenu() {
      
      return cmdMenu;
      
   } // End getCmdMenu()

   public MenuManager getTblMenu() {
      
      return tblMenu;
      
   } // End getTblMenu()

   public MenuManager getTlmMenu() {
      
      return tlmMenu;
      
   } // End getTlmMenu()

   // Get the CmdAction object that corresponds to the caller supplied function code 
   public  CmdAction getCmdAction(int FuncCode) {
      
      CmdAction cmdAction = null;
      
      logger.trace("getCmdAction() for " + fswXmlApp.getName() + ", actionCmd.length = " + actionCmd.length);
      for (int i=0; i < actionCmd.length; i++) {
         
         // actonCmd is a sparse array so don't break on a null entry
         if (actionCmd[i] != null) {  
            logger.trace("getCmdAction() Index = " + i + ", FC = "+ actionCmd[i].getFuncCode());
            if (actionCmd[i].getFuncCode() == FuncCode) {
               cmdAction = actionCmd[i];
               break;
            }
         }
      } // End actionCmd loop
      
      return cmdAction;
      
   } // End getCmdAction()

   /**
    * Set the GUI shell and networked command writer references. These can't be
    * set when the class is constructed because the shell and/or network 
    * connection may not have been established when the class is constructed.  
    * 
    * @param guiShell    GUI display shell
    * @param cmdWriter   Network command writer used to send commands to the cFE
    */
   public void setAppReferences(Shell guiShell, FswCmdNetwork  cmdWriter, String coreFilePath) {
      
      this.guiShell     = guiShell;
      this.cmdWriter    = cmdWriter;
      this.coreFilePath = coreFilePath;

      if (guiShell != null) {
         // Only attempt once after GUI set
         if (verifyUndefMsgIds) {
        	 verifyUndefMsgIds = false;
             if (!fswXmlApp.getUndefMsgIdList().isEmpty()) { 
                
            	 Composite composite = new Composite(guiShell.getShell(), SWT.NONE);
                 composite.setLayout(new GridLayout(1,false));
                 final Label label = new Label(composite, SWT.NONE);
                 label.setText("0000");
    	 
                 Iterator<FswXmlApp.UndefinedMsgId> undefMsgIdIt = fswXmlApp.getUndefMsgIdList().iterator();
                 while ( undefMsgIdIt.hasNext() ) {
                	FswXmlApp.UndefinedMsgId undefinedMsg = undefMsgIdIt.next();
                    if (undefinedMsg.isTlm()) {
                    	InputDialog dlg = new InputDialog(guiShell.getShell(),"","Input hex message ID (4 digits) value for telemetry message " +
                                              undefinedMsg.getMsg(), label.getText(), new LengthValidator());
                        if (dlg.open() == Window.OK) {
                        	int msgId = Integer.parseInt(dlg.getValue(),16);
                        	undefinedMsg.getTlmPkt().setMsgId(msgId);
                        	fswXmlApp.addTlmPkt(msgId, undefinedMsg.getTlmPkt());
                        	createTlmMenu();
                        }
                    } // End if undefined tlm message ID 
                    else {
                    	InputDialog dlg = new InputDialog(guiShell.getShell(),"","Input hex message ID (4 digits) value for command message " +
                                undefinedMsg.getMsg(), label.getText(), new LengthValidator());
                        if (dlg.open() == Window.OK) {
                           fswXmlApp.setCmdPktMsgId (Integer.parseInt(dlg.getValue(),16));
                        }
                    } // End if undefined tlm message ID 
                 } // End 
             }// End if undefined messages
         } // End if should verifyUndefMsgIds
      } // End if null guiShell      

   } // End setAppReferences()

   /**
    * Subclass SWT Action class to create a command specific action for each
    * application command.
    *
    */
   public class CmdAction extends Action {
      
      int funcCode;

      /**
       * Construct a command action.
       * 
       * @param cmdName  string containing the name of the command
       * @param FuncCode integer defining the function code
       */
      CmdAction (String cmdName, int FuncCode) {
         
         super();
         setText(cmdName);
         funcCode = FuncCode;      
         setToolTipText("Send " + fswXmlApp.getName() + " command function code " + funcCode);
         
         
      } // End CmdAction
      
      /**
       * Execute the action for the user selected command.
       */
      public void run() {

         logger.trace("Called " + fswXmlApp.getName() + "'s command " + getText() + ", FC=" + funcCode);
         StatusMsg statusMsg = new StatusMsg(StatusMsg.Type.ERROR, "Command cancelled");
         boolean sendCmd = true;
         
         CmdPkt cmdPkt = fswXmlApp.getCmdList().get(funcCode);
            
         if ((guiShell != null) && (cmdPkt != null)) {
         
            if (cmdPkt.hasParam()) {
               CmdParamDialog cmdParamDialog = new CmdParamDialog(guiShell);
               sendCmd = cmdParamDialog.open(cmdPkt);
               cmdPkt.loadParamList();
            }
	        if (cmdWriter != null) {
	           if (sendCmd) {
	              cmdWriter.sendCmd(cmdPkt.getCcsdsPkt());
	              statusMsg.setType(StatusMsg.Type.INFO);
	              statusMsg.setText("Sent " + fswXmlApp.getName() + " command " + cmdPkt.getName());  
               } // End if sendCmd
	        }
	        else {
	           statusMsg.setText("Error sending " + fswXmlApp.getName() + " command. Verify connection to CFS."); 
	        }
         }// End if cmdPkt != null
         else {
            logger.error("GUI shell or command packet null pointer or GUI ");
            if (guiShell == null)
               statusMsg.setText("Error sending " + fswXmlApp.getName() + " command. Verify connection to CFS.");
            if (cmdPkt == null)
               statusMsg.setText("Error sending " + fswXmlApp.getName() + " command. Command not retrieved from the database. Verify application's XML definitions.");
         } // End if guiShell != null

         proxyStatusMsgQ.add(statusMsg);
         
      } // End run()
      
      /**
       * Return the command function code.
       * 
       * @return integer containing the command's function code 
       */
      public int getFuncCode() {
         
         return funcCode;
         
      } // getFuncCode()
      
   } // End class CmdAction
   
   /**
    * Create a menu containing the application's commands.
    */
   private void createCmdMenu() {
   
      ArrayList<CmdPkt> CmdPktList = fswXmlApp.getCmdList();
      
      cmdMenu = new MenuManager(CARE.MENU_COMMANDS);
      
      logger.trace("FswAppRef::createCmdNameList():");     
      for (int i=0; i < CmdPktList.size(); i++) {
         if (CmdPktList.get(i).getCcsdsPkt().getFuncCode() != FswApp.NULL_CMD_INT) {
            CmdNameList[i] = CmdPktList.get(i).getName();
            logger.trace("   Cmd[" + i + "] = " + CmdNameList[i]);
            actionCmd[i] = new CmdAction(CmdPktList.get(i).getName(), CmdPktList.get(i).getCcsdsPkt().getFuncCode());
            cmdMenu.add(actionCmd[i]);
         }
      } // End CmdPkt loop
      
   } // End createCmdMenu()

   /**
    * Subclass SWT Action class to create a telemetry specific action for each
    * application telemetry packet.
    *
    */
   public class TlmAction extends Action {
       
      String  name;
      int     msgId;
      int     pointCnt;  // Number of telemetry points in the packet
      
      /***
       * Construct a telemetry packet action.
       * 
       * @param name     string containing the telemetry packet name
       * @param msgId    integer defining the message identifier
       * @param pointCnt number of telemetry points in the packet
       */
      TlmAction (String name, int msgId, int pointCnt) {
          
          super();
          setText(name);
          this.name     = name;
          this.msgId    = msgId;
          this.pointCnt = pointCnt;
          setToolTipText("Display " + fswXmlApp.getName() + " telemetry packet " + name + 
                         ", ID = " + Integer.toHexString(msgId) + ", Point Count = " + pointCnt);
          
       } // End TlmAction
       
      /**
       * Execute the action for the user selected telemetry packet.
       */
       public void run() {

          logger.trace("Creating " + fswXmlApp.getName() + "'s " + getText() + "telemetry page with ID " + Integer.toHexString(msgId));
          StatusMsg statusMsg = new StatusMsg(StatusMsg.Type.ERROR, CARE.STR_UNDEFINED);
          
          TlmPkt tlmPkt = fswXmlApp.getTlmPkt(msgId);

          if (guiShell != null) {
             if (tlmPkt != null) {
                TlmDialog dlg = new TlmDialog(guiShell, fswXmlApp, tlmPkt, tlmPageManager);
                dlg.open();
                logger.trace("    Completed telemtry dialog open. Adding page to tlmPageManager");
                tlmPageManager.addPage(tlmPkt.getMsgId(), dlg);
                statusMsg.setType(StatusMsg.Type.INFO);
                statusMsg.setText("Opened " + fswXmlApp.getName() + " telmetry page " + tlmPkt.getName());  
             }// End if tlmPkt != null
             else {
                statusMsg.setText("Error opening " + fswXmlApp.getName() + " telemetry window. Telmetry packet not retrieved from the database. Verify application's XML definitions.");
             }
          } // End if guiShell != null
          else {
             statusMsg.setText("Error opening " + fswXmlApp.getName() + " telemetry window. Verify connection to CFS."); 
          }
          proxyStatusMsgQ.add(statusMsg);
                    
       } // End run()

       /**
        * Return the telemetry packet name.
        *  
        * @return  string containing the name of the telemetry packet.
        */
       public String getName() {
          
          return name;
          
       } // getName()
       
       /**
        * Return the telemetry packet message identifier.
        *  
        * @return  integer containing the telemetry packet message ID
        */
       public int getMsgId() {
          
          return msgId;
          
       } // getMsgId()
       
       /**
        * Return the number of telemetry points in the telemetry packet.
        *  
        * @return  integer containing the number of telemetry points in the packet
        */
       public int getPointCnt() {
          
          return pointCnt;
          
       } // getgetPointCnt()
       
    } // End class CmdAction

   /**
    * Create a menu containing the application's telemetry packets
    */
   private void createTlmMenu() {

      int i=0;
      Map<Integer,TlmPkt> tlmMap = fswXmlApp.getTlmPktMap();
      
      tlmMenu = new MenuManager(CARE.MENU_TELEMTRY);
      
      logger.trace("FswAppProxy::createTlmNameList():");     
      
      Iterator tlmMapIt = tlmMap.entrySet().iterator();
      while (tlmMapIt.hasNext()) {
         Map.Entry<Integer, TlmPkt> tlmPair = (Map.Entry)tlmMapIt.next();
         TlmPkt tlmPkt = tlmPair.getValue();
         if (tlmPkt != null) {
            // This string format is assumed by the dialog box parsers
            tlmMsgIdNameList[i] = "0x"+Integer.toHexString(tlmPkt.getMsgId()) + CARE.STR_GUI_SEP + fswXmlApp.getPrefix() + ": " + tlmPkt.getName();
            logger.trace("   Tlm[" + i + "] = " + tlmMsgIdNameList[i]);
            actionTlm[i] = new TlmAction(tlmPkt.getName(),tlmPkt.getMsgId(),tlmPkt.getPointCnt());
            tlmMenu.add(actionTlm[i]);
            i++;
         }
      } // End tlm map loop

   } // End createTlmMenu()

   
   /**
    * Subclass SWT Action class to create a table-specific action for each
    * application table.
    *
    */
   public class TblAction extends Action {
       
      String  name;
      int     pointCnt;  // Number of data elements in the table
      
      /***
       * Construct a table action.
       * 
       * @param name     string containing the table name
       * @param pointCnt number of data points in the table
       */
      TblAction (String name, int pointCnt) {
          
          super();
          setText(name);
          this.name     = name;
          this.pointCnt = pointCnt;
          setToolTipText("Display " + fswXmlApp.getName() + " table " + name + 
                         ", Point Count = " + pointCnt);
          
       } // End TblAction
       
      /**
       * Execute the action for the user selected table.
       */
       public void run() {

          logger.trace("Selected " + fswXmlApp.getName() + "'s table " + name);
          
          StatusMsg statusMsg = new StatusMsg(StatusMsg.Type.ERROR, CARE.STR_UNDEFINED);
          fsw.Table table = fswXmlApp.getTable(name);
             
          if (guiShell != null) {
             if (table != null) {
                FileDialog fileDialog = new FileDialog(guiShell, SWT.OPEN);
                fileDialog.setFilterNames(new String[] {"Table dump text files (*.txt)", "Table dump files (*.dmp)"});
                fileDialog.setFilterExtensions(new String[] {"*.txt","*.dmp"});
                fileDialog.setFilterPath(coreFilePath);
                
                String tblFile = fileDialog.open();
                if (tblFile != null) {
                   if (table.getPointCnt() > 0) {
                      CfeTblFile cfeTblFile = new CfeTblFile(table); 
                      TblFileDialog tblFileDialog = new TblFileDialog(guiShell, cfeTblFile, table);
                      logger.trace("Before table file dialog open.");
                      tblFileDialog.open(tblFile);
                      logger.trace("Completed table file dialog open.");
                      statusMsg.setType(StatusMsg.Type.INFO);
                      statusMsg.setText("Opened " + fswXmlApp.getName() + " table " + table.getName());
                   }
                   else {
                      CfeTblTxtFile cfeTblFile = new CfeTblTxtFile();
                      if (cfeTblFile.formatDataStrings(tblFile)) {
	                      //TblTxtFileDialog tblFileDialog = new TblTxtFileDialog(guiShell, cfeTblFile);
	                      CfeFileDialog cfeFileDialog = new CfeFileDialog(guiShell, cfeTblFile);
	                      logger.trace("Before cFE file (text table file) dialog open.");
	                      //tblFileDialog.open();
	                      cfeFileDialog.open();
	                      logger.trace("Completed cFE file (text table file) dialog open.");
	                      statusMsg.setType(StatusMsg.Type.INFO);
	                      statusMsg.setText("Opened " + fswXmlApp.getName() + " table " + table.getName());
                      }
                      else {
	                      statusMsg.setType(StatusMsg.Type.ERROR);
	                      statusMsg.setText("Error opening table file " + tblFile + ".The directory for cFE files may be incorrect or the file may not have been transferred.");
                      }
                   }
                      
                } // End if tblFile opened
                else {
                   statusMsg.setText("Error opening " + fswXmlApp.getName() + " table file. Verify table file exists.");
                } // End if tblFile != null
                
             }// End if table != null
             else {
                statusMsg.setText("Error opening " + fswXmlApp.getName() + " table window. Table not retrieved from the database. Verify application's XML definitions.");
             }
          } // End if guiShell != null
          else {
             statusMsg.setText("Error opening " + fswXmlApp.getName() + " table window. Verify connection to CFS."); 
          }
          proxyStatusMsgQ.add(statusMsg);
                    
       } // End run()

       
       /**
        * Return the table name.
        *  
        * @return  string containing the name of the table.
        */
       public String getName() {
          
          return name;
          
       } // getName()
       
       /**
        * Return the number of data points in the table.
        *  
        * @return  integer containing the number of table data points
        */
       public int getPointCnt() {
          
          return pointCnt;
          
       } // getgetPointCnt()
       
    } // End class TblAction

   /**
    * Create a menu containing the application's tables
    */
   private void createTblMenu() {

      int i=0;
      Map<String,fsw.Table> tableMap = fswXmlApp.getTableMap();
      
      tblMenu = new MenuManager(CARE.MENU_TABLES);

      logger.trace("FswAppProxy::createTableNameList():");     
      
      Iterator tableMapIt = tableMap.entrySet().iterator();
      while (tableMapIt.hasNext()) {
         Map.Entry<String, fsw.Table> tablePair = (Map.Entry)tableMapIt.next();
         fsw.Table table = tablePair.getValue();
         if (table != null) {
            // This string format is assumed by the dialog box parsers
            tableNameList[i] = table.getName();
            logger.trace("   Table[" + i + "] = " + tableNameList[i]);
            actionTbl[i] = new TblAction(table.getName(),table.getPointCnt());
            tblMenu.add(actionTbl[i]);
         }
      } // End table map loop

   } // End createTableMenu()

   /**
    * This class validates a String. It makes sure that the String is between 5 and 8
    * characters
    */
   class LengthValidator implements IInputValidator {
     /**
      * Validates the String. Returns null for no error, or an error message
      * 
      * @param newText the String to validate
      * @return String
      */
     public String isValid(String newText) {
       int len = newText.length();

       // Determine if input is too short or too long
       if (len < 4) return "Too short";
       if (len > 4) return "Too long";

       // Input must be OK
       return null;
     }
   }
} // End Class FswAppProxy

