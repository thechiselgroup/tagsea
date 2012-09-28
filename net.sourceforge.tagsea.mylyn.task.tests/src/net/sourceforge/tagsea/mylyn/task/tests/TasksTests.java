package net.sourceforge.tagsea.mylyn.task.tests;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import net.sourceforge.tagsea.mylyn.core.LocationDescriptor;
import net.sourceforge.tagsea.mylyn.task.TaskHyperlink;
import junit.framework.TestCase;

public class TasksTests extends TestCase {

	
	
	public void testTaskLink() throws Exception {
	String FILE = "Foo.java";	
	String LINE = "line 1";
	String LOCATION = FILE + " " + LINE;
	String MARKER = "TODO";
	String DESCRIPTION = "Phony task";
		String normalText = MARKER + " " + DESCRIPTION + LocationDescriptor.LOCATON_DELIMINTER + LOCATION;
		String conflictingDeliminator = MARKER + LocationDescriptor.LOCATON_DELIMINTER + " " + DESCRIPTION + LocationDescriptor.LOCATON_DELIMINTER + LOCATION;
		String noLocation = MARKER + " " + DESCRIPTION;
		
		LocationDescriptor descriptor;
		
		descriptor = LocationDescriptor.createFromText(
				TaskHyperlink.LINK_TAG, null, normalText);
		assertEquals(MARKER + " " + DESCRIPTION, descriptor.getDescription());
		assertEquals(LOCATION, descriptor.getLocation());
		
		descriptor = LocationDescriptor.createFromText(
				TaskHyperlink.LINK_TAG, null, conflictingDeliminator);
		assertEquals(MARKER + LocationDescriptor.LOCATON_DELIMINTER + " " + DESCRIPTION, descriptor.getDescription());
		assertEquals(LOCATION, descriptor.getLocation());
		
		descriptor = LocationDescriptor.createFromText(
				TaskHyperlink.LINK_TAG, "", noLocation);
		assertEquals(MARKER + " " + DESCRIPTION, descriptor.getDescription());
		assertNull(descriptor.getLocation());
	}
	
	public void testTaskHyperlink(){
		String text = "Hyperlink";
		IRegion region = new Region(0, text.length()); 
		TaskHyperlink hyperlink = new TaskHyperlink(region, text);
		hyperlink.open();
	}
}
