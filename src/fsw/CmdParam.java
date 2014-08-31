package fsw;

/**
 * Abstract base class for defining a command parameter.
 * 
 * Design Notes:
 *   1. This class is intended to be used as an interface to a class providing a user interface such as
 *      a GUI or a script. Therefore the user interface will be a string. On the 'back end' is a byte
 *      array to the communications to the CFS. Since these interface types are fixed an abstract base
 *      class can provide methods for the interfaces and the concrete subclasses would provide the conversion
 *      implementation for each data type.
 *   2. When I hard coded some defaults the string constructor seemed odd but if this project expands to use 
 *      a text based database then it would also use text for the constructor so the design may be okay.
 *   3. Generics didn't fit the problem being solved because they are typically used in containers when the
 *      different types are being passed across a class interface.
 *   4. TODO - Add support for variable types, fields widths etc. Read-only?
 * 
 * @author David McComas
 *
 */
public abstract class CmdParam {

   public enum ParamType { UNDEF, UINT, INT, STR };

   protected String    Name;
   protected ParamType Type;
   protected String    Value;
   protected int       NumBytes;    
   protected byte[]    ByteArray;
   
 /**
  * Construct a command parameter. Note the default value is a string and the
  * subclass (based on parameter type) will create the binary value.   
  * 
  * @param Name      Name of the parameter. It will be used in user interfaces.
  * @param Type      Type of parameter
  * @param DefValue  String containing the default value
  * @param NumBytes  Number of bytes
  */
  public CmdParam(String Name, ParamType  Type, String DefValue, int NumBytes)
   {
      this.Name = Name;
      this.Type = Type;
      this.Value    = DefValue;
      this.NumBytes = NumBytes;
      ByteArray = new byte[NumBytes];
      loadByteArray();
      
   } // End CmdParam()
   
  /**
   * Return the command parameter name.
   * 
   * @return String containing the parameter name
   */
   public String getName() {
      
      return Name;
   
   } // End getName()

   /**
    * Set the command parameter name.
    * 
    * @param Name  String containing the parameter name
    */
   public void setName(String Name) {
       
      this.Name = Name;
   
   } // End setName()

   /**
    * Return the enumerated parameter type.
    * 
    * @return  Parameter enumerated type.
    */
   public ParamType getType() {
      
      return Type;
   
   } // End getType()
 
   /**
    *  Return a byte array containing the parameter data. Each subclass type
    *  must provide the conversions for the particular parameter type.
    *  
    * @return  Byte array containing the parameter data.
    */
   protected abstract byte[] loadByteArray();   // Load byte array using current Value. Intent is an internal helper function
  

   /**
    * Return a byte array containing the parameter data.
    * 
    * @return Byte array containing the parameter data.
    */
   public byte[] getByteArray() {
      
      return ByteArray;
   
   } // End getByteArray()

   /**
    * Set the parameter value.
    * 
    * @param value String containing the parameter value.
    * @return Byte array containing the parameter data.
    */
   public byte[] setValue(String value) {
      
      Value = value;
      return loadByteArray();
      
   } // End setValue()
  
   /**
    * Return a string containing the current parameter value.
    *  
    * @return String containing the current parameter value.
    */
   public String getValue() {
      
      return Value;
      
   } // End setValue()

   
   /**
    *  Return the number of bytes used to store the parameter. Can only get the
    *  number of bytes and not set them. The number of bytes should never 
    *  change once the parameter is created.
    *  
    * @return  The number of bytes used to store the parameter
    */
   public int getNumBytes() {
      
      return NumBytes;
      
   } // End getNumBytes()

} // End class CmdParam
