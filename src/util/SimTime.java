package util;

import java.util.Calendar;

import org.apache.log4j.Logger;

import curval.TlmPktDatabase;

/***
 * Manage simulated time. Initialize time with current local time when 
 * constructed.
 * 
 * Log format: YYYY-DOY-HH:MM:ss.mmm
 * 
 * @author David McComas
 */
public class SimTime
{
   /** Useful as a placeholder in text strings before actual time is available */
   static public String DISPLAY_FORMAT= "YYYY-DOY-HH:MM:ss.mmm";
   
   private static Logger logger=Logger.getLogger(SimTime.class);
   
   Calendar calInit;  // Time when class constructed
   int year;
   int doy;
   int hour;
   int min;
   int sec;
   int msec;
   int tick;  // Number of milliseconds in a tick
   String logString;
   
   /***
    * 
    * @param tick number of milliseconds for each time tick 
    */
   public SimTime (int tick) {

      calInit = Calendar.getInstance();
      year = calInit.get(Calendar.YEAR);
      doy  = calInit.get(Calendar.DAY_OF_YEAR);
      hour = calInit.get(Calendar.HOUR_OF_DAY);
      min  = calInit.get(Calendar.MINUTE);
      sec  = calInit.get(Calendar.SECOND);;
      msec = calInit.get(Calendar.MILLISECOND);
      
      this.tick = tick;
      
      logger.trace("SimTime create with tick " + tick + " at time " + getLogString());
      		
   } // End SimTime()

   /*
    * Update Time based on tick value. Assume tick is less
    * than 100ms.
    * 
    * Return whether a second roll over occurred so it can be used as a 1Hz pulse.
    */
   public boolean Tick () {
      
      return Tick (tick);
      
   } // End Tick()

   /**
    * Increment the simulation time by one clock tick.
    * 
    * @param ms  the number of milliseconds to use for the time tick
    * @return    a boolean indicating whether the start of a new 1 second 
    *            period occurred as a reult of the tick
    */
   public boolean Tick (int ms) {
   
      boolean  newSec = false;
      
      msec += tick;
      if (msec >= 1000) {
         
         newSec = true;
         msec = msec % 1000;  // Use modulo in case over 1000
         
         sec++;
         if (sec >= 60) {
            
            sec = 0;
            
            min++;
            if (min >= 60){
               
               min=0;
               
               hour++;
               if (hour >= 24) {
                  
                  hour = 0;
                  
                  doy++;
                  if (isLeapYear(year)) {
                     if (doy >= 366) {
                        doy = 0;
                        year++;
                     } // End doy roll over
                  } // End if leap year
                  else {
                     if (doy >= 365) {
                        doy = 0;
                        year++;
                     } // End doy roll over
                     
                  } // End if not leap year

               } // End hour roll over
            } //End min roll over
         } // End sec roll over
      } //End ms roll over
      
      return newSec;
      
   } // End Tick()

   /**
    * Return the current time using the format: YYYY-DOY-HH:MM:ss.mmm
    * 
    * @return  a string representing the current time
    */
   public String getLogString () {
      
      logString = Integer.toString(year) + "-" +
                  String.format("%03d", doy)  + "-" +
                  String.format("%02d", hour) + ":" +
                  String.format("%02d", min)  + ":" +
                  String.format("%02d", sec)  + "." +
                  String.format("%03d", msec);
      
      return logString;
      
   } // End getTick()
   
   /**
    * Retrieve the current tick value. 
    * 
    * @return value of the current tick in milliseconds.
    */
   public int getTick () {
      
      return tick;
      
   } // End getTick()

   /**
    * Determine whether the supplied year is a leap year.
    * 
    * @param year  year to be evaluated
    * @return      boolean indicating whether the current year is a leap year
    */
   public boolean isLeapYear(int year) {
      
          boolean isLeapYear;

          isLeapYear = (year % 4 == 0);                   // Divisible by 4
          isLeapYear = isLeapYear && (year % 100 != 0);   // Divisible by 4 and not 100
          isLeapYear = isLeapYear || (year % 400 == 0);   // Divisible by 4 and not 100 unless divisible by 400

          return isLeapYear;
          
    } // End isLeapYear()
   
} // End class SimTime
