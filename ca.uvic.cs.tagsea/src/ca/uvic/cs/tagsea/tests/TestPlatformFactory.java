/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.tests;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * A small factory to create resourecs and markers for test cases.
 * This should only be used by the test package, that's why it's protected
 * 
 * @author irbull
 */
class TestPlatformFactory {
	
	private static TestPlatformFactory _TestPlatformFactory = null;
	
	String testClass = new String("" + "public class Test {\n"
			+ "   //@tag myTag\n" + "}\n");

	IProject project = null;
	IJavaProject javaProject = null;
	ICompilationUnit compilationUnit = null;
	IResource resource = null;
	IMarker marker = null;
	

	/**
	 * Creates the TestPlatform Factory. This class uses the singleton pattern
	 * @throws Exception
	 */
	private TestPlatformFactory() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				"SimpleTagTestProject");
		project.create(null);
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		javaProject = JavaCore.create(project);

		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		IPackageFragment mypackage = roots[0].createPackageFragment("ca.uvic",
				true, null);
		compilationUnit = mypackage.createCompilationUnit("Test.java", testClass, true, null);
		resource = compilationUnit.getCorrespondingResource();
		marker = resource.createMarker(TagSEAPlugin.MARKER_ID);
		
	}
	
	public static TestPlatformFactory instance() throws Exception {
		if ( _TestPlatformFactory == null ) {
			_TestPlatformFactory = new TestPlatformFactory();
		}
		return _TestPlatformFactory;
	}
	
	public IResource getResource() {
		return this.resource;
	}
	
	public IMarker getMarker() {
		return this.marker;
	}

}
