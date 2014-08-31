package gui;

import org.eclipse.swt.*; 
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import app.CARE;

/**
 * Manage Telemetry Output's (UDP version) packets.
 * 
 * @author dcmccom2
 *
 */
public class ManageToPktsDialog extends Dialog {

   static final public int ENABLE  =  1;
   static final public int DISABLE =  0;
   static final public int CANCEL  = -1;   // Keep as lowest value so caller can take action on any value grater than the cancel value

   private int  msgId;
   private int  config = CANCEL;

   private Button enableButton;
   private Button disableButton;
   private Combo  msgIdCombo;

   /**
    * Construct a manage TO packets dialog. 
    *  
    * @param parent  Parent shell
    */
   public ManageToPktsDialog(Shell parent) {
     
     this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); // Pass the default styles here
     
   } // End NetworkDialog()
   
   /**
    * Constructor that allows the caller to override the default styles.
    * 
    * @param parent  Parent shell
    * @param style   Window style setting
    */
   public ManageToPktsDialog(Shell parent, int style) {
   
     super(parent, style);         // Let users override the default styles
     setText("Manage TO Packets");
   
   } // End NetworkDialog()

   
   /**
    * Get the message ID entered by the user.
    * 
    * @return Message ID
    */
   public int getMsgId() {
      
      return msgId;
      
    } // End getMsgId()

   /**
    * Opens the dialog.
    * 
    * @param ipAddress  IP Address
    * @param cmdPort    Command port number
    * @param tlmPort    Telemetry port number
    * 
    * @return           IP Address
    */
   public int open(String[] msgStrings) {
     
     this.msgId = 0;
     
     // Create the dialog window
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     createContents(shell, msgStrings);
     shell.pack();
     shell.open();
     Display display = getParent().getDisplay();
     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
     
     return config;
     
   } // End open()

   /**
    * Create the window contents.
    * 
    * @param shell Parent shell
    */
   private void createContents(final Shell shell, String[] msgIdStrings) {

      shell.setLayout(new GridLayout(2, false));

      Group radioGroup = new Group(shell, SWT.SHADOW_OUT);
      radioGroup.setText("Packet Configuration");
      radioGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
      GridData gridData = new GridData();
      gridData.horizontalSpan = 2;
      gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
      radioGroup.setLayoutData(gridData);
      enableButton = new Button(radioGroup, SWT.RADIO);
      enableButton.setText("Enable");
      disableButton = new Button(radioGroup, SWT.RADIO);
      disableButton.setText("Disable");
      enableButton.setSelection(true);
      // Input for Message ID
      Label msgLabel = new Label(shell, SWT.NONE);
      msgLabel.setText("Message ID (4 digit hex)");
      gridData = new GridData();
      msgLabel.setLayoutData(gridData);

      // Display the input combo box
      msgIdCombo = new Combo(shell, SWT.DROP_DOWN);
      msgIdCombo.setItems(msgIdStrings);
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      
     // OK button handler
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(gridData);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         msgId  = parseMsgId();
         if (enableButton.getSelection() == true)
            config = ENABLE;
         else
            config = DISABLE;
         shell.close();
       }
     });

     // Cancel button handler
     Button cancel = new Button(shell, SWT.PUSH);
     cancel.setText("Cancel");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     cancel.setLayoutData(gridData);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         msgId  = 0;
         config = CANCEL;
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);
   
   } // End createContents()
   
   private int parseMsgId() {
   
      String msgIdStr = msgIdCombo.getText();
      int    msgIdInt = 0;
      
      
      if (msgIdStr.contains(CARE.STR_GUI_SEP)) {
         // User selected drop down choice so indices are fixed
         msgIdInt = Integer.parseInt(msgIdStr.substring(2,5),16);      
      } 
      else {
         try {
            msgIdInt = Integer.parseInt(msgIdStr,16);
         } catch (Exception e) {
            // Ignore numeric exceptions and msgIdInt will be 0   
         } // End try block
      } // End if  user entry
      
      
      return msgIdInt;
      
   } // End parseMsgId()
   
} // End class ManageToPktsDialog
