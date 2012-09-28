/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModelLog.java,v 1.1 2007/06/15 19:12:07 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Log</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.sourceforge.tagsea.logging.ModelLog#getEvents <em>Events</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getModelLog()
 * @model
 * @generated
 */
public interface ModelLog extends Log {
	/**
	 * Returns the value of the '<em><b>Events</b></em>' containment reference list.
	 * The list contents are of type {@link net.sourceforge.tagsea.logging.ModelEvent}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Events</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Events</em>' containment reference list.
	 * @see net.sourceforge.tagsea.logging.LoggingPackage#getModelLog_Events()
	 * @model type="net.sourceforge.tagsea.logging.ModelEvent" containment="true"
	 * @generated
	 */
	EList getEvents();

} // ModelLog