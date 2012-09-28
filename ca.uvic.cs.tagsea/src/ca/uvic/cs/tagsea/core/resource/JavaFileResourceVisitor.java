package ca.uvic.cs.tagsea.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author mdesmond
 */
public class JavaFileResourceVisitor implements IResourceVisitor
{
	private List<IResource> fResources;
	private IProgressMonitor fMonitor;
	
	public JavaFileResourceVisitor(IProgressMonitor monitor)
	{
		fMonitor = monitor;
	}
	
	public List<IResource> getResources() 
	{
		if(fResources == null)
			fResources = new ArrayList<IResource>();
		
		return fResources;
	}
	
	public boolean visit(IResource resource) throws CoreException
	{
		if (resource.getType() == IResource.PROJECT)
			if(fMonitor!=null)
				fMonitor.worked(1);
		
		if (resource.getType() == IResource.FILE && "java".equalsIgnoreCase(resource.getFileExtension()))
		{
			getResources().add(resource);
			return false;
		}
		
		return true;
	}

}
