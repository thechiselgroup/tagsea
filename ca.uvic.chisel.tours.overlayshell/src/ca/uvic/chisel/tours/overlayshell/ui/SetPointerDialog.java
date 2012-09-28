package ca.uvic.chisel.tours.overlayshell.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import ca.uvic.chisel.tours.overlayshell.PointerTourElement;
import ca.uvic.chisel.tours.overlayshell.widget.LineShell;

public class SetPointerDialog extends Dialog {

	private PointerTourElement element;
//	private PartActivationListener partActivationListener;
	private MousePointListener mousePointListener;
	private Label startLabel;
	private Label endLabel;
	private Button partRelativeButton;
	
	private class SetButtonSelectionListener implements SelectionListener {
		
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
			
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			SetPointerDialog.this.getShell().setVisible(false);
//			partActivationListener.reset();
			mousePointListener.reset();
			mousePointListener.activate();
		}

		

		
	}
	
//	private class PartActivationListener implements IPartListener {
//		IWorkbenchPart part1 = null;
//		IWorkbenchPart part2 = null;
//
//		@Override
//		public void partActivated(IWorkbenchPart part) {
//			System.out.println("Activated " + part.getSite().getId());		
//			if (part1 == null) {
//				part1 = part;
//			} else {
//				part2 = part;
//			}
//		}
//
//		public void reset() {
//			part1 = null;
//			part2 = null;
//		}
//
//		@Override
//		public void partBroughtToTop(IWorkbenchPart part) {}
//
//		@Override
//		public void partClosed(IWorkbenchPart part) {}
//
//		@Override
//		public void partDeactivated(IWorkbenchPart part) {}
//
//		@Override
//		public void partOpened(IWorkbenchPart part) {}
//		
//	}
	
	private class MousePointListener implements Listener {
		double[] p1 = null;
		double[] p2 = null;
		boolean active = false;
		private LineShell line;
		private Control widget;
		public IWorkbenchPart part1;
		public IWorkbenchPart part2;
		@Override
		public void handleEvent(Event event) {
			if (!active) return;
			Shell workbenchShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			Rectangle bounds = workbenchShell.getBounds();
			if (event.type == SWT.MouseUp && event.button == 1) {
				if (!(event.widget instanceof Control)) {
					return;
				}
				Point dp = ((Control)event.widget).toDisplay(event.x, event.y);
				Point wp = new Point(dp.x - bounds.x, dp.y - bounds.y);
				if (line == null) {
					line = new LineShell(event.display);
					line.setVisible(true);
					line.setRegion(dp.y, dp.x, dp.y, dp.x);
					p1 = new double[] {
						(double)wp.x/bounds.width,
						(double)wp.y/bounds.height
					};
					part1 = PointerTourElement.partUnder(dp);
				} else {
					p2 = new double[] {
						(double)wp.x/bounds.width,
						(double)wp.y/bounds.height
					};
					line.dispose();
					line = null;
					active = false;
					SetPointerDialog.this.getShell().setVisible(true);
					part2 = PointerTourElement.partUnder(dp);
					updateLabels();
				}
			} else if (event.type == SWT.MouseMove) {
				if (line != null && !line.isDisposed()) {
					if (!(event.widget instanceof Control)) {
						return;
					}
					Point dp = ((Control)event.widget).toDisplay(event.x, event.y);
					line.setRegion(line.getTop(), line.getLeft(), dp.y, dp.x);
				}
			} else if (event.type == SWT.KeyUp) {
				if (event.character == SWT.ESC) {
					reset();
					SetPointerDialog.this.getShell().setVisible(true);
				}
			}
		}

		public void reset() {
			p1 = p2 = null;
			if (line != null) {
				if (!line.isDisposed()) {
					line.dispose();
				}
				line = null;
			}
		}
		
		public void activate() {
			reset();
			active = true;
		}
	}

	public SetPointerDialog(Shell parentShell, PointerTourElement element) {
		super(parentShell);
		this.element = element;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = (Composite) super.createDialogArea(parent);
//		this.partActivationListener = new PartActivationListener();
		this.mousePointListener = new MousePointListener();
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(partActivationListener);
		parent.getDisplay().addFilter(SWT.MouseUp, mousePointListener);
		parent.getDisplay().addFilter(SWT.MouseMove, mousePointListener);
		parent.getDisplay().addFilter(SWT.KeyUp, mousePointListener);
		Group coordinatesGroup = new Group(page, SWT.NONE);
		coordinatesGroup.setText("Pointer Coordinates");
		coordinatesGroup.setLayout(new GridLayout());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 200;
		GridDataFactory gdf = GridDataFactory.createFrom(gd);
		Text explanation = new Text(coordinatesGroup, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		explanation.setEnabled(false);
		explanation.setText("This page is used to set the start" +
				" and end point of the pointer indicator. " +
				" The coordinates given are relative to the" +
				" container for each endpoint. The default is" +
				" the workbench window itself. Choose the 'set" +
				" coordinates' button to choose new coordinates. " +
				" click on the workbench to select the endpoints.");
		explanation.setLayoutData(gdf.create());
		startLabel = new Label(coordinatesGroup, SWT.NONE);
		startLabel.setText("Start: (" + (int)(element.getStartX() * 100) + 
				"%, " + (int)(element.getStartY()*100) + ")");
		startLabel.setLayoutData(gdf.create());
		endLabel = new Label(coordinatesGroup, SWT.NONE);
		endLabel.setText("End: (" + (int)(element.getEndX() * 100) + 
				"%, " + (int)(element.getEndY()*100) + "%)");
		endLabel.setLayoutData(gdf.create());
		Button setButton = new Button(coordinatesGroup, SWT.PUSH);
		setButton.setText("Set Coordinates...");
		GridData d = gdf.create();
		d.horizontalAlignment = SWT.END;
		setButton.addSelectionListener(new SetButtonSelectionListener());
		this.partRelativeButton = new Button(coordinatesGroup, SWT.CHECK);
		partRelativeButton.setSelection(element.isPartRelative());
		partRelativeButton.setText("Relative to source and target parts.");
		return page;
	}
	
	protected void updateLabels() {
		if (mousePointListener.p1 != null && mousePointListener.p2 != null) {
			double[] p1 = mousePointListener.p1;
			double[] p2 = mousePointListener.p2;
			startLabel.setText("Start: (" + (int)(p1[0] * 100) + 
					"%, " + (int)(p1[1] * 100) + "%)");
			endLabel.setText("End: (" + (int)(p2[0] * 100) + 
					"%, " + (int)(p2[1] * 100) + "%)");
			((Composite)getContents()).layout();
		}
	}
	
	@Override
	protected void okPressed() {
		if (mousePointListener.p1 != null && mousePointListener.p2 != null) {
			double[] p1 = mousePointListener.p1;
			double[] p2 = mousePointListener.p2;
			double startx = p1[0];
			double starty = p1[1];
			double endx = p2[0];
			double endy = p2[1];
			element.setPartRelative(partRelativeButton.getSelection());
			if (partRelativeButton.getSelection()) {
				//translate the workbench coordinates to part-relative coordinates.
				String part1ID = getPartID(mousePointListener.part1);
				double[] point1 = translateToPart(startx, starty, part1ID);
				String part2ID = getPartID(mousePointListener.part2);
				double[] point2 = translateToPart(endx, endy, part2ID);
				startx = point1[0];
				starty = point1[1];
				endx = point2[0];
				endy = point2[1];
				element.setParts(part1ID, part2ID);
			}
			element.setCoordinates(startx, starty, endx, endy);
			
		}
		super.okPressed();
	}
	
	private String getPartID(IWorkbenchPart part) {
		String partID = PointerTourElement.WORKBENCH_AREA;
		if (part != null) {
			if (part instanceof IEditorPart) {
				partID = PointerTourElement.EDITOR_AREA;
			} else {
				partID = part.getSite().getId();
			}
		} else {
			partID = PointerTourElement.WORKBENCH_AREA;
		}
		return partID;
	}

	private double[] translateToPart(double startx, double starty,
			String partID) {
		Shell window = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Rectangle windowBounds = window.getBounds();
		Rectangle partBounds = PointerTourElement.getPartBounds(partID);
		double displayx = windowBounds.width*startx + windowBounds.x;
		double displayy = windowBounds.height*starty + windowBounds.y;
		double partx = (displayx - partBounds.x)/partBounds.width;
		double party = (displayy - partBounds.y)/partBounds.height;
		return new double[] {partx, party};
	}

	@Override
	public boolean close() {
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().removePartListener(partActivationListener);
		getContents().getDisplay().removeFilter(SWT.MouseUp, mousePointListener);
		getContents().getDisplay().removeFilter(SWT.MouseMove, mousePointListener);
		getContents().getDisplay().removeFilter(SWT.KeyUp, mousePointListener);
		return super.close();
	}

}
