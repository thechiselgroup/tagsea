/**
 * <copyright>
 * </copyright>
 *
 * $Id: ViewEventType.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>View Event Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getViewEventType()
 * @model
 * @generated
 */
public final class ViewEventType extends AbstractEnumerator {
	/**
	 * The '<em><b>Opened</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Opened</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OPENED_LITERAL
	 * @model name="Opened"
	 * @generated
	 * @ordered
	 */
	public static final int OPENED = 0;

	/**
	 * The '<em><b>Top</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Top</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TOP_LITERAL
	 * @model name="Top"
	 * @generated
	 * @ordered
	 */
	public static final int TOP = 1;

	/**
	 * The '<em><b>Activated</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Activated</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ACTIVATED_LITERAL
	 * @model name="activated"
	 * @generated
	 * @ordered
	 */
	public static final int ACTIVATED = 2;

	/**
	 * The '<em><b>Deactivated</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Deactivated</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEACTIVATED_LITERAL
	 * @model name="deactivated"
	 * @generated
	 * @ordered
	 */
	public static final int DEACTIVATED = 3;

	/**
	 * The '<em><b>Hidden</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Hidden</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #HIDDEN_LITERAL
	 * @model name="hidden"
	 * @generated
	 * @ordered
	 */
	public static final int HIDDEN = 4;

	/**
	 * The '<em><b>Closed</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Closed</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CLOSED_LITERAL
	 * @model name="closed"
	 * @generated
	 * @ordered
	 */
	public static final int CLOSED = 5;

	/**
	 * The '<em><b>Filtered</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Filtered</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FILTERED_LITERAL
	 * @model name="filtered"
	 * @generated
	 * @ordered
	 */
	public static final int FILTERED = 6;

	/**
	 * The '<em><b>Hierarchy</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Hierarchy</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #HIERARCHY_LITERAL
	 * @model name="hierarchy"
	 * @generated
	 * @ordered
	 */
	public static final int HIERARCHY = 7;

	/**
	 * The '<em><b>Opened</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPENED
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType OPENED_LITERAL = new ViewEventType(OPENED, "Opened", "Opened");

	/**
	 * The '<em><b>Top</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TOP
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType TOP_LITERAL = new ViewEventType(TOP, "Top", "Top");

	/**
	 * The '<em><b>Activated</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ACTIVATED
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType ACTIVATED_LITERAL = new ViewEventType(ACTIVATED, "activated", "activated");

	/**
	 * The '<em><b>Deactivated</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEACTIVATED
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType DEACTIVATED_LITERAL = new ViewEventType(DEACTIVATED, "deactivated", "deactivated");

	/**
	 * The '<em><b>Hidden</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #HIDDEN
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType HIDDEN_LITERAL = new ViewEventType(HIDDEN, "hidden", "hidden");

	/**
	 * The '<em><b>Closed</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CLOSED
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType CLOSED_LITERAL = new ViewEventType(CLOSED, "closed", "closed");

	/**
	 * The '<em><b>Filtered</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FILTERED
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType FILTERED_LITERAL = new ViewEventType(FILTERED, "filtered", "filtered");

	/**
	 * The '<em><b>Hierarchy</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #HIERARCHY
	 * @generated
	 * @ordered
	 */
	public static final ViewEventType HIERARCHY_LITERAL = new ViewEventType(HIERARCHY, "hierarchy", "hierarchy");

	/**
	 * An array of all the '<em><b>View Event Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ViewEventType[] VALUES_ARRAY =
		new ViewEventType[] {
			OPENED_LITERAL,
			TOP_LITERAL,
			ACTIVATED_LITERAL,
			DEACTIVATED_LITERAL,
			HIDDEN_LITERAL,
			CLOSED_LITERAL,
			FILTERED_LITERAL,
			HIERARCHY_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>View Event Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>View Event Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ViewEventType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ViewEventType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>View Event Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ViewEventType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ViewEventType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>View Event Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ViewEventType get(int value) {
		switch (value) {
			case OPENED: return OPENED_LITERAL;
			case TOP: return TOP_LITERAL;
			case ACTIVATED: return ACTIVATED_LITERAL;
			case DEACTIVATED: return DEACTIVATED_LITERAL;
			case HIDDEN: return HIDDEN_LITERAL;
			case CLOSED: return CLOSED_LITERAL;
			case FILTERED: return FILTERED_LITERAL;
			case HIERARCHY: return HIERARCHY_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private ViewEventType(int value, String name, String literal) {
		super(value, name, literal);
	}

} //ViewEventType
