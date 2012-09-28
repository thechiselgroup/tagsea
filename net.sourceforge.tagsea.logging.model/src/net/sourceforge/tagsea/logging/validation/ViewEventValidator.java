/**
 * <copyright>
 * </copyright>
 *
 * $Id: ViewEventValidator.java,v 1.1 2007/07/30 21:12:09 delmyers Exp $
 */
package net.sourceforge.tagsea.logging.validation;

import net.sourceforge.tagsea.logging.ViewEventType;

/**
 * A sample validator interface for {@link net.sourceforge.tagsea.logging.ViewEvent}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface ViewEventValidator {
	boolean validate();

	boolean validateType(ViewEventType value);
	boolean validateViewid(String value);
	boolean validateFilterString(String value);
}