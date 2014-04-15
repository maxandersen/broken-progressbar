import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * This class demonstrates JFace's ProgressMonitorDialog class
 */
public class ShowProgress extends ApplicationWindow {
  /**
   * ShowProgress constructor
   */
  public ShowProgress() {
    super(null);
  }

  /**
   * Runs the application
   */
  public void run() {
    // Don't return from open() until window closes
    setBlockOnOpen(true);

    // Open the main window
    open();

    // Dispose the display
    Display.getCurrent().dispose();
  }

  /**
   * Configures the shell
   * 
   * @param shell the shell
   */
  protected void configureShell(Shell shell) {
    super.configureShell(shell);

    // Set the title bar text
    shell.setText("Show Progress");
  }

  /**
   * Creates the main window's contents
   * 
   * @param parent the main window
   * @return Control
   */
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));

    // Create the indeterminate checkbox
    final Button useTaskName = new Button(composite, SWT.CHECK);
    useTaskName.setText("Use setTaskName()");

    // Create the ShowProgress button
    Button showProgress = new Button(composite, SWT.NONE);
    showProgress.setText("Show Progress");

    final Shell shell = parent.getShell();

    // Display the ProgressMonitorDialog
    showProgress.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        try {
          new ProgressMonitorDialog(shell).run(true, true,
              new LongRunningOperation(useTaskName.getSelection()));
        } catch (InvocationTargetException e) {
          MessageDialog.openError(shell, "Error", e.getMessage());
        } catch (InterruptedException e) {
          MessageDialog.openInformation(shell, "Cancelled", e.getMessage());
        }
      }
    });

    parent.pack();
    return composite;
  }

  /**
   * The application entry point
   * 
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new ShowProgress().run();
  }
}



/**
 * This class represents a long running operation
 */
class LongRunningOperation implements IRunnableWithProgress {
  // The total sleep time
  private static final int TOTAL_TIME = 500;

  // The increment sleep time
  private static final int INCREMENT = 1;

  private boolean useSetTaskName;

  /**
   * LongRunningOperation constructor
   * 
   * @param indeterminate whether the animation is unknown
   */
  public LongRunningOperation(boolean useSetTaskName) {
    this.useSetTaskName = useSetTaskName;
  }

  /**
   * Runs the long running operation
   * 
   * @param monitor the progress monitor
   */
  public void run(IProgressMonitor monitor) throws InvocationTargetException,
      InterruptedException {
    monitor.beginTask("Running long running operation", TOTAL_TIME);
    for (int total = 0; total < TOTAL_TIME && !monitor.isCanceled(); total += INCREMENT) {
     //Thread.sleep(INCREMENT);
      monitor.worked(INCREMENT);
     
      if (total > TOTAL_TIME / 3) monitor.subTask("setting subtask" + total);
      // removing this line makes it finish instantly on all OS. Keeping it makes it slow on OSX
      if (useSetTaskName) monitor.setTaskName("setTaskname " + total);
      // the click on cancel in dialog never makes it to the monitor - if it did it should cancel instantly.
      if (monitor.isCanceled())
          throw new InterruptedException("The long running operation was cancelled at step "+total);
    }
    monitor.done();
    if (monitor.isCanceled())
        throw new InterruptedException("The long running operation was cancelled");
  }
}
           
       