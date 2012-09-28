package ca.uvic.chisel.tours.overlayshell.widget;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A shell that has a region to make it look like a line.
 * 
 * 
 * @author Del Myers
 */
public class LineShell {
	
	private Shell shell;
	private Region region;
	private int top;
	private int left;
	private int bottom;
	private int right;

	public LineShell(Display display) {
		this.shell = new Shell(display, SWT.ON_TOP | SWT.MODELESS | SWT.NO_TRIM);
		this.shell.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		this.shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		this.shell.setEnabled(false);
		this.shell.open();
		this.shell.setVisible(false);
		this.shell.setAlpha(150);
		this.region = new Region(display);
	}
	
	
	public void dispose() {
		shell.dispose();
		region.dispose();
	}

	/**
	 * Sets the endpoints for the line
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	public void setRegion(int top, int left, int bottom, int right) {
		checkWidget();
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		int thick = 40;
		int ht = thick/2;
		//translate to euclidean space
		//top = -top;
		//bottom = -bottom;
		if (!region.isDisposed()) {
			region.dispose();
			region = new Region(shell.getDisplay());
		}
		double distance = Math.sqrt((bottom-top)*(bottom-top) + (right-left)*(right-left));
		//set up a point array for rotation
		double points[] = {
			0, ht,
			distance-thick, ht,
			distance-thick, thick,
			distance, 0,
			distance-thick, -thick,
			distance-thick, -ht,
			0, -ht
		};
		int rise = bottom-top;
		int run = right - left;
		double theta = 0;
		if (rise == 0) {
			if (run < 0) {
				theta = Math.PI;
			}
		} else if (run == 0) {
			theta = Math.PI/2;
			if (rise < 0 ) {
				theta *=3;
			}
		} else {
			double tan = (double)(bottom-top) / (double)(right-left);
			theta = Math.atan(tan);
			if (run < 0) {
				theta += Math.PI;
			}
		}
		int[] pointArray = new int[points.length];
		int x = 0;
		int y = 1;
		
		//rotate the points, and translate them to 
		//screen coordinates
		int minX, minY, maxX, maxY;
		minX = minY = maxX = maxY = -1;
		for (;y < points.length; x+=2, y+=2) {
			pointArray[x] = (int)(points[x]*Math.cos(theta) - points[y]*Math.sin(theta));
			pointArray[y] = (int)(points[y]*Math.cos(theta) + points[x]*Math.sin(theta));
			pointArray[x] += left;
			pointArray[y] = pointArray[y] + top;
			if (pointArray[x] < minX || minX < 0) {
				minX = pointArray[x];
			}
			if (pointArray[x] > maxX || maxX < 0) {
				maxX = pointArray[x];
			}
			if (pointArray[y] < minY || minY < 0) {
				minY = pointArray[y];
			}
			if (pointArray[y] > maxY || maxY < 0) {
				maxY = pointArray[y];
			}
			
		}
		Rectangle bounds = new Rectangle(minX, minY, maxX-minX+1, maxY-minY+1);
		shell.setBounds(bounds);
		//translate the point array to the shell bounds
		x = 0; y = 1;
		for (;y < points.length; x+=2, y+=2) {
			pointArray[x] -= bounds.x;
			pointArray[y] -= bounds.y;
		}
		region.add(pointArray);
		
		//shell.setRegion(null);
		shell.setRegion(region);
	}
	
	public void setVisible(boolean visible) {
		checkWidget();
		if (visible) {
			shell.open();
			shell.setVisible(true);
		} else {
			shell.setVisible(false);
		}
	}


	private void checkWidget() {
		if (Display.getCurrent() == null) {
			throw new SWTError(SWT.ERROR_THREAD_INVALID_ACCESS);
		} else if (shell.isDisposed() || 
				region.isDisposed()) {
			throw new SWTError(SWT.ERROR_WIDGET_DISPOSED);
		}
		
	}


	public boolean isDisposed() {
		return shell.isDisposed();
	}


	public int getTop() {
		return top;
	}
	public int getLeft() {
		return left;
	}
	public int getBottom() {
		return bottom;
	}
	public int getRight() {
		return right;
	}
	
}
