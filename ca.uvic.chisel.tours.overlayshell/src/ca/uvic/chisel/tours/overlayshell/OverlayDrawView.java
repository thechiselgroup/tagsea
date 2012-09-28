package ca.uvic.chisel.tours.overlayshell;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import ca.uvic.chisel.tours.overlayshell.widget.LineShell;

public class OverlayDrawView extends ViewPart {

	private final class DrawListener implements MouseListener, MouseMoveListener {
		boolean armed = false;
		Point start = null;
		@Override
		public void mouseUp(MouseEvent e) {
			armed = false;
			lineShell.setVisible(false);
		}

		@Override
		public void mouseDown(MouseEvent e) {
			Control control = (Control)e.widget;
			if (!armed) {
				start = control.toDisplay(e.x, e.y);
				lineShell.setRegion(start.y, start.x, start.y, start.x);
				lineShell.setVisible(true);
				armed = true;
			} else {
				
			}
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMove(MouseEvent e) {
			Control control = (Control)e.widget;
			
			if (armed) {
				Point end = control.toDisplay(e.x, e.y);
				lineShell.setRegion(start.y, start.x, end.y, end.x);
			}
			
		}
	}

	private LineShell lineShell;

	public OverlayDrawView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		Canvas c = new Canvas(parent, SWT.NONE);
		lineShell = new LineShell(parent.getDisplay());
		c.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		DrawListener listener = new DrawListener();
		c.addMouseListener(listener);
		c.addMouseMoveListener(listener);
	}
	
	@Override
	public void dispose() {
		lineShell.dispose();
	}

	@Override
	public void setFocus() {
		System.out.println("focus");

	}

}
