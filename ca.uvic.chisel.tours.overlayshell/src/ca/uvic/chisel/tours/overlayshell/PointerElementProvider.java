package ca.uvic.chisel.tours.overlayshell;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementProvider;

public class PointerElementProvider implements ITourElementProvider {

	@Override
	public ITourElement[] createElements() {
		return new ITourElement[] {new PointerTourElement()};
	}

}
