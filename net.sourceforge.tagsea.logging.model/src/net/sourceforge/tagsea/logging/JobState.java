/**
 * <copyright>
 * </copyright>
 *
 * $Id: JobState.java,v 1.2 2007/07/30 21:12:10 delmyers Exp $
 */
package net.sourceforge.tagsea.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Job State</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see net.sourceforge.tagsea.logging.LoggingPackage#getJobState()
 * @model
 * @generated
 */
public final class JobState extends AbstractEnumerator {
	/**
	 * The '<em><b>Created</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Created</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CREATED_LITERAL
	 * @model name="created"
	 * @generated
	 * @ordered
	 */
	public static final int CREATED = 0;

	/**
	 * The '<em><b>Queued</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Queued</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #QUEUED_LITERAL
	 * @model name="queued"
	 * @generated
	 * @ordered
	 */
	public static final int QUEUED = 1;

	/**
	 * The '<em><b>Waiting</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Waiting</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #WAITING_LITERAL
	 * @model name="waiting"
	 * @generated
	 * @ordered
	 */
	public static final int WAITING = 2;

	/**
	 * The '<em><b>Running</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Running</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RUNNING_LITERAL
	 * @model name="running"
	 * @generated
	 * @ordered
	 */
	public static final int RUNNING = 3;

	/**
	 * The '<em><b>Quiting</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Quiting</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #QUITING_LITERAL
	 * @model name="quiting"
	 * @generated
	 * @ordered
	 */
	public static final int QUITING = 4;

	/**
	 * The '<em><b>Done</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Done</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DONE_LITERAL
	 * @model name="done"
	 * @generated
	 * @ordered
	 */
	public static final int DONE = 5;

	/**
	 * The '<em><b>Created</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CREATED
	 * @generated
	 * @ordered
	 */
	public static final JobState CREATED_LITERAL = new JobState(CREATED, "created", "created");

	/**
	 * The '<em><b>Queued</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #QUEUED
	 * @generated
	 * @ordered
	 */
	public static final JobState QUEUED_LITERAL = new JobState(QUEUED, "queued", "queued");

	/**
	 * The '<em><b>Waiting</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WAITING
	 * @generated
	 * @ordered
	 */
	public static final JobState WAITING_LITERAL = new JobState(WAITING, "waiting", "waiting");

	/**
	 * The '<em><b>Running</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RUNNING
	 * @generated
	 * @ordered
	 */
	public static final JobState RUNNING_LITERAL = new JobState(RUNNING, "running", "running");

	/**
	 * The '<em><b>Quiting</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #QUITING
	 * @generated
	 * @ordered
	 */
	public static final JobState QUITING_LITERAL = new JobState(QUITING, "quiting", "quiting");

	/**
	 * The '<em><b>Done</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DONE
	 * @generated
	 * @ordered
	 */
	public static final JobState DONE_LITERAL = new JobState(DONE, "done", "done");

	/**
	 * An array of all the '<em><b>Job State</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final JobState[] VALUES_ARRAY =
		new JobState[] {
			CREATED_LITERAL,
			QUEUED_LITERAL,
			WAITING_LITERAL,
			RUNNING_LITERAL,
			QUITING_LITERAL,
			DONE_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Job State</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Job State</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static JobState get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			JobState result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Job State</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static JobState getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			JobState result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Job State</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static JobState get(int value) {
		switch (value) {
			case CREATED: return CREATED_LITERAL;
			case QUEUED: return QUEUED_LITERAL;
			case WAITING: return WAITING_LITERAL;
			case RUNNING: return RUNNING_LITERAL;
			case QUITING: return QUITING_LITERAL;
			case DONE: return DONE_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private JobState(int value, String name, String literal) {
		super(value, name, literal);
	}

} //JobState
