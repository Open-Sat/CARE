package util;

/**
 * Simple class to contain status messages that can be logged to the GUI. 
 * 
 * @author David McComas
 *
 */
public class StatusMsg {
   
   static public enum Type {INFO, ERROR}
   
   protected Type   type;
   protected String text;
   
   public StatusMsg (Type type, String text) {
      this.type = type;
      this.text = text;
   }
   
   public Type getType () {
      return type;
   }
   
   public void setType (Type type) {
      this.type = type;
   }
   
   public String getText () {
      return text;
   }

   public void setText (String text) {
      this.text = text;
   }
   
} // End class StatusMsg
