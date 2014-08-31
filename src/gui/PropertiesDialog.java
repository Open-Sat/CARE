package gui;

import java.util.*; 

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.viewers.*;


public class PropertiesDialog extends Dialog {
   
   private static Logger logger=Logger.getLogger(PropertiesDialog.class);

   private TableViewer  tableViewer;
   private PropertiesLabelProvider propLabelProvider;
   private Properties   propArray[];
   private String       propStrArray[];
   
   /**
    * Constructor that hard codes the window styles. 
    * 
    * @param parent   Parent shell
    * @param prop     Properties object that is being displayed  
    */
   public PropertiesDialog(Shell parent, Properties propArray[], String propStrArray[]) {

      this (parent, SWT.RESIZE | SWT.CLOSE | SWT.MODELESS | SWT.TITLE, propArray, propStrArray);

   } // End PropertiesDialog()

   /**
    * Constructor that allows the caller to override the default styles
    * 
    * @param parent   Parent shell
    * @param style    Window style settings. At a minimum it should include SWT.MODELESS | SWT.TITLE. 
    * @param prop     Properties object that is being displayed           
    */
   public PropertiesDialog(Shell parent, int style, Properties propArray[], String propStrArray[]) {

     super(parent, style);

     this.propArray    = propArray;
     this.propStrArray = propStrArray;

     setText("Properties Display");

   } // End PropertiesDialog()
   
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
           logger.trace("Closing TlmDialog");
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

     shell.setLayout(new GridLayout(1, false));

     Composite composite = new Composite(shell, SWT.NONE);
     composite.setLayout(new GridLayout(1, false));

     Combo combo = new Combo(composite, SWT.READ_ONLY);
     combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

     for (int i=0; i < propArray.length; i++) {
        combo.add(propStrArray[i]);
     }

     // Add a listener to change the tableviewer's input
     combo.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
          update(propArray[((Combo) event.widget).getSelectionIndex()]);
        }
     });

     tableViewer = new TableViewer(composite);
     //tableViewer = new TableViewer(shell);
     tableViewer.setContentProvider(new PropertiesContentProvider());
     propLabelProvider = new PropertiesLabelProvider(propArray[0]);
     tableViewer.setLabelProvider(propLabelProvider);

     Table table = tableViewer.getTable();
     table.setLayoutData(new GridData(GridData.FILL_BOTH));

     // Add the first name column
     TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
     tableColumn.setText("Key");

     tableColumn = new TableColumn(table, SWT.LEFT);
     tableColumn.setText("Value");


     // Turn on the header and the lines
     table.setHeaderVisible(true);
     table.setLinesVisible(true);

     // Create the OK button and add a handler
     // so that pressing it will set input
     // to the entered value
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(gridData);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         shell.close();
       }
     });

     combo.select(0);
     update(propArray[0]);
     // Pack the columns
     for (int i = 0, n = table.getColumnCount(); i < n; i++) {
       table.getColumn(i).pack();
     }
     //tableViewer.setInput(propArray[0]);
     shell.setDefaultButton(ok);


   } // createContents()
   
   /**
    * Updates the GUI with the selected properties.
    * 
    * @param prop  The Properties object
    */
   private void update(Properties prop) {

     propLabelProvider.setProperties(prop);
     tableViewer.setInput(prop);
   
   } // End update()

} // End class PropertiesDialog

