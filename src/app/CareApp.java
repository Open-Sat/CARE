package app;

import ccsds.*;      
import curval.*;
import network.*;
import fsw.CmdPkt;
import fsw.ExApp;
import fswcore.EvsApp;
import fswcore.ToLabApp;
import fswcore.TfApp;
import gui.*;
import util.*;

import java.util.ArrayList;               
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.apache.log4j.Logger;

/**
 * This application provides an environment to develop and run CFS applications     
 * on a desktop.  This environment goes beyond the simple command and telemetry
 * interface.  It manages all of the resources required by an app which includes
 * table definitions, message IDs, and scheduler requirements. Eventually this 
 * tool will accept a FSW application "manifest" that defines all of an app's
 * deployment and runtime resource requirements.
 *
 * @author David McComas
 * 
 * <div><p><strong>TODO - Near term before initial open source</strong></p></div>
 * <ul>
 * <li>TODO - Rename CARE</li>
 * <li>TODO - Fix event message time display</li>
 * <li>TODO - Display table secondary header</li>
 * <li>TODO - Better error reporting & no crashes for bad XML</li>
 * <li>TODO - General Cleanup files in packages: app, fsw. Coding standards, org todo, javadoc, remove commented code, Run javadoc</li>
 * <li>TODO - Need to get status back from menu actions that can be displayed in the activity windows</li>
 * <li>TODO - Complete apps and design cFE configuration management, browse cFE error codes?, scheduler browse?CfeFileProxy hardcoded paths</li>
 * <li>TODO - Add application load and design XML manifest, reload XML. Allow user to create default user apps loaded on init</li>
 * <li>TODO - Add sort to config dialog box</li>
 * <li>TODO - Verify core cmd, tlm, and files</li>
 * <li>TODO - Cmd: Add all types, radix options, Byte-flip (file vs tlm ordering), ByteBuffer usage, endian</li>
 * <li>TODO - Tlm: Add types (float, signed int, double), fix dialog sizing, radix display, Byte-flip (file vs tlm ordering), ByteBuffer usage</li>
 * <li>TODO - Create log class for GUI? Add time to user log. Should debug go to a log file?Display startup log in user window</li>
 * <li>TODO - Improve error handling. Gracefully reject bad XML. What should be done with exceptions?Dialog X box closure exception</li>
 * <li>TODO - Save modified properties across runs</li>
 * <li>TODO - Ops goal oriented commands: set time & serve cFE. Load App. Reload XML. Unload app with drop down of loaded apps. Disconnect command?</li>
 * <li>TODO - Add an event interface for scheduled activities</li>
 * <li>TODO - Fix time display so it's right justified</li>
 * <li>TODO - Make a single definition for data types used in Java and XML or at least use CARE constants as a single reference</li>
 * <li>TODO - Is an open/active tlm page list needed? Maybe not since simple tool.</li>
 * <li>TODO - Graceful shutdown, cleanup threads when CARE closed</li>
 * <li>TODO - Add  and integrated text file displays, Fix delay problem. automated dump file commands fail</li>
 * <li>TODO - Enhance colors. Establish common look and feel</li>
 * <li>TODO - Add JUnit, automatic regression testing, continuous integration. Put framework in place before OS</a></li>
 * <li>TODO - Add Fix version number in prologues once put on GIT</a></li>
 * <li>TODO - Determine whether a TlmNetwork TlmPktQ is needed after test with higher rates</a></li>
 * <li>TODO - cFEFileProxy has hardcoded directory paths and files names</a></li>
 * </ul>
 * 
 * <div><p><strong>TODO - Long term</strong></p></div>
 * <ul>
 * <li>TODO - Network: CARE 'tlm' windows: network traffic, scheduler, etc.</li>
 * <li>TODO - Network: Disconnect network button for initial setup scenarios. Put network info command (see connect command) somewhere useful</li>
 * <li>TODO - Refactor CAREApp if it gets to big. Move DataPntStr classes out of FSW?</li>
 * <li>TODO - DB: Create XML DTD or use a standard.</li>
 * <li>TODO - DB: Browse XML </li>
 * <li>TODO - Import cFE definitions: Error Codes, File type & subtype strings</li>
 * <li>TODO - Database driven? XML from code? Configuration parameters in requirements DB. FswXmlApp could be expanded to include app info that is now in C headers. The reason is user apps should only create a single XML "manifest", DB driven so header files like osconfi.h are ingested and configure the tool. also browse configurations.</li></li> 
 * <li>TODO - CMD & TLM hover details that would come from a DB</li>
 * <li>TODO - Command: Radix, Parameter choices in XML</li>
 * <li>TODO - Allow faster than real time simulation</li>
 * <li>TODO - Add tools: Browse cFE configurations, Dump FSW scheduler table</li>
 * <li>TODO - Telemetry stale flag</li>
 * <li>TODO - Add script language. This should allow users to access tables and tlm values and to create tools for their apps that are integrated into this app.</a></li>
 * <li>TODO - Local 1024, 2048, and 4096 for constants that shouldn't be hard coded.</li>
 * <li>TODO - Possibly merge with GFAB definitions so one global single definition point</li>
 * <li>TODO - If the same tlm window is open twice and the newest is closed then old window won't update because removed from list</li>
 * </ul>
 */
public class CareApp extends ApplicationWindow {

   private static Logger logger=Logger.getLogger(CareApp.class);
   
   static final private boolean TEST_NEW_FEATURE = false;  // Set to true to add a new feature to app menu so it can be tested
   static final public  String  TOOL_BAR_HELP_ID = "HelpToolBarId";
   static final private String  TIME_STR_PAD = "                   "; // TODO - Kludge until I get right justified working. See code with ~~     

   private StyledText  textFswEventLog;
   private StyledText  textActivityLog;
   private Text        textUserInput;
   private Text        textTimeMenu;  //~~
   
   private Action actionConnectToCFS;
   private Action actionManageToPkts;
   private Action actionGetFileFromCFS;
   private Action actionPutFileToCFS;
   private Action actionExit;

   private Action actionDisplayNetworkStatus;
   private Action actionDisplayGndSchedulerStatus;
   private Action actionDisplayPropertyFile;
   
   private Action actionDisplayAbout;
   private Action actionAppHelp;
   private Action actionCfeHelp;

   private Action actionAddUserApp;
   private Action actionRefreshUserApp;

   private Action actionDisplayTime;
   
   private Action actionTestNewFeature;
   private Action actionTBD;

   // Class level definitions because need to change during runtime
   private MenuManager menuCoreApps;
   private MenuManager menuCoreFiles;
   private MenuManager menuUserApps;

   // Utilities
   private SimTime    simTime   = null;
   private Scheduler  scheduler = null;

   // Command and Telemetry Network
   private FswCmdNetwork  cmdWriter = null;
   private FswTlmNetwork  tlmReader = null;
   
   // Telemetry Management
   private boolean[]      tlmUpdated = new boolean[CARE.MAX_MSG_ID_CNT];   
   private TlmPktDatabase tlmDatabase = null;
   private TlmPageManager tlmPageManager;
   private TlmMonitor     tlmMonitor;
   
