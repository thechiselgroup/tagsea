package ca.uvic.chisel.tours.video.adapters;

import java.util.LinkedList;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;

import ca.uvic.chisel.tours.video.elements.VideoTourElement;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementDropAdapter;

public class TourVideoDropAdapter implements ITourElementDropAdapter {

	@Override
	public ITourElement[] convertDropData(Object data) {
		LinkedList<ITourElement> elements = new LinkedList<ITourElement>();
		
		if (data instanceof String[]) {
			//check file names
			for (String s : ((String[])data)) {
				if (s.toLowerCase().endsWith(".flv")) {
					VideoTourElement tourElement = new VideoTourElement();
					tourElement.setVideoLocation(s);
					elements.add(tourElement);
				}
			}
			
		}
		return elements.toArray(new ITourElement[elements.size()]);
	}

	@Override
	public Transfer getTransfer() {
		return FileTransfer.getInstance();
	}

}
