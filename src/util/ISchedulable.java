package util;

/**
 * Defines an interface for classes who's execution can be managed by the {@link Scheduler}.
 *  
 * @author David McComas
 *
 */
public interface ISchedulable
{

   public int getOffset();  // Offset from the 1 second roll over 
   public int getPeriod();  // Period between executions in milliseconds
   
   public void  execute();
   
} // End interface ISchedulable