   // Application/Runtime Configuration Properties
   private static Properties appConfig = new Properties();     // CARE configuration properties 
   private static Properties cfeConfig = new Properties();     // cfE configuration parameters
   private static Properties midConfig = new Properties();     // Message IDs
   private static Properties propConfig[] = new Properties[3]; // Convenient array for dialog
   static private final String propStrArray[] = { "Application", "cFE Config Parameters", "Message IDs"}; 

   /**
    * cFE 'Proxies'
    * 
    *  A proxy class is used to server as a liaison between CARE control & GUI classes and classes that represent cFE
    *  objects.  
    */
   private ArrayList<FswAppProxy> coreAppProxyList = new ArrayList<FswAppProxy>();
   private CfeFileProxy  cfeFileProxy = null;

   /**
    * User Applications
    * TODO - Hardcoded apps that can be removed once initial development complete
    * TODO - Delete: private EsApp  ES  = new EsApp(EsApp.PREFIX_STR,"Executive Services Application");
    * TODO - Delete: private EvsApp EVS = new EvsApp(EvsApp.PREFIX_STR,"cFE Event Service");
    */
   private ToLabApp TO  = new ToLabApp(ToLabApp.PREFIX_STR,"Telemetry Output");
   private ExApp    EX  = new ExApp(ExApp.PREFIX_STR,"Example Application");

   private ArrayList<FswAppProxy>  userAppProxyList = new ArrayList<FswAppProxy>();

   /**
    * Queue is used to allow proxy objects to pass status back to the main
    * application so it can be displayed in the GUI.  
    */
   private static ConcurrentLinkedQueue<StatusMsg> proxyStatusMsgQ = new ConcurrentLinkedQueue<StatusMsg>();
   
   /*
    * Lua script engine
    */
   LuaScriptEngine Lua;
   
   /*
    * TFTP Server
    */
   
   TFTPServer TftpServer;
   
   /***************************************************************************
    ** 
    ** Application Management
    **
    ** createContents() is called during the open() calla so JcatApp constructor
    ** can't use the GUI.
    */
    public static void main(String[] args) {
       
       ApplicationWindow window = new CareApp(null);

       window.setBlockOnOpen(true);
       window.open();
      
       Display.getCurrent().dispose();

    } // End main()

   public CareApp(Shell parentShell) 
   {
      super(parentShell);

      String propFile = null;
      
      try {
         
         propFile = CARE.PROP_APP_FILE;
         appConfig.loadFromXML(new FileInputStream(propFile));
         logger.trace("Loaded property file: " + propFile);
         propConfig[0] = appConfig;

         propFile = CARE.PROP_CFE_FILE;
         cfeConfig.loadFromXML(new FileInputStream(propFile));
         logger.trace("Loaded property file: " + propFile);
         propConfig[1] = cfeConfig;
      
         propFile = CARE.PROP_MID_FILE;
         midConfig.loadFromXML(new FileInputStream(propFile));
         logger.trace("Loaded property file: " + propFile);
         propConfig[2] = midConfig;
      
         TftpServer = new TFTPServer(new File(appConfig.getProperty(CARE.PROP_APP_FILE_SERVER)),  
                      new File(appConfig.getProperty(CARE.PROP_APP_FILE_SERVER)), 
                      69, TFTPServer.Mode.GET_AND_PUT);

      } catch (Exception e) {
         
          logger.error("Couldn't open properties file: " + propFile);
          e.printStackTrace();
      
      } // End property file exception catch 
      
      logger.info("Version = " + appConfig.getProperty(CARE.PROP_APP_VER));
      for(String key : appConfig.stringPropertyNames()) {
         String value = appConfig.getProperty(key);
         logger.info(key + " => " + value);
       }

      createActions();

      addStatusLine();
      addToolBar(SWT.FLAT);
      addMenuBar();
      
      Arrays.fill(tlmUpdated, Boolean.FALSE);
      tlmMonitor  = new TlmMonitor();      
      tlmDatabase = new TlmPktDatabase();
      tlmDatabase.registerObserver(tlmMonitor);
      tlmPageManager = new TlmPageManager();
      
      // TODO - Possibly add default behavior based on previous application execution 
      loadCoreApps(appConfig.getProperty(CARE.PROP_APP_CORE_APPS));
      loadUserApps(appConfig.getProperty(CARE.PROP_APP_USER_APPS));

      simTime = new SimTime(Integer.parseInt(appConfig.getProperty(CARE.PROP_APP_TIME_TICK)));
      scheduler = new Scheduler(simTime, Integer.parseInt(appConfig.getProperty(CARE.PROP_APP_TIME_TICK)));
      
   } // End CareApp()

   public void postGuiCareConstuctor() {
      
      /* Code I found for right justified toolbar item.
       * This gets squished but it may be because the window size is too small and it doesn't expand the window
      ToolItem itemSeparator = new ToolItem (getToolBarManager().getControl(), SWT.SEPARATOR);  
      textTime = new Text(getToolBarManager().getControl(), SWT.READ_ONLY | SWT.BORDER | SWT.TRAIL); // trail=right  
      textTime.setText("YYYY-DOY-HH:MM:ss.mmm");  
      itemSeparator.setWidth(textTime.getBounds().width);  
      itemSeparator.setControl(textTime);  
      */
      
      scheduler.addSchedulableItem("1Hz Display", new OneHzDisplay());
      scheduler.addSchedulableItem("Proxy Monitor", new proxyMonitor());
      //scheduler.addSchedulableItem("Telemetry Monitor", new TlmMonitor());
      if (scheduler.getInvalidInitTick()) {
         logActivityError(CARE.LOG_CONSOLE, "Invalid user tick of " + simTime.getTick() + "ms specified in properties file. Automatically corrected to " + scheduler.getTick() + "ms");
      }
      logActivity(CARE.LOG_CONSOLE, Lua.getVersionStr(), false);
      logActivity(CARE.LOG_CONSOLE, "System Startup Complete. Timer Tick = " + scheduler.getTick() + "ms", true);
      scheduler.startThread();
     

   } // postGuiCareConstuctor()

