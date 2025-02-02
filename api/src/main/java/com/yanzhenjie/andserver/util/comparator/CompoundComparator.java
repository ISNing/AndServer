/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.util.comparator;

import com.yanzhenjie.andserver.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/7/11.
 */
public class CompoundComparator<T> implements Comparator<T>, Serializable {

    private final List<InvertibleComparator<T>> comparators;


    /**
     * Construct a CompoundComparator with initially no Comparators. Clients must add at least one Comparator before
     * calling the compare method or an IllegalStateException is thrown.
     */
    public CompoundComparator() {
        this.comparators = new ArrayList<>();
    }

    /**
     * Construct a CompoundComparator from the Comparators in the provided array.
     *
     * <p>All Comparators will default to ascending sort order, unless they are InvertibleComparators.
     *
     * @param comparators the comparators to build into a compound comparator
     * @see InvertibleComparator
     */
    @SuppressWarnings("unchecked")
    public CompoundComparator(Comparator<T>... comparators) {
        Assert.notNull(comparators, "Comparators must not be null");
        this.comparators = new ArrayList<>(comparators.length);
        for (Comparator<T> comparator : comparators) {
            addComparator(comparator);
        }
    }


    /**
     * Add a Comparator to the end of the chain.
     *
     * <p>The Comparator will default to ascending sort order, unless it is a InvertibleComparator.
     *
     * @param comparator the Comparator to add to the end of the chain.
     */
    public void addComparator(Comparator<T> comparator) {
        if (comparator instanceof InvertibleComparator) {
            this.comparators.add((InvertibleComparator<T>) comparator);
        } else {
            this.comparators.add(new InvertibleComparator<>(comparator));
        }
    }

    /**
     * Add a Comparator to the end of the chain using the provided sort order.
     *
     * @param comparator the Comparator to add to the end of the chain
     * @param ascending  the sort order: ascending (true) or descending (false)
     */
    public void addComparator(Comparator<T> comparator, boolean ascending) {
        this.comparators.add(new InvertibleComparator<>(comparator, ascending));
    }

    /**
     * Replace the Comparator at the given index. <p>The Comparator will default to ascending sort order, unless it is a
     * InvertibleComparator.
     *
     * @param index      the index of the Comparator to replace
     * @param comparator the Comparator to place at the given index
     * @see InvertibleComparator
     */
    public void setComparator(int index, Comparator<T> comparator) {
        if (comparator instanceof InvertibleComparator) {
            this.comparators.set(index, (InvertibleComparator<T>) comparator);
        } else {
            this.comparators.set(index, new InvertibleComparator<>(comparator));
        }
    }

    /**
     * Replace the Comparator at the given index using the given sort order.
     *
     * @param index the index of the Comparator to replace
     * @param comparator the Comparator to place at the given index
     * @param ascending the sort order: ascending (true) or descending (false)
     */
    public void setComparator(int index, Comparator<T> comparator, boolean ascending) {
        this.comparators.set(index, new InvertibleComparator<>(comparator, ascending));
    }

    /**
     * Invert the sort order of each sort definition contained by this compound comparator.
     */
    public void invertOrder() {
        for (InvertibleComparator<T> comparator : this.comparators) {
            comparator.invertOrder();
        }
    }

    /**
     * Invert the sort order of the sort definition at the specified index.
     *
     * @param index the index of the comparator to invert
     */
    public void invertOrder(int index) {
        this.comparators.get(index).invertOrder();
    }

    /**
     * Change the sort order at the given index to ascending.
     *
     * @param index the index of the comparator to change
     */
    public void setAscendingOrder(int index) {
        this.comparators.get(index).setAscending(true);
    }

    /**
     * Change the sort order at the given index to descending sort.
     *
     * @param index the index of the comparator to change
     */
    public void setDescendingOrder(int index) {
        this.comparators.get(index).setAscending(false);
    }

    /**
     * Returns the number of aggregated comparators.
     */
    public int getComparatorCount() {
        return this.comparators.size();
    }

    @Override
    public int compare(T o1, T o2) {
        String message = "No sort definitions have been added to this CompoundComparator to compare";
        Assert.state(this.comparators.size() > 0, message);
        for (InvertibleComparator<T> comparator : this.comparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CompoundComparator)) {
            return false;
        }
        CompoundComparator<T> other = (CompoundComparator<T>) obj;
        return this.comparators.equals(other.comparators);
    }

    @Override
    public int hashCode() {
        return this.comparators.hashCode();
    }

    @Override
    public String toString() {
        return "CompoundComparator: " + this.comparators;
    }
}