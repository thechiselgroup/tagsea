package ca.uvic.chisel.tours.overlayshell;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.WorkbenchPartReference;

import ca.uvic.chisel.tours.overlayshell.widget.LineShell;

import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.ITourElement;

public class PointerTourElement extends AbstractTourElement {

	private static final String POINTER_MEMENTO = "pointer.memento";
	private static final String SOURCE_PART = "source.part";
	private static final String PART_ORIENTATION = "part.orientation";
	private static final String START_X = "start.x";
	private static final String START_Y = "start.y";
	private static final String END_X = "end.x";
	private static final String END_Y = "end.y";
	private static final String TARGET_PART = "target.part";
	public static final String EDITOR_AREA = "ca.uvic.chisel.tours.pointerTourElement.editorsArea";
	public static final String WORKBENCH_AREA = "ca.uvic.chisel.tours.pointerTourElement.workbenchArea";
	private String sourceID;
	private String targetID;
	private double startxp, startyp, endxp, endyp;
	private LineShell line;
	private boolean isPartRelative;
	
	public PointerTourElement() {
		this.sourceID = "";
		this.targetID = "";
		this.isPartRelative = false;
	}

	@Override
	public ITourElement createClone() {
		return new PointerTourElement();
	}

	@Override
	public Image getImage() {
		return OverlayPlugin.getDefault().getImageRegistry().get(IOverlayImages.ICON_POINTER);
	}

	@Override
	public String getShortText() {
		return "Pointer";
	}

	@Override
	public String getText() {
		return "Pointer";
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		if (line != null && !line.isDisposed()) {
			line.dispose();
		}
	}

