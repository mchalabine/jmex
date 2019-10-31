package jmex;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles;
import org.junit.jupiter.api.Test;

final class IterableUnitTest {

  @Test
  void testValueOfVarargsCreates() {
    Strings strings = Strings.valueOf("1", "2", "3");
    requireNonNull(strings);
  }

  @Test
  void testValueOfVarargsCreatesExpected() {
    Strings strings = Strings.valueOf("1", "2", "3");
    assertEquals(strings.stream()
        .filter(i -> !strings.contains(i))
        .count(), 0);
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