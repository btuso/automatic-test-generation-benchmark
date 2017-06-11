package collections.comparators;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** 
 * A Comparator which imposes a specific order on a specific set of Objects.
 * Objects are presented to the FixedOrderComparator in a specified order and
 * subsequent calls to {@link #compare(Object, Object) compare} yield that order.
 * For example:
 * <pre>
 * String[] planets = {"Mercury", "Venus", "Earth", "Mars"};
 * FixedOrderComparator distanceFromSun = new FixedOrderComparator(planets);
 * Arrays.sort(planets);                     // Sort to alphabetical order
 * Arrays.sort(planets, distanceFromSun);    // Back to original order
 * </pre>
 * <p>
 * Once <code>compare</code> has been called, the FixedOrderComparator is locked
 * and attempts to modify it yield an UnsupportedOperationException.
 * <p>
 * 
 */
public class FixedOrderComparator implements Comparator {

    /** 
     * Behavior when comparing unknown Objects:
     * unknown objects compare as coming before known Objects.
     */
    public static final int UNKNOWN_BEFORE = 0;

    /** 
     * Behavior when comparing unknown Objects:
     * unknown objects compare as coming after known Objects.
     */
    public static final int UNKNOWN_AFTER = 1;

    /** 
     * Behavior when comparing unknown Objects:
     * unknown objects cause a IllegalArgumentException to be thrown.
     * This is the default behavior.
     */
    public static final int UNKNOWN_THROW_EXCEPTION = 2;

    /** Internal map of object to position */
    private final Map map = new HashMap();
    /** Counter used in determining the position in the map */
    private int counter = 0;
    /** Is the comparator locked against further change */
    private boolean isLocked = false;
    /** The behaviour in the case of an unknown object */
    private int unknownObjectBehavior = UNKNOWN_THROW_EXCEPTION;

    // Constructors
    //-----------------------------------------------------------------------
    /** 
     * Constructs an empty FixedOrderComparator.
     */
    public FixedOrderComparator() {
        super();
    }

    /** 
     * Constructs a FixedOrderComparator which uses the order of the given array
     * to compare the objects.
     * <p>
     * The elements of the array are stored locally (see {@link #add(Object)}).
     * 
     * @param items  the array of items that the comparator can compare in order
     * @throws IllegalArgumentException if the array <code>items</code> is <code>null</code>
     */
    public FixedOrderComparator(Object[] items) {
        super();
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        for (int i = 0; i < items.length; i++) {
            add(items[i]);
        }
    }

    /** 
     * Constructs a FixedOrderComparator which uses the order of the given list
     * to compare the objects.
     * <p>
     * The elements of the list are stored locally (see {@link #add(Object)}).
     * 
     * @param items  the {@link List} of items that the comparator can compare in order
     * @throws IllegalArgumentException if the {@link List} is <code>null</code>.
     */
    public FixedOrderComparator(List items) {
        super();
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        for (Iterator it = items.iterator(); it.hasNext();) {
            add(it.next());
        }
    }

    // Bean methods / state querying methods
    //-----------------------------------------------------------------------
    /**
     * Returns true if modifications cannot be made to the FixedOrderComparator.
     * FixedOrderComparators cannot be modified once they have performed a comparison.
     * 
     * @return true if modifications cannot be made to this comparator, false if it can be changed.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Checks to see whether the comparator is now locked against further changes.
     * 
     * @throws UnsupportedOperationException if the comparator is locked
     */
    protected void checkLocked() {
        if (isLocked()) {
            throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
        }
    }

    /** 
     * Gets the behavior for comparing unknown objects, which is
     * UNKNOWN_THROW_EXCEPTION by default, and can be set
     * via {@link #setUnknownObjectBehavior(int)}.
     * 
     * @return the value for unknown behaviour - UNKNOWN_AFTER,
     * UNKNOWN_BEFORE or UNKNOWN_THROW_EXCEPTION
     */
    public int getUnknownObjectBehavior() {
        return unknownObjectBehavior;
    }

    /** 
     * Sets the behavior for comparing unknown objects.
     * 
     * @param unknownObjectBehavior  the value for unknown behaviour -
     * UNKNOWN_AFTER, UNKNOWN_BEFORE or UNKNOWN_THROW_EXCEPTION
     * @throws UnsupportedOperationException if a comparison has already been performed and the comparator is locked
     * @throws IllegalArgumentException if the parameter <code>unknownObjectBehaviour</code> 
     * is not valid.
     */
    public void setUnknownObjectBehavior(int unknownObjectBehavior) {
        checkLocked();
        if (unknownObjectBehavior != UNKNOWN_AFTER 
            && unknownObjectBehavior != UNKNOWN_BEFORE 
            && unknownObjectBehavior != UNKNOWN_THROW_EXCEPTION) {
            throw new IllegalArgumentException("Unrecognised value for unknown behaviour flag");    
        }
        this.unknownObjectBehavior = unknownObjectBehavior;
    }

    // Methods for adding items
    //-----------------------------------------------------------------------
    /** 
     * Adds an item, which compares as greater than all items known to the Comparator.
     * If the item is already known to the Comparator, its old position is
     * replaced with the new position.
     * 
     * @param obj  the {@link Object} to be added to the Comparator.
     * @return <code>true</code> if <code>obj</code> has been added for the first time, 
     * or <code>false</code> if it was already known to the Comparator.
     * @throws UnsupportedOperationException if a comparison has already been made
     * and the comparator is locked.
     */
    public boolean add(Object obj) {
        checkLocked();
        Object position = map.put(obj, new Integer(counter++));
        return (position == null);
    }

    /**
     * Adds a new item, which compares as equal to the given existing item.
     * 
     * @param existingObj  an item already in the Comparator's set of 
     *  known objects
     * @param newObj  an item to be added to the Comparator's set of
     *  known objects
     * @return true if <code>newObj</code> has been added for the first time, false if
     *  it was already known to the Comparator.
     * @throws IllegalArgumentException if <code>existingObject</code> is not in the 
     *  Comparator's set of known objects.
     * @throws UnsupportedOperationException if a comparison has already been made
     * and the comparator is locked.
     */
    public boolean addAsEqual(Object existingObj, Object newObj) {
        checkLocked();
        Integer position = (Integer) map.get(existingObj);
        if (position == null) {
            throw new IllegalArgumentException(existingObj + " not known to " + this);
        }
        Object result = map.put(newObj, position);
        return (result == null);
    }

    // Comparator methods
    //-----------------------------------------------------------------------
    /** 
     * Compares two objects according to the order of this Comparator.
     * @param obj1  the first object to compare
     * @param obj2  the second object to compare
     * @return 
     * 
     * If both <code>obj1</code> and <code>obj2</code> are known to the Comparator,
     * the return value is similar to {@link Integer#compareTo(Integer)}:
     * <ul>
     * <li>Negative if <code>obj1</code> comes before <code>obj2</code> in the fixed order</li>
     * <li>Positive if <code>obj1</code> comes after <code>obj2</code> in the fixed order</li>
     * <li>Zero if <code>obj1</code> and <code>obj2</code> are equal in the fixed order</li></ul>
     * </ul>
     * For example, assuming the order {"Mercury"=0, "Venus"=1, "Earth"=1, "Mars"=2}:
     * <pre>
     * compare("Mercury","Earth")	-> -1	// "Mercury" is less than "Earth"
     * compare("Mars"   ,"Venus")	-> 1	// "Mars" is greater than "Venus"
     * compare("Venus"  ,"Earth")	-> 0	// "Venus" and "Earth" are equal
     * </pre>
     * <p>
     * Otherwise, if at least one object is unknown, the return value is negative, positive or 
     * zero depending on the unknown objects behaviour (set via {@link #setUnknownObjectBehavior(int)}).
     * <p>
     * For example, consider "Saturn", "Uranus" are objects unknown to this Comparator:
     * <ul>
     * <li>If the unknown objects behaviour is <code>UNKNOWN_BEFORE</code>, any unknown object comes before (is less than) any known one:</li>
     * <pre>
     * compare("Saturn","Mars"  )	-> -1 
     * compare("Mars"  ,"Saturn")	-> 1
     * compare("Uranus","Saturn")	-> 0 
     * </pre>
     * <li>If the unknown objects behaviour is <code>UNKNOWN_AFTER</code>, any unknown object comes after (is greater than) any known one:</li>
     * <pre>
     * compare("Saturn","Mars"  )	-> 1
     * compare("Mars"  ,"Saturn")	-> -1
     * compare("Uranus","Saturn")	-> 0 
     * </pre>
     * <li>If the unknown objects behaviour is <code>UNKNOWN_THROW_EXCEPTION</code> (the default), an exception is thrown indicating the unknown object:</li>
     * <pre>
     * compare("Saturn","Mars"  )	-> IllegalArgumentException("Saturn")
     * compare("Mars"  ,"Saturn")	-> IllegalArgumentException("Saturn")
     * compare("Uranus","Saturn")	-> IllegalArgumentException("Uranus")
     * </pre>
     * </ul>
     * <p>
     * @throws IllegalArgumentException if <code>obj1</code> or <code>obj2</code>
     * are not known to this Comparator and the unknown behaviour is <code>UNKNOWN_THROW_EXCEPTION</code>
     * (the unknown object is passed in in the exception string).
     * @throws UnsupportedOperationException if the unknown objects behavior has been set
     * to an invalid value (e.g., via {@link #setUnknownObjectBehavior(int)}).
     */
    public int compare(Object obj1, Object obj2) {
        isLocked = true;
        Integer position1 = (Integer) map.get(obj1);
        Integer position2 = (Integer) map.get(obj2);
        if (position1 == null || position2 == null) {
            switch (unknownObjectBehavior) {
                case UNKNOWN_BEFORE :
                    if (position1 == null) {
                        return (position2 == null) ? 0 : -1;
                    } else {
                        return 1;
                    }
                case UNKNOWN_AFTER :
                    if (position1 == null) {
                        return (position2 == null) ? 0 : 1;
                    } else {
                        return -1;
                    }
                case UNKNOWN_THROW_EXCEPTION :
                    Object unknownObj = (position1 == null) ? obj1 : obj2;
                    throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
                default :
                    throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + unknownObjectBehavior);
            }
        } else {
            return position1.compareTo(position2);
        }
    }

}
