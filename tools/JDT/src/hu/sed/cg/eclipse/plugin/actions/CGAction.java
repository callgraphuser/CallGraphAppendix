package hu.sed.cg.eclipse.plugin.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import hu.sed.cg.instrumenter.CGCreator;
import hu.sed.cg.instrumenter.CGVisitor;

public class CGAction implements IObjectActionDelegate {

  private Shell shell;

  /**
   * Constructor for InstrumenterAction.
   */
  public CGAction() {
    super();
  }

  /**
   * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
   */
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    shell = targetPart.getSite().getShell();
  }
  
  
	private void produceDotFile(String path, Map<String, CGVisitor.Pair<Integer, String>> idMapper, Set<CGVisitor.Pair<Integer, Integer>> cg_map) {
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			writer.println("digraph graphname {\nrankdir=\"LR\";");
			for (Map.Entry<String, CGVisitor.Pair<Integer, String>> met : idMapper.entrySet()) {
				writer.println(met.getValue().getLeft() + " [label=\"" + met.getValue().getRight() + "\"]");
			}
			for (CGVisitor.Pair<Integer, Integer> met : cg_map) {
				writer.println(met.getLeft() + " -> " + met.getRight());
			}
			writer.println("}");
			writer.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
	}

  /**
   * @see IActionDelegate#run(IAction)
   */
  public void run(IAction action) {
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setText("Select a file in which the data should be saved...");
    final String path = dialog.open();

    if (path.isEmpty()) {
      return;
    }

    boolean start = MessageDialog.openConfirm(shell, "CG-creator", "Do You really want to build a snow graph?");

    if (!start) {
      return;
    }

    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

    if (window != null) {
      IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

      final CGVisitor visitor = new CGVisitor();
      final CGCreator cgCrator = new CGCreator(visitor);

      for (Object element : selection.toList()) {
        if (element instanceof IAdaptable) {
          IAdaptable adaptable = (IAdaptable) element;
          
          IPackageFragmentRoot selectedRoot = adaptable.getAdapter(IPackageFragmentRoot.class);
          
          try {
			for (final IJavaElement pkg : selectedRoot.getChildren()) {
				  if (pkg != null && pkg instanceof IPackageFragment) {
					  IPackageFragment selectedPackage = (IPackageFragment)pkg;
					  try {
			        	  cgCrator.addUnits(selectedPackage.getCompilationUnits());
			        	  System.out.println(selectedPackage.getElementName());
			          } catch (JavaModelException e) {
			            System.err.println(String.format("Cannot get the compilation units of package (%s).", selectedPackage.getElementName()));
			          }
				  }
			  }
		} catch (JavaModelException e) {
			System.err.println(String.format("Cannot get children of package root (%s).", selectedRoot.getElementName()));
		}
         
         
        }
      }

      final int n = cgCrator.getNumUnits();

      if (n > 0) {
        Job job = new Job("CG-creator") {

          @Override
          protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask("CG-creating", n);

            try {
            	cgCrator.run(monitor);
            } catch (JavaModelException | MalformedTreeException | BadLocationException | IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

            produceDotFile(path, visitor.getNodes(), visitor.getEdges());
            return Status.OK_STATUS;
          }
        };

        job.schedule();
      }
    }
  }

  /**
   * @see IActionDelegate#selectionChanged(IAction, ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection) {
  }

}
