package util;

import java.util.ArrayList;    
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

/***
 * 
 * Schedule activities for the application. Only one Schedule should be created
 * but the singleton design pattern was not explicitly used so there are no
 * protections against multiple schedulers.
 *
 * @author David McComas
 * 
 */
public class Scheduler {
   
   private static Logger logger=Logger.getLogger(Scheduler.class);
   
   private int      tick;             // Time between ticks in milliseconds
   private int      slotCnt;
   private int      currSlot;         // Current execution slot
   private boolean  invalidInitTick;  //Set to true if user constructed this class with an invalid tick 
   private SimTime  timeKeeper;
   private Thread   thread;

   private boolean  oneHzCycle = false;

   private ArrayList<ScheduleItem> scheduleList = new ArrayList<ScheduleItem>();
   
   /***
    *  
    *  Construct a scheduler.
    *  
    *  @param appTimeKeeper  object that manages simulated time
    *  @param tickDuration   length in milliseconds of a closk tick. Must be
    *                        <= 1000ms and must go evenly into 1000   
    */
   public Scheduler (SimTime appTimeKeeper, int tickDuration) {
      
      oneHzCycle = false;
      timeKeeper = appTimeKeeper;
      invalidInitTick = false;
      
      if (tickDuration > 1000) {
         tick    = 1000;
         invalidInitTick = true;
      }
      else {

         if ( (1000 % tickDuration) != 0 ) {
   
            invalidInitTick = true;
            // Create a valid tick duration by incrementing user supplied value
            // until hit a number that evenly goes into 1000
            for (int i=tickDuration; i <= 1000 ;i++) {
               if (1000 % i == 0) {
                  tick = i;
                  break;
               }
            }
            logger.error("Scheduler: Invalid user tick. auto set to " + tick);
         }
         else {

            tick    = tickDuration;
            
         } // End if valid tickDuration
      } // End if less than 1000
         
      slotCnt = 1000/tick;
      
      
   } // End Scheduler()

  /***
   * Start the scheduler thread.
   */
   public void startThread() {
      
      thread = new Thread(new Runnable() {
         public void run() {
            logger.info("Scheduler thread started with tick="+ tick + ", slotcnt="+slotCnt);
            while (true) {
               //Display.getDefault().asyncExec(new Runnable() {
               //   public void run() {
                     try {
                        /*
                         * A couple of notes about this logic:
                         * 1. currSlot is incremented first so if someone queries it during the slot period the
                         *    count reflects the actual slot
                         * 2. currSlot is not initially set to -1 to delay the first 1Hz from occurring on the 
                         *    first execution cycle
                         * 3. The thread sleep time does not take into account the elapsed time of processScheduleList() 
                         */
                        logger.trace("Start tick thread loop. currSlot = " + currSlot);
                        currSlot++;
                        oneHzCycle = timeKeeper.Tick();
                        if (oneHzCycle) {
                           logger.trace("1Hz Tick");
                           currSlot = 0;
                        } // End if 1Hz cycle
                     
                        processScheduleList();
                        Thread.sleep(tick);
                        
                     } // End try
                     catch (Exception e) {
                        
                        System.err.println("Scheduler exception");
                        e.printStackTrace();
                        
                     } // End catch
                 // } // End run()
                 //});
            }// End while true
         } // End run()
      });
      
      thread.setName("Scheduler");
      thread.start();
      
   } // End startThread()
   
   private void processScheduleList() {
      
      ScheduleItem schedulerItem;
      
      logger.trace("Scheduler: Process schedulerList on slot " + currSlot);
      Iterator<ScheduleItem> scheduleIt = scheduleList.iterator();
      while ( scheduleIt.hasNext() ) {
      
         schedulerItem = scheduleIt.next();
         if (schedulerItem.timeToExecute(currSlot)) {
            logger.trace("Scheduler: Executing item");
            schedulerItem.item.execute();
         }
      } // Scheduler item loop

   } // End processScheduleList()
   