	@Override
	public void transition() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (line == null || line.isDisposed()) {
			line = new LineShell(page.getWorkbenchWindow().getShell().getDisplay());
		}
		Shell shell = page.getWorkbenchWindow().getShell();
		if (!isPartRelative) {
			Rectangle bounds = shell.getBounds();
			Point topLeft = new Point(bounds.x, bounds.y);
			line.setRegion((int)(topLeft.y + bounds.height*startyp),
					(int)(topLeft.x + bounds.width*startxp),
					(int)(topLeft.y + bounds.height*endyp), 
					(int)(topLeft.x + bounds.width*endxp));
			line.setVisible(true);
		} else {
			if (sourceID != null && targetID != null) {
				Rectangle sourceBounds = getPartBounds(sourceID);
				Rectangle targetBounds = getPartBounds(targetID);
				int top = (int)(sourceBounds.y +sourceBounds.height*startyp);
				int left = (int)(sourceBounds.x +sourceBounds.width*startxp);
				int bottom = (int)(targetBounds.y +targetBounds.height*endyp);
				int right = (int)(targetBounds.x +targetBounds.width*endxp);
				line.setRegion(top,left,bottom,right);
				line.setVisible(true);
			}
		}
	}
	
	@Override
	public void load(IMemento memento) {
		super.load(memento);
		IMemento pointerMemento = memento.getChild(POINTER_MEMENTO);
		if (pointerMemento != null) {
			this.sourceID = pointerMemento.getString(SOURCE_PART);
			this.targetID = pointerMemento.getString(TARGET_PART);
			try {
				this.isPartRelative = pointerMemento.getBoolean(PART_ORIENTATION);
			} catch (NullPointerException e) {
				this.isPartRelative = false;
			}
			try {
				startxp = Double.parseDouble(pointerMemento.getString(START_X));
				startyp = Double.parseDouble(pointerMemento.getString(START_Y));
				endxp = Double.parseDouble(pointerMemento.getString(END_X));
				endyp = Double.parseDouble(pointerMemento.getString(END_Y));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.sourceID = "";
			this.targetID = "";
			this.isPartRelative = false;
		}
	}
	
	@Override
	public void save(IMemento memento) {
		IMemento pointerMemento = memento.getChild(POINTER_MEMENTO);
		if (pointerMemento == null) {
			pointerMemento = memento.createChild(POINTER_MEMENTO);
		}
		pointerMemento.putBoolean(PART_ORIENTATION, isPartRelative);
		pointerMemento.putString(SOURCE_PART, sourceID);
		pointerMemento.putString(TARGET_PART, targetID);
		pointerMemento.putString(START_X, startxp + "");
		pointerMemento.putString(START_Y, startyp + "");
		pointerMemento.putString(END_X, endxp + "");
		pointerMemento.putString(END_Y, endyp + "");
		super.save(memento);
	}

	public double getStartX() {
		return startxp;
	}
	
	public double getEndX() {
		return endxp;
	}
	
	public double getStartY() {
		return startyp;
	}
	
	public double getEndY() {
		return endyp;
	}

	public void setCoordinates(double startx, double starty, double endx, double endy) {
		startxp = startx;
		startyp = starty;
		endxp = endx;
		endyp = endy;
		fireElementChangedEvent();
		
	}

	public boolean isPartRelative() {
		return isPartRelative;
	}

	public void setPartRelative(boolean partRelative) {
		isPartRelative = partRelative;
	}

	/**
	 * Returns the display-relative bounds of the given part, if it is
	 * visible in the workbench. Otherwise, the workbench window bounds
	 * are returned.
	 * @param partID
	 * @return
	 */
	@SuppressWarnings("restriction")
	public static Rectangle getPartBounds(String partID) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (partID == null || WORKBENCH_AREA.equals(partID)) {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
		} else if (EDITOR_AREA.equals(partID)) {
			IEditorReference[] references = page.getEditorReferences();
			Rectangle partBounds = null;
			for (IEditorReference reference : references) {
				//there is no api for finding the location of a part. I have to cheat.
				EditorReference cheat = (EditorReference) reference;
				PartPane pane = cheat.getPane();
				if (pane.getControl() == null) continue;
				Rectangle paneBounds = pane.getStack().getBounds();
				paneBounds = new Rectangle(paneBounds.x, paneBounds.y, paneBounds.width, paneBounds.height);
				Point origin = pane.getStack().getControl().getParent().toDisplay(paneBounds.x, paneBounds.y);
				paneBounds.x = origin.x;
				paneBounds.y = origin.y;
				if (partBounds == null) {
					partBounds = paneBounds;
				} else {
					partBounds = partBounds.union(paneBounds);
				}
				
			}
			if (partBounds != null) {
				return partBounds;
			}
		} else {
			WorkbenchPartReference cheat = (WorkbenchPartReference) page.findViewReference(partID);
			if (cheat != null) {
				PartPane pane = cheat.getPane();
				if (pane.getControl() != null) {
					Rectangle partBounds = pane.getStack().getBounds();
					partBounds = new Rectangle(partBounds.x, partBounds.y, partBounds.width, partBounds.height);
					Point origin = pane.getStack().getControl().getParent().toDisplay(partBounds.x, partBounds.y);
					partBounds.x = origin.x;
					partBounds.y = origin.y;
					return partBounds;
				}
			}
		}
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
	}

	public void setParts(String sourcePartID, String targetPartID) {
		sourceID = sourcePartID;
		targetID = targetPartID;
	}

	@SuppressWarnings("restriction")
	public static IWorkbenchPart partUnder(Point displayPoint) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IViewReference reference : page.getViewReferences()) {
			IViewPart part = reference.getView(false);
			if (part == null) continue;
			IViewPart[] stack = page.getViewStack(part);
			if (stack.length < 1 || stack[0] != part) continue;
			WorkbenchPartReference cheat = (WorkbenchPartReference) reference;
			PartPane pane = cheat.getPane();
			Rectangle partBounds = pane.getControl().getBounds();
			partBounds = new Rectangle(partBounds.x, partBounds.y, partBounds.width, partBounds.height);
			Point origin = pane.getControl().getParent().toDisplay(partBounds.x, partBounds.y);
			partBounds.x = origin.x;
			partBounds.y = origin.y;
			if (partBounds.contains(displayPoint)) {
				return part;
			}
		}
		Rectangle editorBounds = getPartBounds(EDITOR_AREA);
		if (editorBounds.contains(displayPoint)) {
			return page.getActiveEditor();
		}
		return null;
	}

}
