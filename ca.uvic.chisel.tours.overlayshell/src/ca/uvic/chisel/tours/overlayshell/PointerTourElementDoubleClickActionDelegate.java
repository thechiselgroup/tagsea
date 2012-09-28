package ca.uvic.chisel.tours.overlayshell;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import ca.uvic.chisel.tours.overlayshell.ui.SetPointerDialog;

import com.ibm.research.tours.IDoubleClickActionDelegate;

public class PointerTourElementDoubleClickActionDelegate implements
		IDoubleClickActionDelegate {

	private PointerTourElement element;

	public PointerTourElementDoubleClickActionDelegate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if (element == null) {
			return;
		}
		SetPointerDialog dialog = new SetPointerDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), element);
		dialog.open();
		
	}

	@Override
	public void selectionChanged(ISelection selection) {
		this.element = null;
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection)selection).getFirstElement();
			if (o instanceof PointerTourElement) {
				this.element = (PointerTourElement)o;
			}
		}
		
	}

	@Override
	public void setActivePart(IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

}