   /**
    * Add an item to be scheduled to the scheduler.
    *  
    * @param name  name of the item to e scheduled
    * @param item  entity that implements the {@code}ISchedulabe interface
    */
   public void addSchedulableItem(String name, ISchedulable item) {
   
      logger.trace("Scheduler: Add item " + name + " Period = " + item.getPeriod() + ", Offset= "+ item.getOffset());
      scheduleList.add(new ScheduleItem (name, item));
   
   } // End addSchedulableItem()

   
   /**
    * Remove an item from the scheduler.
    * 
    * @param item entity that implements the {@code}ISchedulabe interface
    */
   public void removeSchedulableItem(ISchedulable item) {
      
      ScheduleItem schedulerItem;
      
      Iterator<ScheduleItem> scheduleIt = scheduleList.iterator();
      while ( scheduleIt.hasNext() ) {
         schedulerItem =  scheduleIt.next();
         if (schedulerItem.getItem() == item){
            scheduleList.remove(schedulerItem);
            break;
         }
      } // While scheduler has entries
      
   }// End removeSchedulableItem()

   /**
    * Generate a report of the items currently in the scheduler and indicate
    * which executions are enabled for execution. 
    * 
    * @return string containing the report
    */
   public String getScheduleList() {
      
      String schedulerReport = "The scheduler is operating with a " + tick + "ms tick and contains the following items:\n";
      ScheduleItem schedulerItem;
      
      Iterator<ScheduleItem> scheduleIt = scheduleList.iterator();
      while ( scheduleIt.hasNext() ) {
      
         schedulerItem = scheduleIt.next();
         schedulerReport = schedulerReport + schedulerItem.getName() + ": Enabled in the following slots:\n";
         boolean[] executionSlot = schedulerItem.getExecutionSlot();
         
         for (int i=0; i < executionSlot.length; i++) {
            schedulerReport = schedulerReport + "   executionSlot["+i+"] = " + executionSlot[i] +"\n";
         }

      } // Scheduler item loop

      return schedulerReport;
      
   } // End getScheduleList()

   
   public int getTick() {
      
      return tick;
      
   } // End getTick()

   public int getSlotCnt () {
      
      return slotCnt;
      
   } // End getSlotCnt()

   public boolean getInvalidInitTick () {
      
      return invalidInitTick;
      
   } // End getInvalidInitTick()


   /**
    * Inner class used to contain/manage a ISchedulable item
    *
    * A goal of this program is to remain simple and complex scheduling should
    * not be required. These algorithms simply make things work and status is
    * returned to the user.  
    *
    */
   private class ScheduleItem {
    
      private String       name;
      private ISchedulable item;
      private boolean[]    executionSlot;
      private int          offsetSlot;     // Execution slot index (converted from offset period)
       
   public ScheduleItem (String schedulableName, ISchedulable schedulableItem) {
      
      name = schedulableName;
      item = schedulableItem;
      executionSlot = new boolean[slotCnt];
      
      // Integer division should return closest whole number slot
      if (item.getOffset() < 1000) {
         offsetSlot = item.getOffset()/tick;  
      }
      else{
         offsetSlot = (item.getOffset() % 1000) /tick;
      }
      
      for (int i=0; i < executionSlot.length; i++) {
         executionSlot[i] = false;
      }
      
      executionSlot[offsetSlot] = true;
      
      if (item.getPeriod() <= tick) {
         for (int i=0; i < executionSlot.length; i++) {
            executionSlot[i] = true;
         }
      } // End Item's period is faster than scheduler so every slot is used
      else {
         // TODO - Add logic to schedule items with periods > tick
      } // End if Item's period is slower 
         
      // TODO - Add logic to schedule long period items

      logger.trace("SchedulableItem: Add item " + name + " Offset slot = " + offsetSlot + ", Execution slots = ");
      for (int i=0; i < executionSlot.length; i++) {
         logger.trace("   executionSlot["+i+"] = " + executionSlot[i]);
      }
      
   } // End ScheduleItem()

   public boolean timeToExecute(int currSlot) {
      
      boolean timeToExecute = false;
      
      if (currSlot < executionSlot.length){
         
         timeToExecute = executionSlot[currSlot]; 
      
      }
      
      return timeToExecute;
      
   } // End timeToExecute()

   public ISchedulable getItem() {
      
      return item;

   } // End getItem()
   

   public String getName() {
      
      return name;

   } // End getName()
   
   public boolean[] getExecutionSlot() {
      
      return executionSlot;

   } // End getExecutionSlot()

} // End class ScheduleItem

} // End Class Scheduler


 