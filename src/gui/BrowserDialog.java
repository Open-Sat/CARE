package gui;

import org.eclipse.swt.*; 
import org.eclipse.swt.browser.*;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Browse html files. A good chunk of this code was taken from
 * {@link http://www.eclipse.org/articles/Article-SWT-browser-widget/DocumentationViewer.java}
 * 
 * @author David McComas
 *
 */
public class BrowserDialog extends Dialog
{

   Browser  browser;
   
   /**
    * Construct a browser dialog with hard coded style.
    * 
    * @param parent  Parent shell
    * @param title   Title for browser window
    */
   public BrowserDialog(Shell parent, String title) {
      this(parent, SWT.DIALOG_TRIM, title);  // Pass the default styles here
   }

   /**
    * Construct a browser dialog with caller supplied window style.
    * 
    * @param parent  Parent shell
    * @param style   Window style settings   
    * @param title   Title for browser window
    */
   public BrowserDialog(Shell parent, int style, String title) {
      super(parent, style); // Let users override the default styles
      setText(title);
   }

   /**
    * Open the dialog window and initializes the browser object with the user
    * supplied URL. 
    * 
    * @param url  URL of first HTML page
    */
   public void open(String url) {
     
     // Create the shell
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     
     shell.setLayout(new GridLayout());
     Composite compTools = new Composite(shell, SWT.NONE);
     GridData data = new GridData(GridData.FILL_HORIZONTAL);
     compTools.setLayoutData(data);
     compTools.setLayout(new GridLayout(2, false));
     ToolBar tocBar = new ToolBar(compTools, SWT.NONE);
     ToolItem openItem = new ToolItem(tocBar, SWT.PUSH);
     openItem.setText("Browse");
     ToolBar navBar = new ToolBar(compTools, SWT.NONE);
     navBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
     final ToolItem back = new ToolItem(navBar, SWT.PUSH);
     back.setText("Back");
     back.setEnabled(false);
     final ToolItem forward = new ToolItem(navBar, SWT.PUSH);
     forward.setText("Forward");
     forward.setEnabled(false);

     Composite comp = new Composite(shell, SWT.NONE);
     data = new GridData(GridData.FILL_BOTH);
     comp.setLayoutData(data);
     comp.setLayout(new FillLayout());
     final SashForm form = new SashForm(comp, SWT.HORIZONTAL);
     form.setLayout(new FillLayout());
     
     // Open URL
     try {
        browser = new Browser(form, SWT.NONE);
        browser.setUrl(url);
     } catch (SWTError e) {
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        messageBox.setMessage("Browser cannot be initialized for URL " + url);
        messageBox.setText("Exit");
        messageBox.open();
        System.exit(-1);
     }

     back.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event event) {
           browser.back();
        }
     });
     forward.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event event) {
           browser.forward();
        }
     });
     final LocationListener locationListener = new LocationListener() {
        public void changed(LocationEvent event) {
           Browser browser = (Browser)event.widget;
           back.setEnabled(browser.isBackEnabled());
           forward.setEnabled(browser.isForwardEnabled());
        }
        public void changing(LocationEvent event) {
        }
     };
     
     browser.addLocationListener(locationListener);
     // Create the dialog window
     //createContents(shell);
     //shell.pack();
     shell.open();
     Display display = getParent().getDisplay();

     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
   } // End open()

   
   /**
    * Create the dialog window contents.
    * 
    * @param shell Parent shell
    */
   private void createContents(final Shell shell) {

      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      shell.setLayout(gridLayout);

      StyledText text = new StyledText( shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      GridData data = new GridData(GridData.FILL);
      text.setLayoutData(data);
      text.setText(browser.getText());

      // OK button handler
      Button ok = new Button(shell, SWT.PUSH);
      ok.setText("OK");
      data = new GridData(GridData.CENTER);
      ok.setLayoutData(data);
      ok.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            shell.close();
         }
         });

     shell.setDefaultButton(ok);

   } // End createContents()
   
} // End class BrowserDialog
