package app;


import java.util.ArrayList;       
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;

import util.StatusMsg;

import fsw.*;
import fswcore.CfeFile;
import fswcore.EsApp;
import fswcore.EsAppLogFile;
import fswcore.EsCdsLogFile;
import fswcore.EsErLogFile;
import fswcore.EsSysLogFile;
import fswcore.EsTaskLogFile;
import fswcore.EvsApp;
import fswcore.EvsAppInfoFile;
import fswcore.EvsLogFile;
import fswcore.SbApp;
import fswcore.SbMsgMapFile;
import fswcore.SbPipeFile;
import fswcore.SbRouteFile;
import fswcore.TblApp;
import fswcore.TblRegLogFile;
import gui.CfeFileDialog;


/**
 * Serves as a container for all of the cFE files that are generated as the 
 * result of a command. It could have been designed with one proxy per file
 * but that seemed to be overkill. Architecturally this serves as a delegate
 * between the file 'models' and the GUI.  This allows cFeFile classes to be 
 * decoupled from the GUI. Currently XML is not used to define the file's
 * structure but if it is then the XML should be processed in this class or
 * it could be combined with the FswAppProxy. 
 * 
 * @author David McComas
 */
public class CfeFileProxy 
{

   static public final int MAX_CFE_FILES = 12; 
   private static Logger logger=Logger.getLogger(CfeFileProxy.class);
   
   private Shell          guiShell  = null;
   private FswCmdNetwork  cmdWriter = null;  // Reference to CARE's command writer object
   
   /** Array of file names that can be used for GUIs. The indices must correspond to the action indices */ 
   //private String[]         fileNameList  = new String[MAX_CFE_FILES]; 
   //private CfeFileAction[]  actionCfeFile = new CfeFileAction[MAX_CFE_FILES]; // 
   private MenuManager[]    fileMenu      = new MenuManager[4];

   private ArrayList<FswAppProxy>  fswAppProxyList;

   protected ConcurrentLinkedQueue<StatusMsg> proxyStatusMsgQ;

   /**
    * Construct all of the cFE file proxies. 
    * 
    * @param fswAppProxyList List of cFE application proxies.
    */
   public CfeFileProxy (ArrayList<FswAppProxy>  fswAppProxyList, ConcurrentLinkedQueue<StatusMsg> proxyStatusMsgQ) {

      this.fswAppProxyList = fswAppProxyList;
      this.proxyStatusMsgQ = proxyStatusMsgQ;
      
      Iterator<FswAppProxy> AppProxyIt = fswAppProxyList.iterator();
      while ( AppProxyIt.hasNext() ) {
         
         FswAppProxy appProxy = AppProxyIt.next();
         String appPrefix = appProxy.getFswXmlApp().getPrefix();
         if (appPrefix.equalsIgnoreCase(CARE.XML_VAL_APP_ES)) {
            createEsFileActions(appProxy);
         } // End if ES
         if (appPrefix.equalsIgnoreCase(CARE.XML_VAL_APP_TBL)) {
            createTblFileActions(appProxy);
         } // End if TBL
         if (appPrefix.equalsIgnoreCase(CARE.XML_VAL_APP_EVS)) {
            createEvsFileActions(appProxy);
         } // End if EVS
         if (appPrefix.equalsIgnoreCase(CARE.XML_VAL_APP_SB)) {
            createSbFileActions(appProxy);
         } // End if SB
         
      } // End appProxy loop
      
   } // End CfeFileproxy()
   
