package gui;


import org.apache.log4j.Logger;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import fswcore.CfeFile;


/**
 * 
 * Create a simple modeless dialog for displaying cFE files. All cFE
 * files have the same cFE file header.
 * 
 * TODO - Decide whether this dialog is a one time dialog or can remain open
 *        and allow the user to repeatedly dump a file with new values. 
 *        
 * @author David McComas
 *
 */
public class TblTxtFileDialog extends  Dialog {

   private static Logger logger=Logger.getLogger(TblTxtFileDialog.class);

   protected CfeFile      cfeFile;
   
   protected Label[]  hdrLabel;
   protected Text[]   hdrValue;
                   
   /**
    * Constructor that hard codes the window styles. 
    * 
    * @param parent   Parent shell
    * @param fswFile  cFE file that is being displayed           
    */
   public TblTxtFileDialog(Shell parent, CfeFile tblFile) {

      this (parent, SWT.RESIZE | SWT.CLOSE | SWT.MODELESS | SWT.TITLE, tblFile);
   
   } // End TblTxtFileDialog()

   /**
    * Constructor that allows the caller to override the default styles
    * 
    * @param parent          Parent shell
    * @param style           Window style settings. At a minimum it should include SWT.MODELESS | SWT.TITLE. 
    * @param fswFile  cFE file that is being displayed           
    */
   public TblTxtFileDialog(Shell parent, int style, CfeFile tblFile) {

     super(parent, style);

     hdrLabel = new Label[CfeFile.HDR_NUM_FIELDS];
     hdrValue = new Text[CfeFile.HDR_NUM_FIELDS];

     setText("Table Display");

   } // End TblTxtFileDialog()
   
   /**
    * Opens the dialog
    * 
    */
   public void open() {

     // Create the dialog window
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     createContents(shell);
     shell.pack();

     shell.addDisposeListener(new DisposeListener () {
        public void widgetDisposed(DisposeEvent e) {
           logger.trace("Closing Text Table Dialog");
        }
     });

     shell.open();
     /**
     Display display = getParent().getDisplay();
     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
     **/

     
   } // End open()

   /**
    * Creates the dialog's contents
    * 
    * @param shell the dialog window
    */
   protected void createContents(final Shell shell) {
     
     shell.setLayout(new GridLayout(2, false));
      
     createHeaderContents(shell);

     Text fileDataText = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
     GridData gridData = new GridData();
     gridData.horizontalSpan = 2;
     gridData.horizontalAlignment = SWT.FILL;
     gridData.grabExcessHorizontalSpace = true;
     gridData.verticalAlignment = SWT.FILL;
     gridData.grabExcessVerticalSpace = true;
     fileDataText.setLayoutData(gridData);
     logger.trace("userDataStr = " + cfeFile.getUserDataStr());
     fileDataText.setText(cfeFile.getUserDataStr());
     
     // Create the OK button and add a handler
     // so that pressing it will set input
     // to the entered value
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(gridData);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         shell.close();
       }
     });

     // Create the cancel button and add a handler
     // so that pressing it will set input to null
     Button cancel = new Button(shell, SWT.PUSH);
     cancel.setText("Cancel");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     cancel.setLayoutData(gridData);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);

   } // createContents()

   /**
    * Create contents of the file page header. This is private to
    * force all file pages to have the same header look and feel.  
    * 
    * @param shell the dialog window
    */
   protected void createHeaderContents(final Shell shell) {
     
      String[] hdrValArray = cfeFile.getHdrStrArray(); 
     
      for (int i=0; i < CfeFile.HDR_NUM_FIELDS; i++) {
         
         hdrLabel[i] = new Label(shell, SWT.NONE | SWT.CENTER);
         GridData gridData = new GridData();
         gridData.horizontalSpan = 1;
         //gridData.horizontalAlignment = SWT.CENTER;
         hdrLabel[i].setLayoutData(gridData);
         hdrLabel[i].setText(CfeFile.hdrLblStrArray[i]);
         
         hdrValue[i] = new Text (shell, SWT.BORDER);
         gridData = new GridData();
         gridData.horizontalAlignment = SWT.FILL;
         gridData.horizontalSpan = 1;
         //gridData.horizontalAlignment = SWT.CENTER;
         gridData.grabExcessHorizontalSpace = true;
         hdrValue[i].setLayoutData(gridData);
         hdrValue[i].setText(hdrValArray[i]);

     } // End header loop
      
   } // End createHeaderContents()

} // End class TblTxtFileDialog