   /***************************************************************************
    ** 
    ** GUI Management
    */
   private void createActions() 
   {

      /********************************
       *  ACTION: Connect to CFS
       *  
       */
      actionConnectToCFS = new Action() 
      {
         public void run() {

            NetworkDialog networkDialog = new NetworkDialog(getShell());
            String careIp = networkDialog.open(appConfig.getProperty(CARE.PROP_APP_CARE_IP_ADDR),
            		                       appConfig.getProperty(CARE.PROP_APP_CFS_IP_ADDR),
                                           appConfig.getProperty(CARE.PROP_APP_CFS_CMD_PORT),
                                           appConfig.getProperty(CARE.PROP_APP_CFS_TLM_PORT));
            
            // A little odd to just check one item but its a little sanity
            if (careIp == null) {
               logActivityError(CARE.LOG_CONSOLE, "Connection to the CFS aborted");
            }
            else {
               
               logActivity(CARE.LOG_CONSOLE, "Establising connect to the CFS"
                               + ": CARE IP Address = " +  networkDialog.getCareIpAddress()
                               + ", CFS IP Address = "  +  networkDialog.getCfsIpAddress()
                               + ", CFS Cmd Port = " + networkDialog.getCfsCmdPort()
                               + ", CFS Tlm Port = " + networkDialog.getCfsTlmPort(), true);

               //TODO - More this to somewhere useful: NetworkInfo networkInfo = new NetworkInfo();
               
               cmdWriter = new FswCmdNetwork(networkDialog.getCfsIpAddress(),networkDialog.getCfsCmdPort());
               tlmReader = new FswTlmNetwork(tlmDatabase, networkDialog.getCareIpAddress(), networkDialog.getCfsTlmPort());
               logNetworkStatus();            

               logActivity(CARE.LOG_USER, "Enabling TO Lab Telemetry", true);
               FswAppProxy toProxy = getFswAppProxy(CARE.XML_VAL_APP_LABTLM);
               CmdPkt cmd = toProxy.getFswXmlApp().getCmdPkt(ToLabApp.CMD_FC_ENA_TLM);
               cmd.setParam(0, careIp);   // TODO - Remove hardcoded parameter index
               cmd.loadParamList();
               cmdWriter.sendCmd(cmd.getCcsdsPkt());
               
               /*
                *  Ensure that an App Proxy has the CmdWriter.
                *   
                *  This must be done for cFE apps and depending upon when a user adds an app relative
                *  to when they connect to the CFS this may or not need to be performed.
                */
               
               Shell shell = getShell();
               Iterator<FswAppProxy> appProxyIt = coreAppProxyList.iterator();
               while ( appProxyIt.hasNext() ) {
                  FswAppProxy appProxy = appProxyIt.next();
                  appProxy.setAppReferences(shell, cmdWriter, appConfig.getProperty(CARE.PROP_APP_CFE_FILE));
               }
               
               cfeFileProxy.setAppReferences(shell, cmdWriter);
               
               appProxyIt = userAppProxyList.iterator();
               while ( appProxyIt.hasNext() ) {
                  FswAppProxy appProxy = appProxyIt.next();
                  appProxy.setAppReferences(shell, cmdWriter, appConfig.getProperty(CARE.PROP_APP_CFE_FILE));
               }

               scheduler.addSchedulableItem("Telemetry Monitor", new TlmMonitor());
               
            } // End if IP Address != null
           
         } // End run()
      }; // End Action()
      actionConnectToCFS.setText("Connect to CFS");
      actionConnectToCFS.setToolTipText("Initialize command and telemetry network connections");
      //actionInitNetwork.setImageDescriptor(ImageDescriptor.createFromFile(null, ""));


      /********************************
       *  ACTION: Manage TO Packets - Configure (enable/disable) which packets are output by TO Lab
       *   
       */
      actionManageToPkts = new Action() {
         
         public void run() {

            FswAppProxy toProxy = getFswAppProxy(CARE.XML_VAL_APP_LABTLM);
            if (toProxy != null) {
               ManageToPktsDialog manageToPktsDialog = new ManageToPktsDialog(getShell());
               int userAction = manageToPktsDialog.open(createMsgIdStrArray());
               logger.trace("TO packet config = " + userAction + " message = " + Integer.toHexString(manageToPktsDialog.getMsgId()));
               if (userAction > ManageToPktsDialog.CANCEL) {
                  int funcCode = ToLabApp.CMD_FC_ADD_PKT;
                  if (userAction == ManageToPktsDialog.DISABLE)
                     funcCode = ToLabApp.CMD_FC_REM_PKT;  
                  CmdPkt cmd = toProxy.getFswXmlApp().getCmdPkt(funcCode);
                  if (cmd != null) {
                     int msgId = manageToPktsDialog.getMsgId();
                     cmd.setParam(0,Integer.toString(msgId)); // TODO - Remove hardcoded parameter index
                     cmd.loadParamList();
                     if (cmdWriter != null) {
                        cmdWriter.sendCmd(cmd.getCcsdsPkt());
                        if (funcCode == ToLabApp.CMD_FC_ADD_PKT)
                           logActivity(CARE.LOG_USER, "Enabled Telemetry Output to send packet " + msgId, true);
                        else
                           logActivity(CARE.LOG_USER, "Disabled Telemetry Output from sending " + msgId, true);
                     } // End if cmdwriter != null
                     else
                        logActivityError(CARE.LOG_USER, "Error sending Telemetry Out command. Verify connection to the CFS.");
                  } // End if cmd != null
                  else {
                     logActivityError(CARE.LOG_USER, "Error sending Telemetry Out command. Verify Telemetry Output XML database.");
                  }
               } // End if not cancel

            } // End if found TO proxy
            else {
               logActivityError(CARE.LOG_USER, "Error sending Telemetry Out command. Unable to find Telemetry Output application in database. Verify CARE configuration file.");
            }

         } // End run()
      }; // End Action()
      actionManageToPkts.setText("Manage TO Pkt");
      actionManageToPkts.setToolTipText("Enable/disable packets for Telemetry Output");
      //actionCfgToPkt.setImageDescriptor(ImageDescriptor.createFromFile(null, ""));

      /********************************
       *  ACTION: Get a file to the CFS
       *  
       */
      actionGetFileFromCFS = new Action() 
      {
         public void run() {

            final String fileName = appConfig.getProperty(CARE.PROP_APP_FILE_SERVER) + "TFTP-get-test.txt";
            logger.trace("Get file from the cFS and write to " + fileName);
             
            Display.getDefault().syncExec(new Runnable() {
               public void run() {
                  logger.trace("Creating TFTP session to get file");
                  final TftpClient tftpClient = new TftpClient(appConfig.getProperty(CARE.PROP_APP_CFS_IP_ADDR),"/ram/TFTP-get-test3.txt"); 
                  logActivity(CARE.LOG_USER, "TFTP ground client created", true);
                  Display.getDefault().timerExec(1000, new Runnable() {
                     public void run() {
                        try {
	                       if (tftpClient.getFile(fileName) == 0) {
	                          logActivity(CARE.LOG_USER, "TFTP successfully got " + fileName, true);	 
	                       } else {
	                          logActivityError(CARE.LOG_USER, "TFTP failed to get " + fileName);
	                       }
                        }
                        catch (Exception e) {
                           e.printStackTrace();
                        }
                     } // End run()
                  }); // End timerExec()

               } // End run()
            }); // End runnable()
 
         } // End run()
      }; // End Action()
      
      actionGetFileFromCFS.setText("Get a file from the CFS");
      actionGetFileFromCFS.setToolTipText("Get a file from the CFS");

      /********************************
       *  ACTION: Put a file to the CFS
       *  
       *  TODO - Add destination file management
       */
      actionPutFileToCFS = new Action() 
      {
         public void run() {

            logger.trace("Put File to the cFS");
            FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
            fileDialog.setFilterPath(CARE.PATH_CWD);
            logger.debug("PutFileToCFS: " + CARE.PATH_CWD);
            final String appFile = fileDialog.open();
             
            if (appFile != null) {
           	   Display.getDefault().syncExec(new Runnable() {
                  public void run() {
                     logger.trace("Creating TFTP session to put file");
                     // TODO - Remove once server working: 
                     // TDOO final TftpSession Tftp = new TftpSession(appConfig.getProperty(CARE.PROP_APP_CFS_IP_ADDR));
                     //if (Tftp.createClient()) {  // TODO - TftpServer.isRunning()) {  
                     final TftpClient tftpClient = new TftpClient(appConfig.getProperty(CARE.PROP_APP_CFS_IP_ADDR),"/ram/TFTP-put-test.txt"); 
                         logActivity(CARE.LOG_USER, "TFTP ground client created", true);
                         FswAppProxy toProxy = getFswAppProxy(CARE.XML_VAL_APP_TFAPP);
                         CmdPkt cmd = toProxy.getFswXmlApp().getCmdPkt(TfApp.CMD_FC_PUT_FILE);

                         //char    Host[TFTP_HOST_NAME_LEN];
                         //char    Port[TFTP_PORT_NAME_LEN];
                         //char    SrcFileName[TFTP_FILE_NAME_LEN];
                         //char    DestFileName[TFTP_FILE_NAME_LEN];
                         // TODO - Remove hardcoded parameters
                         //cmd.setParam(0, Tftp.getClient().getLocalAddress().toString());
                         //cmd.setParam(1, Integer.toString(Tftp.getClient().getLocalPort()));
                         //cmd.setParam(0, "1237");
                         //cmd.setParam(1, "Test");
                         //cmd.setParam(2, "/ram/TBD.txt");
                         //cmd.loadParamList();
                         //cmdWriter.sendCmd(cmd.getCcsdsPkt());
                         //logActivity(CARE.LOG_USER, "TFTP put file " + appFile + " to port " + Tftp.getClient().getLocalPort(), true);
                         logActivity(CARE.LOG_USER, "TFTP put file " + appFile, true);
                         Display.getDefault().timerExec(1000, new Runnable() {

                             public void run() {

                                //if (Tftp.putFile(appFile, "/ram/TBD.txt")) {
                            	try {
	                            	if (tftpClient.putFile(appFile) == 0) {
	                            	
	                                   logActivity(CARE.LOG_USER, "TFTP successfully put " + appFile, true);	 
	                                } else {
	                                   //logActivityError(CARE.LOG_USER, "TFTP failed to put " + appFile + ". " + Tftp.getErrString());
	                                   logActivityError(CARE.LOG_USER, "TFTP failed to put " + appFile);
	                                }
                            	}
                            	catch (Exception e) {
                            		
                            	}
                             } // End run()

                         }); // End timerExec()

                     //} // End if TFTP server running
                     //else {
                     //	 logActivityError(CARE.LOG_CONSOLE, "TFTP server not running"); // TODO - Addc corrective action 
                     //}

                  } // End run()
         	   }); // End runnable()
            } // End if appFile valid
            else {
             
               logActivityError(CARE.LOG_USER, "Failed to open file");
                
            } // End if appFile != null

         } // End run()
      }; // End Action()
      actionPutFileToCFS.setText("Put a file to the CFS");
      actionPutFileToCFS.setToolTipText("Send a file to the CFS");

      /********************************
       *  ACTION: Add user application to database and menu system. This does
       *          not load the app to the FSW.
       */
      actionAddUserApp = new Action() {
         
         public void run() {

            logger.trace("Adding user to menu & database");
            FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
            fileDialog.setFilterNames(new String[] {"Application database files (*.xml)"});
            fileDialog.setFilterExtensions(new String[] {"*.xml"});
            fileDialog.setFilterPath(CARE.PATH_CWD + CARE.PATH_SEP + CARE.PATH_DB_APPS);
            logger.debug("Adding user app: " + CARE.PATH_CWD + CARE.PATH_SEP + CARE.PATH_DB_APPS);
            String appFile = fileDialog.open();
            
            if (appFile != null) {
               
               FswAppProxy appProxy = new FswAppProxy(appFile, tlmPageManager, midConfig, proxyStatusMsgQ);
               appProxy.setAppReferences(getShell(), cmdWriter, appConfig.getProperty(CARE.PROP_APP_CFE_FILE));

               int appIndex = userAppExists(appProxy.getFswXmlApp().getPrefix()); 
               if (appIndex >=  0) {
                  
                  MessageBox msgBox = new MessageBox(getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
                  msgBox.setText("Application " + appProxy.getFswXmlApp().getName() + " Already Exists");
                  msgBox.setMessage("Do you want to refresh the application's database?");
                  if (msgBox.open() == SWT.YES) {
                  
                     refreshUserApp(appIndex, appProxy);
                     logActivity(CARE.LOG_USER, "Refreshed user application " + appProxy.getFswXmlApp().getName(), true);
                  
                  } // End if refresh user app
                  
               } // End if user app exists
               else {

                  userAppProxyList.add(appProxy);
                  MenuManager menuUserApp = new MenuManager(appProxy.getFswXmlApp().getName());
                  menuUserApp.add(appProxy.getCmdMenu());
                  menuUserApp.add(appProxy.getTblMenu());
                  menuUserApp.add(appProxy.getTlmMenu());
                  menuUserApps.add(menuUserApp);
                  logActivity(CARE.LOG_USER, "Added user application " + appProxy.getFswXmlApp().getName() + " to the menu", true);
                  
               } // End if user app does not exist

            } // End if appFile valid
            else {
            
               logActivityError(CARE.LOG_USER, "Failed to open application XML database file");
               
            } // End if appFile != null


         } // End run()
      }; // End Action()

      actionAddUserApp.setText("Add user application");
      actionAddUserApp.setToolTipText("Add a user application to the database and menu");

      /********************************
       *  ACTION: Refresh an existing user application's database. This does
       *          not load the app to the FSW.
       */
      actionRefreshUserApp = new Action() {
         
         public void run() {

            logger.trace("Refresh user database");
            FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
            fileDialog.setFilterNames(new String[] {"Application database files (*.xml)"});
            fileDialog.setFilterExtensions(new String[] {"*.xml"});
            fileDialog.setFilterPath(CARE.PATH_CWD + CARE.PATH_SEP + appConfig.getProperty(CARE.PATH_DB_APPS));
            String appFile = fileDialog.open();
            if (appFile != null) {

               FswAppProxy appProxy = new FswAppProxy(appFile, tlmPageManager, midConfig, proxyStatusMsgQ);
               appProxy.setAppReferences(getShell(), cmdWriter, appConfig.getProperty(CARE.PROP_APP_CFE_FILE));

               int appIndex = userAppExists(appProxy.getFswXmlApp().getPrefix()); 
               if (appIndex >=  0) {

                  refreshUserApp(appIndex, appProxy);
                  logActivity(CARE.LOG_USER, "Refreshed user application " + appProxy.getFswXmlApp().getName(), true);

               } // End if app exists
               else {
                  
                  logActivity(CARE.LOG_USER, "User application " + appProxy.getFswXmlApp().getName() + " doesn't exist", true);

               } // End if app exists
            } // End if appFile valid
            else {
            
               logActivityError(CARE.LOG_USER, "Failed to open application XML database file");
            
            } // End if appFile != null


         } // End run()
      }; // End Action()

      actionRefreshUserApp.setText("Refresh user application");
      actionRefreshUserApp.setToolTipText("Refresh an existing user application's database");

      /********************************
       *  ACTION: Prototype a new feature
       *   
       */
      actionTestNewFeature = new Action() 
      {
         public void run() 
         {

            int     cmdID;
            String  cmdParam;
            CmdPkt  cmdPkt;
            

            /*
             *  Use a dialog to get parameters. The test dialogue can modified as needed.
             */
            TestNewFeatureDialog newFeatureDialog = new TestNewFeatureDialog(getShell(), "Test: 1=ExApp NOOP");
            cmdID = newFeatureDialog.open();
            cmdParam = newFeatureDialog.getCmdParam();
            
            switch (cmdID) 
            {
            case 1:
               logActivity(CARE.LOG_USER, "Send EX Noop", true);
               CmdPkt ExCmd = EX.getCmdList().get(ExApp.CMD_FC_NOOP);
               cmdWriter.sendCmd(ExCmd.getCcsdsPkt());
               break;

            default:
               logActivity(CARE.LOG_USER, "Test new feature Error: Invalid ID = " + cmdID, true);
            
            } // End command switch
            
            
         } // End run()
      }; // End Action()
      actionTestNewFeature.setText("Test New Feature");
      actionTestNewFeature.setToolTipText("Test a new feature under development");

      /********************************
       *  ACTION: Place holder for new feature 
       *   
       */
      actionTBD = new Action() {
         
         public void run() {

            logActivityError(CARE.LOG_USER, "This feature has not been implemented");
         } // End run()
      }; // End Action()

      actionTBD.setText("Coming Soon...");
      actionTBD.setToolTipText("This feature has not been developed");

      /********************************
       *  ACTION: Exit the application
       *   
       */
      actionExit = new Action() {
         public void run()
         {
            if(! MessageDialog.openConfirm(getShell(), "Confirm", "Are you sure you want to exit?")) 
               return;
            // TODO - Add dirty file checks and saving

            close();
         } // End run()
      };
      actionExit.setText("Exit");
      
      /********************************
       *  ACTION: Display Network Status
       *   
       */
      actionDisplayNetworkStatus = new Action() 
      {
         public void run() 
         {
            logNetworkStatus();
            
         } // End run()
      }; // End Action()
      actionDisplayNetworkStatus.setText("Network Status");
      actionDisplayNetworkStatus.setToolTipText("Display network configurationa and traffic statistics");

      /********************************
       *  ACTION: Display Scheduler Status
       *   
       */
      actionDisplayGndSchedulerStatus = new Action() 
      {
         public void run() 
         {
            logActivity(CARE.LOG_USER, scheduler.getScheduleList(), false);
            
         } // End run()
      }; // End Action()
      actionDisplayGndSchedulerStatus.setText("Ground scheduler status");
      actionDisplayGndSchedulerStatus.setToolTipText("Display ground scheduler activities");

      /********************************
       *  ACTION: Display a properties configuration file
       *   
       */
      actionDisplayPropertyFile = new Action() 
      {
         public void run() 
         {
            PropertiesDialog propDialog = new PropertiesDialog(getShell(),propConfig, propStrArray); 
            propDialog.open();
            
         } // End run()
      }; // End Action()
      actionDisplayPropertyFile.setText("Property Config File...");
      actionDisplayPropertyFile.setToolTipText("Display properties configuration file");

      /********************************
       *  ACTION: Display About
       */
      actionDisplayAbout = new Action() {
         public void run() {
            MessageDialog.openInformation(getShell(), "About", "CFS Application Runtime Environment (CARE)\nProvides basic user interface for running and testing CFS applications");
         }
      };
      actionDisplayAbout.setText("About");
      //actionDisplayAbout.setImageDescriptor(ImageDescriptor.createFromFile(null, ""));
      
      /********************************
       *  ACTION: Application Help
       */
      actionAppHelp = new Action() {
         public void run() {
            BrowserDialog cfeBrowser = new BrowserDialog(getShell(), "CARE User's Guide");
            cfeBrowser.open(appConfig.getProperty(CARE.PROP_APP_HELP));
         }
      };
      actionAppHelp.setText("CARE Help");
      actionAppHelp.setToolTipText("Instructions for use");
      actionAppHelp.setId(TOOL_BAR_HELP_ID); 

      /********************************
       *  ACTION: cFE Help
       */
      actionCfeHelp = new Action() {
         public void run() {
            BrowserDialog cfeBrowser = new BrowserDialog(getShell(), "cFE User's Guide");
            cfeBrowser.open(appConfig.getProperty(CARE.PROP_APP_CFE_HELP));
            
         } // End run()
      };
      actionCfeHelp.setText("cFE Users Guide");
      actionCfeHelp.setToolTipText("cFE User's Guide");
      actionCfeHelp.setId(TOOL_BAR_HELP_ID); 

      /* From Joe's JCAT but got null pointer exception
       * Add exit prompt
            getShell().addShellListener(new ShellAdapter() {
         @Override
         public final void shellClosed(ShellEvent e) {
            e.doit = false;

            int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
            MessageBox messageBox = new MessageBox(getShell(), style);
            messageBox.setText("Confirmation of Exit");
            messageBox.setMessage("End your current session?");
            e.doit = messageBox.open() == SWT.YES;
         }
      });
 */
/* This is some code for someone trying something similar
      public TextFieldAction(ApplicationWindow app, ToolBar toolBar) {  
         ToolItem itemSeparator = new ToolItem(toolBar, SWT.SEPARATOR);  
         this.textField= new Text(toolBar, SWT.READ_ONLY | SWT.BORDER | SWT.TRAIL); // trail=right  
         this.textField.setText("120");  
         itemSeparator.setWidth(textField.getBounds().width);  
         itemSeparator.setControl(textField);  
   
         setEnabled(false);  
     }  
 */
      /********************************
       *  ACTION: Display Time
       *  ~~
       */
      actionDisplayTime = new Action() 
      {
         public void run() 
         {
            // Nothing to do
            
         } // End run()
      }; // End Action()
      actionDisplayTime.setText( TIME_STR_PAD + SimTime.DISPLAY_FORMAT);
     
   } // End createActions()
  
   
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.window.ApplicationWindow#createMenuManager()
    */
   protected MenuManager createMenuManager()
   {
      MenuManager bar = new MenuManager();

      MenuManager menuFile = new MenuManager("&Application");
      menuFile.add(actionConnectToCFS);
      menuFile.add(new Separator());
      if (TEST_NEW_FEATURE) {
         menuFile.add(actionTestNewFeature);
         menuFile.add(new Separator());
      }
      menuFile.add(actionManageToPkts);
      menuFile.add(new Separator());
      menuFile.add(actionGetFileFromCFS);
      menuFile.add(actionPutFileToCFS);
      menuFile.add(new Separator());
      menuFile.add(actionExit);
      
      menuCoreApps = new MenuManager(CARE.MENU_CORE_APPS);  // Sub-menus created when apps loaded from XML definitions

      menuCoreFiles = new MenuManager(CARE.MENU_CORE_FILES); // Sub-menus created when core file objects created

      menuUserApps = new MenuManager(CARE.MENU_USER_APPS);
      menuUserApps.add(actionAddUserApp);
      menuUserApps.add(actionRefreshUserApp);
      menuUserApps.add(new Separator()); // User apps loaded during runtime will be done after the separator
      
      MenuManager menuNetwork = new MenuManager("Status");
      menuNetwork.add(actionDisplayPropertyFile);
      menuNetwork.add(actionDisplayNetworkStatus);
      menuNetwork.add(actionDisplayGndSchedulerStatus);

      MenuManager menuHelp = new MenuManager("&Help");
      menuHelp.add(actionDisplayAbout);
      menuHelp.add(actionAppHelp);
      menuHelp.add(actionCfeHelp);

      bar.add(menuFile);
      bar.add(menuCoreApps);
      bar.add(menuCoreFiles);
      bar.add(menuUserApps);
      bar.add(menuNetwork);
      bar.add(menuHelp);
      bar.updateAll(true);

      return bar;
      
   } // End MenuManager()

   public static void addAction(
      
      ToolBarManager manager,
      Action action,
      boolean displayText) {
      if (!displayText) {
         manager.add(action);
         return;
      } else {
         ActionContributionItem item = new ActionContributionItem(action);
         item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
         manager.add(item);
      }
      
   } // addAction()

   
   /* (non-Javadoc)
    * @see org.eclipse.jface.window.ApplicationWindow#createToolBarManager(int)
    */
   protected ToolBarManager createToolBarManager(int style) {
      ToolBarManager manager = super.createToolBarManager(style);

      // Network actions
      addAction(manager, actionConnectToCFS, true);
      manager.add(new Separator());

      // Telemetry actions
      addAction(manager, actionManageToPkts, true);
      manager.add(new Separator());

      // Help Actions
      addAction(manager, actionAppHelp, true);      
      manager.add(new Separator());
      addAction(manager, actionCfeHelp, true);      
      manager.add(new Separator());

      // Time Display
      addAction(manager, actionDisplayTime, true); 
      // Some code to right align something: GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(toolBar);
      /*~~
      //ToolItem itemSeparator = new ToolItem(, SWT.SEPARATOR); 
      Composite composite = new Composite(getShell(), SWT.NONE);
      ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
      ToolItem itemSeparator = new ToolItem(toolBar, SWT.SEPARATOR );
      Text textTimeMenu = new Text(toolBar, SWT.READ_ONLY | SWT.BORDER | SWT.TRAIL); // trail=right  
      textTimeMenu.setText(SimTime.DISPLAY_FORMAT);  
      itemSeparator.setWidth(textTimeMenu.getBounds().width);  
      itemSeparator.setControl(textTimeMenu);  
      manager.add(itemSeparator);
      */
/*
         ToolItem itemSeparator = new ToolItem(toolBar, SWT.SEPARATOR);  
         this.textField= new Text(toolBar, SWT.READ_ONLY | SWT.BORDER | SWT.TRAIL); // trail=right  
         this.textField.setText("120");  
         itemSeparator.setWidth(textField.getBounds().width);  
         itemSeparator.setControl(textField);  

      */
      
      manager.update(true);      
      
      return manager;
      
   } // End createToolBarManager()

   /**
    * Return the MenuManager that matches the menuString parameter.
    * 
    */
   private MenuManager getMenuManager(MenuManager menuManager, String menuString) {
      
      MenuManager menu  = null;
      
      IContributionItem menuItems[] = menuManager.getItems(); //getMenuBarManager().getItems();

      // Find Command and Telemetry menu so core app commands & telemetry can be added to them
      logger.trace("getMenuManager(): getMenuBarManager().getMenuText() = " + getMenuBarManager().getMenuText());
      for (IContributionItem item : menuItems){
         if (item instanceof MenuManager) {
            logger.trace("Menutext = " + ((MenuManager)item).getMenuText());
            if ( ((MenuManager)item).getMenuText().equalsIgnoreCase(menuString) ){  
               menu = (MenuManager)item;
               logger.trace("Found menu " + menuString);
            }
         } // End if Item is a MenuManager
      } // End menuItem loop

      return menu;
      
   } // End getMenuManager()

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
    */
   protected Control createContents(Composite parent) 
   {
   
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new FillLayout(SWT.VERTICAL));

      // The vertical sashform (controls are set vertically and sash is horizontal)
      SashForm verticalForm = new SashForm(composite, SWT.VERTICAL);
      verticalForm.setLayout(new FillLayout());
      
      // Composite FSW Event Window

      Composite compositeTextFswEventWin = new Composite(verticalForm, SWT.NULL);
      compositeTextFswEventWin.setLayout(new FillLayout());

      Group compositeTextFswEventGroup = new Group(compositeTextFswEventWin, SWT.NULL);
      compositeTextFswEventGroup.setText("FSW Event Messages");
      compositeTextFswEventGroup.setLayout(new FillLayout());

      textFswEventLog = new StyledText( compositeTextFswEventGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      textFswEventLog.setLayout(new FillLayout());


      // Composite Activity Log Window

      Composite compositeTextActivityWin = new Composite(verticalForm, SWT.NULL);
      compositeTextActivityWin.setLayout(new FillLayout());

      Group compositeTextActivityGroup = new Group(compositeTextActivityWin, SWT.NULL);
      compositeTextActivityGroup.setLayout(new FillLayout());
      compositeTextActivityGroup.setText("System Activity");

      textActivityLog = new StyledText( compositeTextActivityGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
     
      // Composite User Input Window

      Composite compositeTextUserInputWin = new Composite(verticalForm, SWT.NULL);
      compositeTextUserInputWin.setLayout(new FillLayout());
      
      Group compositeTextUserInputGroup = new Group(compositeTextUserInputWin, SWT.NULL);
      compositeTextUserInputGroup.setLayout(new FillLayout());
      compositeTextUserInputGroup.setText("User Input");

      Lua = new LuaScriptEngine(appConfig.getProperty(CARE.PROP_APP_LUA_STARTUP));
      
      textUserInput = new Text( compositeTextUserInputGroup, SWT.BORDER);
      textUserInput.setText("<User Command>");
      textUserInput.addKeyListener(new KeyListener() {

         public void keyPressed(KeyEvent e) {
            logger.trace("keyPressed: " + e.character);
         }

         public void keyReleased(KeyEvent event) {
            char inputChar = event.character;
            String key = Character.toString(event.character);
            
            logger.trace("keyReleased: " + event.character + ", numeric value = " + Character.getNumericValue(inputChar));
            
            if (inputChar == SWT.CR) {
               logger.trace("keyReleased: CR");
               String userStr = textUserInput.getText();
               logActivity(CARE.LOG_USER, userStr, false);
               if ( !Lua.evalStr(userStr) ) {
            	   
            	   logActivity(CARE.LOG_USER, Lua.getEvalStatus(), true);
               
               }
            	   
            
               textUserInput.setText("");
            }
            
         }
       });
      
      // resize sashform children and give each child equal space
      verticalForm.setWeights(new int[]{100,100,25});
     
      getToolBarControl().setBackground(
         new Color(getShell().getDisplay(), 230, 230, 230));

      
      //getShell().setImage(new Image(getShell().getDisplay(), ""));
      getShell().setText("CFS Application Runtime Environment (CARE) - Version " + appConfig.getProperty(CARE.PROP_APP_VER));
      
      postGuiCareConstuctor();
      
      return composite;
      
   } // End createContent()

   /***************************************************************************
    ** 
    ** Log Management
    */

    private void logNetworkStatus() {

      String LogString;
      
      if (cmdWriter != null && tlmReader != null)
      {
         LogString = new String("Network Status: "     + "\n   " + 
                                 cmdWriter.getStatus() + "\n   " +
                                 tlmReader.getStatus());
      }
      else
         LogString = new String("Network not initialized");
     
      logActivity(CARE.LOG_CONSOLE, LogString, true);
      
   } // End logNetworkStatus()


  private void logFswEventMessage(String message, EvsApp.EventType eventType) {
      
      StyleRange styleRange1 = new StyleRange();
      styleRange1.start = textFswEventLog.getCharCount();
      styleRange1.length = message.length();
      styleRange1.fontStyle = SWT.NORMAL;
      if ( eventType == EvsApp.EventType.DEBUG || eventType == EvsApp.EventType.INFORMATION) { 
         styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK);         
      }
      else {
         styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
      }

      textFswEventLog.append(message + "\r\n");
      textFswEventLog.setStyleRange(styleRange1);
      //textFswEventLog.setSelection(textFswEventLog.getCharCount());
      
   } // End logFswEventMessage()

   private void logActivity(String subsystem, String message, boolean showInStatusBar) {
      
      String timedMessage = simTime.getLogString() + " - " + subsystem + " - " + message;
      
      logger.trace("logactivity message = " + timedMessage);
      StyleRange styleRange1 = new StyleRange(); 
      styleRange1.start = textActivityLog.getCharCount();
      styleRange1.length = timedMessage.length();
      styleRange1.fontStyle = SWT.NORMAL;
      if (subsystem.charAt(0) == 'U') {
         styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
      }
      else {
         styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }
      textActivityLog.append(timedMessage + "\r\n");
      textActivityLog.setStyleRange(styleRange1);
      
      if(showInStatusBar) {
         setStatus(timedMessage);
      }
      
   } // End logActivity()

   /**
    * Write an error message to the activity log.  Error messages are always red
    * and always written to the status bar. 
    * 
    * @param subsystem
    * @param message
    */
   private void logActivityError(String subsystem, String message) {
      
      String timedMessage = simTime.getLogString() + " - " + subsystem + " - " + message;
      
      logger.error("logActivityError message = " + timedMessage);
      StyleRange styleRange1 = new StyleRange(); 
      styleRange1.start = textActivityLog.getCharCount();
      styleRange1.length = timedMessage.length();
      styleRange1.fontStyle = SWT.NORMAL;
      styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
      textActivityLog.append(timedMessage + "\r\n");
      textActivityLog.setStyleRange(styleRange1);
      
      setStatus(timedMessage);
      
   } // End logActivity()
   
   /***************************************************************************
    ** 
    ** Time and Telemetry Management
    */
   private class OneHzDisplay implements ISchedulable {
      
      public int getOffset() {
         return 0; 
      }
      public int getPeriod() {
         return 1000;
      }
      
      public void  execute() {
    	 Display.getDefault().syncExec(new Runnable() {
            public void run() {
               logger.trace("OneHzDisplay execute called");
               actionDisplayTime.setText(TIME_STR_PAD+simTime.getLogString());
               //~~textTimeMenu.setText(simTime.getLogString());
               //textTime.setText(simTime.getLogString());
               //logActivity(CARE.LOG_CONSOLE, "One Hz Tick", false);
            } // End run()
    	 }); // End runnable()
      } // End Execute()
      
   } // End class OneHzDisplay
  
   private class proxyMonitor implements ISchedulable {
      
      public int getOffset() {
         return 0; 
      }
      public int getPeriod() {
         return 500;
      }
      
      public void  execute() {
         
     	 Display.getDefault().syncExec(new Runnable() {
             public void run() {
               while (!proxyStatusMsgQ.isEmpty()) {
                  StatusMsg statusMsg = proxyStatusMsgQ.remove();
                  if (statusMsg.getType() == StatusMsg.Type.INFO)
                     logActivity(CARE.LOG_USER, statusMsg.getText(), false);
                  else
                     logActivityError(CARE.LOG_USER, statusMsg.getText());
                  } // End while Q not empty
             } // End run()
     	 }); // End runnable()
      } // End Execute()
      
   } // End class proxyMonitor

   private class TlmMonitor implements ISchedulable, IPktObserver {
     
      public int getOffset() {
         return 0; 
      }
      
      public int getPeriod() {
         return 500;
      }
            
      public void update(int StreamId)
      {
         logger.trace("TlmMonitor update() called for packet " + Integer.toHexString(StreamId));
         tlmUpdated[StreamId] = true;
      }
      
      public void  execute() {
         
         logger.trace("TlmMonitor execute called");
         
     	 Display.getDefault().syncExec(new Runnable() {
             public void run() {
	         synchronized (tlmDatabase) {
	
	            while (!tlmReader.getTlmPktQ().isEmpty()) {
	               
	               CcsdsTlmPkt ccsdsTlmPkt = tlmReader.getTlmPktQ().remove();
	               int streamId = ccsdsTlmPkt.getStreamId();
	               
	               logger.trace("TlmMonitor dequeued packet " + Integer.toHexString(streamId));
	               
	               if (streamId == EvsApp.TLM_MID_EVENT_MSG) {
	                  logFswEventMessage(EvsApp.formatEventStr(ccsdsTlmPkt.getByteArray(),0),EvsApp.getEventType(ccsdsTlmPkt));
	               }
	               TlmDialog tlmDialog = tlmPageManager.getPage(streamId);
	               if (tlmDialog != null) {
	                  
	                  logger.trace("TlmMonitor found " + Integer.toHexString(streamId) + " in tlmPageManager");
	                  tlmDialog.updateValues(ccsdsTlmPkt);
	                  tlmUpdated[streamId] = false;
	               } // End tlmDialog != null
	            } // End while packets in queue
	
	         } // End synchronized block
             } // End run()
     	 }); // End runnable()
      } // End Execute()

   } // End class OneHzDisplay

   /***************************************************************************
   ** 
   ** FSW Application Management
   */

   /**
    *  Return a FswAppProxy for a given app prefix
    */
   public FswAppProxy getFswAppProxy(String appPrefix) {

      FswAppProxy fswAppProxy = null;
      
      Iterator<FswAppProxy> AppProxyIt = coreAppProxyList.iterator();
      while ( AppProxyIt.hasNext() ) {
         FswAppProxy appProxy = AppProxyIt.next();
         logger.debug("getFswAppProxy() looking for " + appPrefix + " in " + appProxy.getFswXmlApp().getPrefix());
         if (appPrefix.equalsIgnoreCase(appProxy.getFswXmlApp().getPrefix())) {
            logger.debug("Found " + appProxy.getFswXmlApp().getPrefix());
            fswAppProxy = appProxy;
            break;
         }
      } // End AppProxy loop

      return fswAppProxy;
      
   } // End getFswAppProxy()
   
   /**
    * Create an array of strings containing all of the known message IDs. The
    * strings are in a format that is suitable for a GUI drop down list box.
    * 
    * @return  Array of strings containing message IDs
    */
   public String[] createMsgIdStrArray() {

      ArrayList<String>  msgIdArrayList = new ArrayList<String>();
      
      Iterator<FswAppProxy> appProxyIt = coreAppProxyList.iterator();
      while ( appProxyIt.hasNext() ) {
         FswAppProxy appProxy = appProxyIt.next();
         String[] appMsgIdStrArray = appProxy.getTlmMsgIdNameList();
         logger.trace("createMsgIdStrArray(): appMsgIdStrArray.length = " + appMsgIdStrArray.length);
         for (int i=0; i < appMsgIdStrArray.length; i++) {
            if (appMsgIdStrArray[i] != null ) {
               msgIdArrayList.add(appMsgIdStrArray[i]);
               logger.trace("createMsgIdStrArray() adding " + appMsgIdStrArray[i]);
            }
         }
      } // End FswAppProxy loop

      appProxyIt = userAppProxyList.iterator();
      while ( appProxyIt.hasNext() ) {
         FswAppProxy appProxy = appProxyIt.next();
         String[] appMsgIdStrArray = appProxy.getTlmMsgIdNameList();
         for (int i=0; i < appMsgIdStrArray.length; i++) {
            if (appMsgIdStrArray[i] != null ) {
               msgIdArrayList.add(appMsgIdStrArray[i]);
               logger.debug("createMsgIdStrArray() adding " + appMsgIdStrArray[i]);
            }
         }
      } // End FswAppProxy loop

      String[] msgIdStrArray = (String[]) msgIdArrayList.toArray(new String[0]);
      
      return msgIdStrArray;
      
   } // End createMsgIdStrArray()

   /**
    * Load the core applications specified in the properties file. This 
    * must be called after the app's GUI is setup.
    * 
    */
   private void loadCoreApps(String appList) {
      
      String filePath = CARE.PATH_DB_CORE + CARE.PATH_SEP;
      String [] app   = appList.split(",");
      
      for (int i=0; i < app.length; i++) {
         logger.trace("Adding core App["+i+"]=" + app[i] + " Command & Telemetry Menus");
         FswAppProxy appProxy = new FswAppProxy(filePath+app[i]+".xml", tlmPageManager, midConfig, proxyStatusMsgQ);
         coreAppProxyList.add(appProxy);
         MenuManager menuCoreApp = new MenuManager(appProxy.getFswXmlApp().getName());
         menuCoreApp.add(appProxy.getCmdMenu());
         menuCoreApp.add(appProxy.getTblMenu());
         menuCoreApp.add(appProxy.getTlmMenu());
         menuCoreApps.add(menuCoreApp);

      } // End app loop
         
      // Add property-defined core app commands to the tool bar
      String coreToolCmds = appConfig.getProperty(CARE.PROP_APP_CORE_TOOL_CMDS);
      String[] toolCmds = coreToolCmds.split(",");   // Each tool Cmd is specified as a Prefix:FuncCode pair
      
      ToolBarManager toolMenu  = getToolBarManager();
      logger.debug("Tool command property string: " + coreToolCmds);
      for (int i=0; i < toolCmds.length; i++) {
         FswAppProxy appProxy = null;
         String[] cmdPair = toolCmds[i].split(":");
         logger.trace("Adding Tool Command: App["+i+"]=" + cmdPair[0] + ", Function Code = " + cmdPair[1]);
         if ( (appProxy = getFswAppProxy(cmdPair[0])) != null) {
            
            logger.debug("Found AppProxy");
            FswAppProxy.CmdAction cmdAction = appProxy.getCmdAction(Integer.parseInt(cmdPair[1]));
            if (cmdAction != null) {
               logger.debug("Found cmdAction");
               toolMenu.insertBefore(TOOL_BAR_HELP_ID, (IAction)cmdAction);
               toolMenu.insertBefore(TOOL_BAR_HELP_ID, new Separator());
            }
            else {
               // TODO - Get thread exception. Need general startup message strategy. Can Proxy msg Q be used? Create local buffer of messages that can be output after GUI created or use proxy message queue
               // logActivityError(CARE.LOG_CONSOLE, "Invalid toolbar app:fc definition " + toolCmds[i] + " specified in properties file.");
            }
         } // End if appPRoxy
           
      } // End toolCmds loop
     
      /*
       *  Now that default apps created the cFE File Proxy can be created. This 
       *  is structured differently than the app proxies. There is only one file
       *  proxy for all of the cFE apps  
       */
      cfeFileProxy = new CfeFileProxy (coreAppProxyList, proxyStatusMsgQ);
      MenuManager fileMenuList[] = cfeFileProxy.getFileMenu();
      for (int i=0; i < fileMenuList.length; i++) {
         if (fileMenuList[i] != null)
            menuCoreFiles.add(fileMenuList[i]);
      } // End fileMenu loop
      
   } // End loadCoreApps()
     
   /***************************************************************************
    ** 
    ** User Application Management
    */

   /**
    * Load the user applications specified in the properties file. This 
    * must be called after the app's GUI is setup.
    * 
    */
   private void loadUserApps(String appList) {
      
      String filePath = CARE.PATH_DB_APPS+CARE.PATH_SEP;
      String [] app   = appList.split(",");

      for (int i=0; i < app.length; i++) {
         logger.trace("Adding user App["+i+"]=" + app[i] + " Command & Telemetry Menus");
         FswAppProxy appProxy = new FswAppProxy(filePath+app[i]+".xml", tlmPageManager, midConfig, proxyStatusMsgQ);
         appProxy.setAppReferences(getShell(), cmdWriter, appConfig.getProperty(CARE.PROP_APP_CFE_FILE));
         userAppProxyList.add(appProxy);
         MenuManager menuUserApp = new MenuManager(appProxy.getFswXmlApp().getName());
         menuUserApp.add(appProxy.getCmdMenu());
         menuUserApp.add(appProxy.getTblMenu());
         menuUserApp.add(appProxy.getTlmMenu());
         menuUserApps.add(menuUserApp);

      } // End app loop
      
   } // End loadUserApps()

   /**
    * Refresh a user application's database definitions. This is used for an 
    * application that has already been loaded. 
    * 
    */
   private void refreshUserApp(int appIndex, FswAppProxy appProxy) {
      
      
      MenuManager menuManager = getMenuManager(menuUserApps, appProxy.getFswXmlApp().getName());
      
      if (menuManager != null) {
         appProxy.setAppReferences(getShell(), cmdWriter, appConfig.getProperty(CARE.PROP_APP_CFE_FILE));
         userAppProxyList.remove(appIndex);
         userAppProxyList.add(appProxy);
    
         menuManager.removeAll();
         menuManager.add(appProxy.getCmdMenu());
         menuManager.add(appProxy.getTblMenu());
         menuManager.add(appProxy.getTlmMenu());

      } // End if found menu manager
      
   } // End refreshUserApps()

   /**
    *  Determine whether a user application is already in the
    *  database.
    *  
    *  @return Index into userAppProxyList if app exists or -1 if app doesn't exist 
    */
   public int userAppExists(String appPrefix) {

      int appExists = -1;
      int appIndex  = 0;
      
      Iterator<FswAppProxy> appProxyIt = userAppProxyList.iterator();
      while ( appProxyIt.hasNext() ) {
         FswAppProxy appProxy = appProxyIt.next();
         logger.debug("userAppExists() looking for " + appPrefix + " in " + appProxy.getFswXmlApp().getPrefix());
         if (appPrefix.equalsIgnoreCase(appProxy.getFswXmlApp().getPrefix())) {
            logger.debug("Found " + appProxy.getFswXmlApp().getPrefix());
            appExists = appIndex;
            break;
         }
         appIndex++;
      } // End AppProxy loop

      return appExists;
      
   } // End userAppExists()
   
} // End class CareApp