   private void createEsFileActions(FswAppProxy appProxy) {

      logger.trace("CfeFileProxy createEsFileActions()");
      fileMenu[0] = new MenuManager(appProxy.getFswXmlApp().getName());

      //actionCfeFile[fileIndex] = new CfeFileAction (appProxy, new EsSysLogFile(), "Sys Log", "\\cygwin\\usr\\dmccomas\\proto_mission\\build\\pc-linux\\exe\\cfe_es_syslog.log", EsApp.CMD_FC_WRITE_SYSLOG);
      //fileMenu[0].add(actionCfeFile[fileIndex]);
      //fileIndex++;
      fileMenu[0].add(new CfeFileAction (appProxy, new EsSysLogFile(),  "Sys Log",  CARE.CFE_DEF_FILE_PATH+"cfe_es_syslog.log",    EsApp.CMD_FC_WRITE_SYSLOG));
      fileMenu[0].add(new CfeFileAction (appProxy, new EsErLogFile(),   "ER Log",   CARE.CFE_DEF_FILE_PATH+"cfe_es_erlog.log",     EsApp.CMD_FC_WRITE_ERLOG));
      fileMenu[0].add(new CfeFileAction (appProxy, new EsAppLogFile(),  "App Log",  CARE.CFE_DEF_FILE_PATH+"cfe_es_app_info.log",  EsApp.CMD_FC_QUERY_ALL));
      fileMenu[0].add(new CfeFileAction (appProxy, new EsTaskLogFile(), "Task Log", CARE.CFE_DEF_FILE_PATH+"cfe_es_task_info.log", EsApp.CMD_FC_QUERY_ALL_TASKS));
      fileMenu[0].add(new CfeFileAction (appProxy, new EsCdsLogFile(),  "CDS Log",  CARE.CFE_DEF_FILE_PATH+"cfe_cds_reg.log",      EsApp.CMD_FC_WRITE_CDS_REG));

   } // End createEsFileActions()

   private void createTblFileActions(FswAppProxy appProxy) {

      logger.trace("CfeFileProxy createTblFileActions()");
      fileMenu[1] = new MenuManager(appProxy.getFswXmlApp().getName());
      fileMenu[1].add(new CfeFileAction (appProxy, new TblRegLogFile(),  "Registry Log", CARE.CFE_DEF_FILE_PATH+"cfe_tbl_reg.log", TblApp.CMD_FC_DUMP_REG));

   } // End createTblFileActions()
   
   private void createEvsFileActions(FswAppProxy appProxy) {

      logger.trace("CfeFileProxy createEvsFileActions()");
      fileMenu[2] = new MenuManager(appProxy.getFswXmlApp().getName());
      fileMenu[2].add(new CfeFileAction (appProxy, new EvsLogFile(),      "Event Log",  CARE.CFE_DEF_FILE_PATH+"cfe_evs.log",     EvsApp.CMD_FC_WRITE_LOG));
      fileMenu[2].add(new CfeFileAction (appProxy, new EvsAppInfoFile(),  "App Info",   CARE.CFE_DEF_FILE_PATH+"cfe_evs_app.log", EvsApp.CMD_FC_WRITE_APP_INFO));

   } // End createEvsFileActions()
   
   private void createSbFileActions(FswAppProxy appProxy) {

      logger.trace("CfeFileProxy createSbFileActions()");
      fileMenu[3] = new MenuManager(appProxy.getFswXmlApp().getName());
      fileMenu[3].add(new CfeFileAction (appProxy, new SbRouteFile(),  "Route Log",   CARE.CFE_DEF_FILE_PATH+"cfe_sb_route.dat",  SbApp.CMD_FC_WRITE_ROUTE_INFO));
      fileMenu[3].add(new CfeFileAction (appProxy, new SbPipeFile(),   "Pipe Log",    CARE.CFE_DEF_FILE_PATH+"cfe_sb_pipe.dat",   SbApp.CMD_FC_WRITE_PIPE_INFO));
      fileMenu[3].add(new CfeFileAction (appProxy, new SbMsgMapFile(), "MsgMap Log",  CARE.CFE_DEF_FILE_PATH+"cfe_sb_msgmap.dat", SbApp.CMD_FC_WRITE_MAP_INFO));

   } // End createSbFileActions()
   

   /**
    * Return a MenuManager array
    * 
    * @return MenuManager array, one entry for each cFE application 
    */
   public MenuManager[] getFileMenu() {
      
      return fileMenu;
      
   } // End getFileMenu()

