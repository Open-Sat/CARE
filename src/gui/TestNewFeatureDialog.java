package gui;

import org.eclipse.swt.*; 
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * This is a diagnostic dialog that is helpful to test a new feature. It was
 * originally used to test commands so it takes a command ID and one parameter
 * as input. How ever it can be used to test any new feature that needs a quick 
 * and dirty user input mechanism.
 * 
 * @author David McComas
 *
 */
public class TestNewFeatureDialog extends Dialog
{

   private String   cmdID;
   private String   cmdParam;
   private String   featureStr;
   
   /**
    * Construct a Command Test dialog with hard coded window style.
    * 
    * @param parent  Parent shell
    */
   public TestNewFeatureDialog(Shell parent, String featureStr) {
   
     // Pass the default styles here
     this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, featureStr);
   
   } // End TestNewFeatureDialog()
   
   /**
    * Construct a Command Test dialog with caller supplied window style.
    * 
    * @param parent  Parent shell
    * @param style   Window style
    */
   public TestNewFeatureDialog(Shell parent, int style, String featureStr) {
   
     // Let users override the default styles
     super(parent, style);
     setText("Test a new feature...");
     this.featureStr = featureStr;
   
   } // End TestNewFeatureDialog()

   /**
    * Return the user supplied command identifier.
    *  
    * @return Command indentifier
    */
   public int getCmdID() {
      
     return Integer.parseInt(cmdID);
   
   }

   /**
    * Return the user supplied command parameter 
    * 
    * @return  Command parameter
    */
   public String getCmdParam() {
     return cmdParam;
   }

   /**
    * Open the dialog window.
    * 
    * @return Command Identifier
    */
   public int open() {
     // Create the dialog window
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     createContents(shell);
     shell.pack();
     shell.open();
     Display display = getParent().getDisplay();
     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
     // Return the entered value, or null
     return getCmdID();
     
   } // End open()

   private void createContents(final Shell shell) {

      shell.setLayout(new GridLayout(2, false));

      // Input for project name
      Label featureLabel = new Label(shell, SWT.NONE);
      featureLabel.setText(featureStr);
      GridData data = new GridData();
      data.horizontalSpan = 2;
      featureLabel.setLayoutData(data);

      // Input for project name
      Label projLabel = new Label(shell, SWT.NONE);
      projLabel.setText("Command ID:");
      data = new GridData();
      projLabel.setLayoutData(data);

      // Display the input box
      final Text cmdText = new Text(shell, SWT.BORDER);
      data = new GridData(GridData.FILL_HORIZONTAL);
      cmdText.setLayoutData(data);

      // Input for version
      Label paramLabel = new Label(shell, SWT.NONE);
      paramLabel.setText("Parameter:");
      data = new GridData();
      paramLabel.setLayoutData(data);

      // Display the input box
      final Text paramText = new Text(shell, SWT.BORDER);
      data = new GridData(GridData.FILL_HORIZONTAL);
      paramText.setLayoutData(data);

     // OK button handler
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     data = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(data);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         cmdID = cmdText.getText();
         cmdParam = paramText.getText();
         shell.close();
       }
     });

     // Cancel button handler
     Button cancel = new Button(shell, SWT.PUSH);
     cancel.setText("Cancel");
     data = new GridData(GridData.FILL_HORIZONTAL);
     cancel.setLayoutData(data);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         cmdID = null;
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);
     
   } // End creatContents() 
   
} // End class CmdTestDialog
