package collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import collections.Predicate;

/**
 * Decorates another <code>Map</code> to validate that additions
 * match a specified predicate.
 * <p>
 * This map exists to provide validation for the decorated map.
 * It is normally created to decorate an empty map.
 * If an object cannot be added to the map, an IllegalArgumentException is thrown.
 * <p>
 * An example of usage would be to ensure that no null keys are added to the map and that all the values in the map are instances of type Integer:
 * <pre>Map map = PredicatedMap.decorate(new HashMap(), NotNullPredicate.INSTANCE, InstanceofPredicate.getInstance(Integer));</pre>
 * It is also possible to specify a PredicatedMap that validates only keys or values, by passing <code>null</code> as any of the arguments in the constructor.
 * <p>
 */
public class PredicatedMap
        extends AbstractInputCheckedMapDecorator
        implements Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 7412622456128415156L;

    /** The key predicate to use */
    protected final Predicate keyPredicate;
    /** The value predicate to use */
    protected final Predicate valuePredicate;

    /**
     * Factory method to create a predicated (validating) map.
     * <p>
     * If there are any elements already in the list being decorated, they
     * are validated.
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    public static Map decorate(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        return new PredicatedMap(map, keyPredicate, valuePredicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies) a <code>map</code> (see super class constructor),
     * sets the <code>keyPredicate</code> and <code>valuePredicate</code>.
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null (inherited from super class constructor)
     */
    protected PredicatedMap(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        super(map);
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;
        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            validate(key, value);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates a key-value pair, via calls to the {@link Predicate#evaluate(Object)} method in 
     * <code>keyPredicate</code> and <code>valuePredicate</code>, when they are set. If a
     * predicate is not set (i.e., was set to <code>null</code> in the constructor {@link PredicatedMap})
     * then it is not checked.
     * 
     * @param key  the key to validate
     * @param value  the value to validate
     * @throws IllegalArgumentException if either the <code>key</code> or the <code>value</code> 
     * is invalid, i.e. the call to <code>evaluate</code> returns <code>false</code>
     */
    protected void validate(Object key, Object value) {
        if (keyPredicate != null && keyPredicate.evaluate(key) == false) {
            throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
        }
        if (valuePredicate != null && valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
        }
    }

    /**
     * Uses the value predicate to validate an object set into the map via <code>setValue</code> in the superclass.
     * Applies the value predicate (set via parameter <code>valuePredicate</code> of constructor {@link PredicatedMap})
     * to the parameter <code>value</code>.
     * 
     * @param value  the value to validate
     * @throws IllegalArgumentException if <code>Predicate.evaluate(Object)</code> returns <code>false</code>
     * @return the same <code>value</code> object
     */
    protected Object checkSetValue(Object value) {
        if (valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
        }
        return value;
    }

    /**
     * Returns true when there is a value predicate.
     * 
     * @return <code>true</code> if a value predicate is in use (i.e., if a non-null predicate was set 
     * via parameter <code>valuePredicate</code> of constructor {@link PredicatedMap};
     * <code>false</code> otherwise.
     */
    protected boolean isSetValueChecking() {
        return (valuePredicate != null);
    }

    //-----------------------------------------------------------------------
    /**
     * If the passed in <code>key</code> and <code>value</code> can be validated, 
     * (using {@link validate}), then this method adds an entry to the
     * underlying map (see {@link Map#put(Object,Object)})
     * 
     * @param key the key of the new entry
     * @param value the value of the new entry 
     */
    public Object put(Object key, Object value) {
        validate(key, value);
        return map.put(key, value);
    }

    /**
     * Iterates through all the elements of a {@link Map} validating its entries (see {@link #validate(Object, Object)}).
     * If all the entries are validated, the entire map is copied into the underlying map (see {@link #putAll(Map)}),
     * otherwise no element is copied.
     * 
     * @param mapToCopy the {@link Map}s
     * @exception IllegalArgumentException raised by {@link #validate(Object, Object)}  
     */
    public void putAll(Map mapToCopy) {
        Iterator it = mapToCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            validate(key, value);
        }
        map.putAll(mapToCopy);
    }

}