   /**
    * Set the GUI shell and networked command writer references. These can't be
    * set when the class is constructed because the shell and/or network 
    * connection may not have been established when the class is constructed.  
    * 
    * @param guiShell    GUI display shell
    * @param cmdWriter   Network command writer used to send commands to the cFE
    */
   public void setAppReferences(Shell guiShell, FswCmdNetwork  cmdWriter) {
      
      this.guiShell  = guiShell;
      this.cmdWriter = cmdWriter;;
      
   } // End setAppReferences()

   
   /**
    * Subclass SWT Action class to create a command specific action for each
    * cFE File.
    *
    */
   public class CfeFileAction extends Action {
       
      FswAppProxy  appProxy;
      CfeFile      cfeFile;
      String       defFileName;
      int          funcCode;
      
      /***
       * Construct a cFE File action.
       * 
       * @param appProxy       Proxy object for the FSW Application 
       * @param fileMenuDescr  String that will be used in the menu
       * @param defFileName    String containing the default filename
       * @param funcCode       Function of the command that generates the file
       */
      CfeFileAction (FswAppProxy appProxy, CfeFile cfeFile, String fileMenuDescr, String defFileName, int funcCode) {
          
          super();
          
          this.appProxy    = appProxy;
          this.cfeFile     = cfeFile;
          this.defFileName = defFileName;
          this.funcCode    = funcCode;

          setText(appProxy.getFswXmlApp().getPrefix() + " - " + fileMenuDescr);

          setToolTipText("Comamnd and display " + appProxy.getFswXmlApp().getName() + "'s " + fileMenuDescr);
          
       } // End CfeFileAction
       
      /**
       * Execute the action for the user selected cFE File
       */
       public void run() {

          logger.trace("CfeFileProxy Action: Creating " + appProxy.getFswXmlApp().getName() + "'s " + defFileName);
          StatusMsg statusMsg = new StatusMsg(StatusMsg.Type.ERROR, CARE.STR_UNDEFINED);

          // TODO - Provide mechanism to override the default
          // Send the command to get the file
          CmdPkt cmd = appProxy.getFswXmlApp().getCmdPkt(funcCode);
          if (cmd != null) {
             if (cmdWriter != null) {
                cmdWriter.sendCmd(cmd.getCcsdsPkt());   
                // Provide time for file to be written and transferred
                // TODO - This should be event driven, i.e. continue after confirm file written
                /* This didn'twork. Get event message saying can't write the file and I think it's due to file being opened
                 * by the next line of code. The event message seems to appear after this times out which may be a GUI update 
                 * synchronization issue and not a file already open issue. 
                try {
                   Thread.sleep(3000);
                } catch(InterruptedException ex) {
                   Thread.currentThread().interrupt();
                }
                */
                if (cfeFile.formatDataStrings(defFileName)) {
                   statusMsg.setType(StatusMsg.Type.INFO);
                   statusMsg.setText("Sent " + appProxy.getFswXmlApp().getName() + "'s command function code " + funcCode+ ". Opening dialog to display the file."); 
                   CfeFileDialog cfeFileDialog = new CfeFileDialog(guiShell, cfeFile);
                   cfeFileDialog.open();
                }
                else {
                   statusMsg.setText("Error opening " + defFileName + ". The directory for cFE files may be incorrect or the file may not have been transferred."); 
                }
             }
             else {
                logger.error("CfeFileProxy Action: cmdWriter null for " + appProxy.getFswXmlApp().getName() + "'s command for function code " + funcCode);
                statusMsg.setText("Error sending " + appProxy.getFswXmlApp().getName() + "'s command for function code " + funcCode+ ". Verify connection to CFS."); 
             }
          }
          else {
             logger.trace("CfeFileProxy Action: Couldn't find " + appProxy.getFswXmlApp().getName() + "'s command for function code " + funcCode);
             statusMsg.setText("Error sending " + appProxy.getFswXmlApp().getName() + "'s command for function code " + funcCode+ ". Commannd nto found. Verify application's XML definitions.");
          }

          proxyStatusMsgQ.add(statusMsg);
        
       } // End run()
       
    } // End class CfeFileAction

} // End class CfeFileProxy
