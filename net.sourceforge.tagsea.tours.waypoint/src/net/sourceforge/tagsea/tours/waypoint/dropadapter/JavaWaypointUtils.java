/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.tours.waypoint.dropadapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Utility methods for java waypoints.
 * @author Del Myers
 */

public class JavaWaypointUtils {
	
	/**
	 * The character offset in the file that the waypoint is on.
	 * @param waypoint the waypoint.
	 * @return character offset in the file that the waypoint is on.
	 */
	public static int getOffset(IWaypoint waypoint) {
		return waypoint.getIntValue(IJavaWaypointAttributes.ATTR_CHAR_START, -1);
	}
	
	/**
	 * Returns the formatted date String for the given date as it would appear
	 * inside a Java waypoint in source code. Uses the default locale.
	 * @param date the date to format.
	 * @return the formatted date.
	 */
	public static String getFormattedDate(Date date) {
		return getFormattedDate(date, Locale.getDefault());
	}
	
	
	/**
	 * Returns the formatted date String for the given date as it would appear
	 * inside a Java waypoint in source code. Uses the given locale.
	 * @param date the date to format.
	 * @return the formatted date.
	 */
	public static String getFormattedDate(Date date, Locale loc) {
		String dateString = DateFormat.getDateInstance(DateFormat.SHORT, loc).format(date);
		dateString = loc.getLanguage()+loc.getCountry().toUpperCase()+':'+dateString;
		return dateString;
	}
	/**
	 * The character length in the file that the waypoint is on.
	 * @param waypoint the waypoint.
	 * @return character length in the file that the waypoint is on.
	 */
	public static int getLength(IWaypoint waypoint) {
		int start = getOffset(waypoint);
		int end = getEnd(waypoint);
		if (start >= 0 && end >= 0) {
			return end - start;
		}
		return -1;
	}
	
	public static int getEnd(IWaypoint waypoint) {
		return waypoint.getIntValue(IJavaWaypointAttributes.ATTR_CHAR_END, -1);
	}
	 
	/**
	 * Returns the file that the given waypoint references if it can be found.
	 * @param waypoint the waypoint.
	 * @return the file that the given waypoint references if it can be found.
	 */
	public static IFile getFile(IWaypoint waypoint) {
		String fileName;
		fileName = waypoint.getStringValue(IJavaWaypointAttributes.ATTR_RESOURCE, ""); //$NON-NLS-1$
		if (!"".equals(fileName)) { //$NON-NLS-1$
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
			return file;
		}
		return null;
	}
	
	/**
	 * Sets the file that the given waypoint references. 
	 * @param waypoint the waypoint to change.
	 * @param file the file to set.
	 * @return the status indicating whether or not the set operation occurred.
	 */
	
	public static TagSEAChangeStatus setFile(IWaypoint waypoint, IFile file) {
		return waypoint.setStringValue(IJavaWaypointAttributes.ATTR_RESOURCE, file.getFullPath().toPortableString());
	}
	
	/**
	 * Sets the offset in the file that the given waypoint references. NOTE: this method
	 * is not for use outside of the Java Waypoints platform. It will always fail to apply
	 * the change when used outside the Java Waypoints platform.
	 * @param waypoint the waypoint to change.
	 * @param offset the offset to set.
	 * @return the status of whether or not the change could occurr.
	 */
	public static TagSEAChangeStatus setOffset(IWaypoint waypoint, int offset) {
		return waypoint.setIntValue(IJavaWaypointAttributes.ATTR_CHAR_START, offset);
		
	}
	
	/**
	 * Sets the character in the file that the given waypoint references. NOTE: this method
	 * is not for use outside of the Java Waypoints platform. It will always fail to apply
	 * the change when used outside the Java Waypoints platform.
	 * @param waypoint the waypoint to change.
	 * @param end the end to set.
	 * @return the status of whether or not the change could occurr.
	 */
	public static TagSEAChangeStatus setEnd(IWaypoint waypoint, int end) {
		return waypoint.setIntValue(IJavaWaypointAttributes.ATTR_CHAR_END, end);
	}
	
	/**
	 * Returns a string representation to the given java element for use within
	 * Java Waypoints. Calling this method will return the string representation
	 * that would be placed inside the "Java Element" attribute of a java
	 * waypoint for the given element.
	 * @param element the element to get the string for.
	 * @return the Java Waypoint representation of the element.
	 */
	public static String getStringForElement(IJavaElement element) {
		String result = "";
		switch (element.getElementType()) {
		case IJavaElement.TYPE:
			result = "T:" + getStringForType((IType) element);
			break;
		case IJavaElement.PACKAGE_DECLARATION:
			result = "P:" + getStringForPackage((IPackageDeclaration) element);
			break;
		case IJavaElement.METHOD:
			result = "M:" + getStringForMethod((IMethod)element);
			break;
		case IJavaElement.FIELD:
			result = "F:" + getStringForField((IField)element);
			break;
		case IJavaElement.LOCAL_VARIABLE:
			result = "V:" + getStringForVariable((ILocalVariable)element);
			break;
		case IJavaElement.INITIALIZER:
			result = "I:" + getStringForInitializer((IInitializer)element);
			break;
		default:
			return "";		
		}
		return result;
	}
	
	/**
	 * Returns the java element for the given waypoint if it can be found.
	 * Null otherwise.
	 * @param waypoint
	 * @return the java element for the given waypoint if it can be found.
	 * Null otherwise.
	 */
	public static IJavaElement getJavaElement(IWaypoint waypoint) {
		IFile file = getFile(waypoint);
		if (file != null && file.exists()) {
			ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);
			if (unit != null) {
				int offset = getOffset(waypoint);
				if (offset >= 0) {
					try {
						return unit.getElementAt(offset);
					} catch (JavaModelException e) {
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	private static String getStringForType(IType type) {
		return type.getFullyQualifiedName();
	}

	/**
	 * @param declaration
	 * @return
	 */
	private static String getStringForPackage(IPackageDeclaration declaration) {
		return declaration.getElementName();
	}

	/**
	 * @param method
	 * @return
	 */
	private static String getStringForMethod(IMethod method) {
		try {
			return Signature.toString(method.getSignature(), method.getElementName(), method.getParameterNames(), false, true);
		} catch (JavaModelException e) {
			return "";
		}
	}

	
	
	/**
	 * @param field
	 * @return
	 */
	private static String getStringForField(IField field) {
		try {
			return Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @param variable
	 * @return
	 */
	private static String getStringForVariable(ILocalVariable variable) {
		try {
			return Signature.toString(variable.getTypeSignature()) + " " + variable.getElementName();
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @param initializer
	 * @return
	 */
	private static String getStringForInitializer(IInitializer initializer) {
		return initializer.getElementName();
	}
	
}
