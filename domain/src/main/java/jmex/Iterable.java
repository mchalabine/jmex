package jmex;

import java.io.Serializable;
import java.lang.invoke.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Base representation for everything iterable.
 *
 * @author Michael Chalabine
 * @since 0.1.0
 */
public abstract class Iterable<T> implements java.lang.Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<Class<?>, Object> instanceFactoryByClass = new ConcurrentHashMap<>();

    protected final Collection<T> items;

    protected final Class<T> itemClass;

    protected final Class<? extends Iterable<T>> itemsClass;

    private transient Iterator<T> iterator;

    protected abstract MethodHandles.Lookup getMethodHandlesLookup();

    private static <I> Supplier<I> getInstanceFactory(Class<I> clazz, MethodHandles.Lookup lookup) {
        Object result = instanceFactoryByClass.get(clazz);
        if (isNull(result)) {
            Supplier<I> instanceFactory = createInstanceFactory(clazz, lookup);
            result = instanceFactoryByClass.putIfAbsent(clazz, instanceFactory);
            if (isNull(result)) {
                result = instanceFactory;
            }
        }
        return (Supplier<I>) result;
    }

    private static <I> Supplier<I> createInstanceFactory(Class<I> clazz,
                                                         MethodHandles.Lookup lookup) {
        try {
            return tryCreateInstanceFactory(clazz, lookup);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException("Failed to create factory", throwable.getCause());
        }
    }

    private static <I> Supplier<I> tryCreateInstanceFactory(
            Class<I> clazz, MethodHandles.Lookup lookup) throws Throwable {
        MethodType methodType = MethodType.methodType(Object.class);
        MethodType invokedType = MethodType.methodType(Supplier.class);
        MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class));
        CallSite site = LambdaMetafactory
                .metafactory(lookup, "get", invokedType, methodType, constructor, methodType);
        return (Supplier<I>) site.getTarget().invokeExact();
    }

    protected Iterable(Class<T> itemClass, Class<? extends Iterable<T>> itemsClass) {
        this.itemClass = itemClass;
        this.itemsClass = itemsClass;
        items = new LinkedList<>();
        this.iterator = items.iterator();
    }

    protected void add(T item) {
        if (items != null) {
            items.add(item);
            this.iterator = items.iterator();
        }
    }

    protected void addAll(T[] items) {
        Collections.addAll(this.items, items);
        this.iterator = this.items.iterator();
    }

    protected boolean hasNext() {
        if (iterator == null) {
            iterator = items.iterator();
        }
        return iterator.hasNext();
    }

    protected T next() {
        if (iterator == null) {
            iterator = items.iterator();
        }
        return iterator.next();
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        return itemsClass.getSimpleName() + " [" + list(",") + "]";
    }

    public Set<T> asSet() {
        return Set.copyOf(items);
    }

    public List<T> asList() {
        return List.copyOf(items);
    }

    public Stream<T> stream() {
        return items.stream();
    }

    public T[] asArray() {
        T[] result = (T[]) Array.newInstance(itemClass, items.size());
        return (T[]) items.toArray(result);
    }

    public String[] asArrayOfStrings() {
        List<String> result = new ArrayList<>(items.size());
        for (T item : items) {
            Method stringifier;
            try {
                stringifier = itemClass.getMethod("toString");
                stringifier.setAccessible(true);
                String string = (String) stringifier.invoke(item);
                result.add(string);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toArray(new String[items.size()]);
    }

    public String asPropertyString() {
        return list(",");
    }

    public String asClassPathString() {
        return list(":");
    }

    private String list(String separator) {
        String result = "";
        String[] itemsAsStrings = asArrayOfStrings();
        for (int i = 0; i < itemsAsStrings.length; i++) {
            result += (i > 0) ? separator + itemsAsStrings[i] : itemsAsStrings[i];
        }
        return result;
    }

    protected static <T, S extends Iterable<T>> S valueOf(Stream<T> items, Class<S> itemsClass,
                                                          MethodHandles.Lookup lookup) {
        S result;
        result = newInstance(itemsClass, lookup);
        items.forEach(result::add);
        return result;
    }

    private static <S extends Iterable<T>, T> S valueOf(
            java.lang.Iterable<T> items, Class<S> itemsClass, MethodHandles.Lookup lookup) {
        S result;
        result = newInstance(itemsClass, lookup);
        for (T item : items) {
            result.add(item);
        }
        return result;
    }

    protected static <T, S extends Iterable<T>> S valueOf(
            T[] items, Class<S> itemsClass, MethodHandles.Lookup lookup) {
        S result;
        result = newInstance(itemsClass, lookup);
        for (T item : items) {
            result.add(item);
        }
        return result;
    }

    protected static <T, S extends Iterable<T>> S valueOf(
            S items, Class<S> itemsClass, MethodHandles.Lookup lookup) {
        S result = newInstance(itemsClass, lookup);
        shallowCopy(items, result);
        return result;
    }

    protected static <T, S extends Iterable<T>> S valueOf(
            S items, Class<S> itemsClass, T exclude, T insert, MethodHandles.Lookup lookup) {
        return filterInsert(items, itemsClass, exclude, insert, lookup);
    }

    private static <S extends Iterable<T>, T> S filterInsert(
            S items, Class<S> itemsClass, T exclude, T insert, MethodHandles.Lookup lookup) {
        S result;
        result = newInstance(itemsClass, lookup);
        shallowCopyFilter(items, result, exclude);
        if (insert != null) {
            result.add(insert);
        }
        return result;
    }

    private static <S extends Iterable<T>, T> void shallowCopy(S source, S destination) {
        for (T item : source) {
            destination.add(item);
        }
    }

    private static <S extends Iterable<T>, T> void shallowCopyFilter(
            S source, S destination, T exclude) {
        if (exclude == null) {
            shallowCopy(source, destination);
        } else {
            for (T item : source) {
                if (!item.equals(exclude)) {
                    destination.add(item);
                }
            }
        }
    }

    private static <T, S extends Iterable<T>> S newInstance(
            Class<S> itemsClass, MethodHandles.Lookup lookup) {
        return Iterable.getInstanceFactory(itemsClass, lookup).get();
    }

    public boolean isStrictEmpty() {
        boolean result = false;
        if (items == null) {
            result = true;
        } else if (items.size() == 0) {
            result = true;
        } else if (containsEmptyElementsOnly(items)) {
            result = true;
        }
        return result;
    }

    public boolean contains(T other) {
        return items.contains(other);
    }

    public boolean contains(final Iterable<T> others) {
        for (T t : others) {
            if (!contains(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemClass == null) ? 0 : itemClass.hashCode());
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + ((itemsClass == null) ? 0 : itemsClass.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        if (this == other) {
            return true;
        }

        Iterable<T> others = (Iterable<T>) other;
        if (this.size() != others.size()) {
            return false;
        }
        boolean result = true;
        for (T t : others) {
            result = result && this.contains(t);
        }
        return result;
    }

    private boolean containsEmptyElementsOnly(Collection<T> items) {
        boolean isEmpty;
        for (T item : items) {
            Method emptyChecker;
            try {
                emptyChecker = itemClass.getMethod("isEmpty");
                isEmpty = (boolean) emptyChecker.invoke(item);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (!isEmpty) {
                return false;
            }
        }
        return true;
    }

    public Class<T> getItemClass() {
        return itemClass;
    }

    public Class<?> getIterableClass() {
        return itemsClass;
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    private static final class SerializationProxy<T> implements Serializable {

        private static final long serialVersionUID = 1L;
        Collection<T> items;
        Class<T> itemClass;
        Class<? extends Iterable<T>> itemsClass;
        MethodHandles.Lookup lookup;

        private SerializationProxy(Collection<T> items, Class<T> itemClass,
                                   Class<? extends Iterable<T>> itemsClass,
                                   MethodHandles.Lookup lookup) {
            this.items = requireNonNull(items, "Items");
            this.itemClass = requireNonNull(itemClass, "ItemClass");
            this.itemsClass = requireNonNull(itemsClass, "ItemsClass");
            this.lookup = requireNonNull(lookup, "MethoHandle.Lookup");
        }

        private Object readResolve() {
            return valueOf(this.items, this.itemsClass, lookup);
        }
    }

    private Object writeReplace() {
        return new SerializationProxy<T>(this.items, this.itemClass, this.itemsClass,
                getMethodHandlesLookup());
    }

    @SuppressWarnings({"unchecked", "hiding"})
    public <T, S extends Iterable<T>> S asSorted(Class<S> subItemsClass, Comparator<T> comparator,
                                                 MethodHandles.Lookup lookup) {
        List<T> sortedList = new ArrayList<T>((Collection<? extends T>) items);
        Collections.sort(sortedList, comparator);
        return valueOf(sortedList, subItemsClass, lookup);
    }

}
