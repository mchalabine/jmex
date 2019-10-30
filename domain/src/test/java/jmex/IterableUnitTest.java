package jmex;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

final class IterableUnitTest {

    @Test
    void testValueOfVarargsCreatesExpected() {
        Strings strings = Strings.valueOf("1", "2", "3");
        requireNonNull(strings);
    }

    private final static class Strings extends Iterable<String> {

        private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

        private Strings() {
            super(String.class, Strings.class);
        }

        private Strings(Class<String> itemClass, Class<? extends Iterable<String>> itemsClass) {
            super(itemClass, itemsClass);
        }

        @Override
        protected MethodHandles.Lookup getMethodHandlesLookup() {
            return lookup;
        }

        public static Strings valueOf(String... names) {
            return Iterable.valueOf(names, Strings.class, lookup);
        }
    }

}