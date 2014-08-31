package util;

/**
 * TODO - Replaced with log4j Apache library
 * 
 * A utility to help developers debug their code. Debug lines are written to
 * the console using the {@link #printLn} method. Debug statements are 
 * organized according to the package structure. The "PKG_" constant define 
 * he packages. Debug statements also have a level associated with them and 
 * these are defined by the "LVL_" constants.  Only two levels of debugging, 
 * high and low, are defined because more levels only gets murky. Each call to
 * printLn specifies the package and debug level. A developer can change these
 * in the printLn statement  during development or they can set them during 
 * runtime in the CARE properties file. This organization was chosen because
 * debugging usually occurs within a particular functional area rather then 
 * unilaterally across an application.
 * 
 * When using levels think a little backwards.  "HI" means everything will be 
 * logged so if something is unlikely to be a problem then set it as high 
 * because you don't want to see it when a low level of debugging is on and it
 * will mostly not be the source of the issue.  
 * 
 * @author David McComas
 * @version  TODO
 */
 public class Debug {

   /** Turn off a debug print */
   static public final int LVL_DIS = 0; 

   /** High level debugging. All non-disabled debug statements output */
   static public final int LVL_HI  = 1;

   /** Low level debugging. Only low level debug statements output */
   static public final int LVL_LO  = 2;

   /** Limit of a debug level definition (includes disable definition)  */
   static public final int LVL_LMT = 3;
   
   /** Application package debug category */
   static public final int PKG_APP     = 0;
   
   /** CCSDS package debug category */
   static public final int PKG_CCSDS   = 1;
   
   /** Current Value package debug category */
   static public final int PKG_CURVAL  = 2;
   
   /** Flight Software package debug category */
   static public final int PKG_FSW     = 3;
   
   /** Graphical User Interface package debug category */
   static public final int PKG_GUI     = 4;
   
   /** Network package debug category */
   static public final int PKG_NETWORK = 5;
   
   /** Utility package debug category */
   static public final int PKG_UTIL    = 6;
   
   /** Count debug packages */
   static public final int PKG_CNT     = 7;

   // This order must correspond to the PKG_ constants
   static public final String PKG_STR[] = { "APP: ", "CCSDS: ", "CURVAL: ", "FSW: ", "GUI: ", "NETWORK: ", "UTIL: " };
   
   static private boolean sysEnable = false;
   static private int[]   pkgLevel  = new int[PKG_CNT]; 
   
   /**
    * Constructor used to create a Debug object. Only one debug object should
    * be created for the application. The singleton pattern was not explicitly
    * used since this is a small project and no one shoudl abuse it.
    * 
    * @param enable     flag indicating whether all debugging is enabled
    *                   or disabled for the entire system
    * @param pkgStrList string containing comma separated pairs of PKG:LVL 
    *                   strings that determine the initial debug level for
    *                   each package
    */
   public Debug (boolean enable, String pkgStrList) {
      
      sysEnable = enable;

      String[] pkgList = pkgStrList.split(",");

      for (int i=0; i < pkgList.length; i++) {

         String[] pkgDef = pkgList[i].split(":");
         setLevel (Integer.parseInt(pkgDef[0]), Integer.parseInt(pkgDef[1]));

      } // End pkgList loop
      
   } //End Debug
   
   /**
    * Write a debug line to the console.
    * 
    * @param pkg   package identifying the source of the debug message
    * @param level level of the debug message
    * @param text  debug message text
    */
   static public void printLn (int pkg, int level, String text) {
      
      if (sysEnable) {
         
         if (pkg >= 0 && pkg < PKG_CNT) {
         
            if (pkgLevel[pkg] > LVL_DIS && pkgLevel[pkg] <= LVL_LMT)

               if (level >= pkgLevel[pkg]) {
                  System.out.println(PKG_STR[pkg] + text);
               }
         
         } // End if valid pkg
         else {
            System.out.println("*****: Invalid package identifier passed to Debug.printLn()");
         }
     
      } // End if sysEnable
      
   } // End printLn()

   /***
    * Set a package's current debug level.
    * 
    * @param pkg   package identifying the source of the debug message
    * @param level level of the debug message
    */
   static public void setLevel (int pkg, int level) {
      
      if (pkg < PKG_CNT){
         if (level >= LVL_DIS && level <= LVL_LO) {
            pkgLevel[pkg] = level;
         }
         else {
            pkgLevel[pkg] = LVL_DIS;
         }
      } // End if valid pkg
      
   } // End setLevel()

   /***
    * Get the current debug level for a package.
    * 
    * @param pkg   package identifying the source of the debug message
    * @return      integer value of the debug level. See LVL_ definitions
    */
   public int getLevel (int pkg) {

      int level = -1;
      
      if (pkg < PKG_CNT){
         level = pkgLevel[pkg];
      } // End if valid pkg
      
      return level;
   
   } // End getLevel()
   
} // End class Debug
