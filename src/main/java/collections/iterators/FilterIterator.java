package collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import collections.Predicate;

/** 
 * Decorates another {@link Iterator} using a predicate to filter elements.
 * <p>
 * This iterator decorates the underlying iterator, only allowing through
 * those elements that match the specified {@link Predicate Predicate}.
 *
 */
public class FilterIterator implements Iterator {

    /** The iterator being used */
    private Iterator iterator;
    /** The predicate being used */
    private Predicate predicate;
    /** The next object in the iteration */
    private Object nextObject;
    /** Whether the next object has been calculated yet */
    private boolean nextObjectSet = false;

    //-----------------------------------------------------------------------
    /**
     * Constructs a new <code>FilterIterator</code>.
     * Methods {@link #setIterator(Iterator)} and {@link #setPredicate(Predicate)} 
     * must be invoked for this FilterIterator to be functional.
     */
    public FilterIterator() {
        super();
    }

    /**
     * Constructs a new <code>FilterIterator</code> that decorates the passed in <code>iterator</code>.
     * Method {@link #setPredicate(Predicate) setPredicate} must be invoked
     * for this FilterIterator to be functional.
     *
     * @param iterator  the iterator to use
     */
    public FilterIterator(Iterator iterator) {
        super();
        this.iterator = iterator;
    }

    /**
     * Constructs a new <code>FilterIterator</code> that will use the
     * given iterator and predicate.
     *
     * @param iterator  the iterator to use
     * @param predicate  the predicate to use
     */
    public FilterIterator(Iterator iterator, Predicate predicate) {
        super();
        this.iterator = iterator;
        this.predicate = predicate;
    }

    //-----------------------------------------------------------------------
    /** 
     * Returns true if the underlying iterator contains an object that 
     * matches the predicate. This method changes the state of the underlying iterator.
     *
     * @return true if there is another object that matches the predicate.
     */
    public boolean hasNext() {
        if (nextObjectSet) {
            return true;
        } else {
            return setNextObject();
        }
    }

    /** 
     * Returns the next object of the underlying iterator that matches the predicate.
     * If called after {@link #hasNext()}, the returned object is the same 
     * as the one that matched the predicate in {@link #hasNext()}.
     *
     * @return the next object which matches the given predicate
     * @throws NoSuchElementException if there are no more elements that
     *  match the predicate.
     * @throws NullPointerException if no {@link Iterator} has been set yet.
     */
    public Object next() {
        if (!nextObjectSet) {
            if (!setNextObject()) {
                throw new NoSuchElementException();
            }
        }
        nextObjectSet = false;
        return nextObject;
    }

    /**
     * Removes from the underlying collection of the base iterator the last
     * element returned by this iterator (see {@link Iterator#remove()}).
     * 
     * @throws IllegalStateException if {@link #hasNext()} has already
     *  been called, as this changes the underlying iterator.
     * @throws NullPointerException if no {@link Iterator} has been set yet.
     */
    public void remove() {
        if (nextObjectSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        iterator.remove();
    }

    //-----------------------------------------------------------------------
    /** 
     * Gets the {@link Iterator} this FilterIterator is using.
     *
     * @return the iterator
     */
    public Iterator getIterator() {
        return iterator;
    }

    /** 
     * Sets the underlying {@link Iterator} for this FilterIterator to use.
     * If the next object was already calculated, it is discarded.
     *
     * @param iterator  the iterator to use
     */
    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
        nextObject = null;
        nextObjectSet = false;
    }

    //-----------------------------------------------------------------------
    /** 
     * Gets the {@link Predicate} this FilterIterator is using.
     *
     * @return the predicate
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /** 
     * Sets the {@link Predicate} for this FilterIterator to use,
     * If the next object was already calculated, it is discarded.
     * 
     * @param predicate  the predicate to use
     */
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
        nextObject = null;
        nextObjectSet = false;
    }

    //-----------------------------------------------------------------------
    /**
     * Traverses the underlying Iterator to find the next object for which
     * {@link Predicate#evaluate(Object)} returns <code>true</code>,
     * sets <code>nextObject</node> to that object and sets <code>nextObjectSet</code> 
     * to <code>true</code>.
     * @return true if the next object was calculated, and <code>false</code> otherwise.
     */
    private boolean setNextObject() {
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (predicate.evaluate(object)) {
                nextObject = object;
                nextObjectSet = true;
                return true;
            }
        }
        return false;
    }

}
