package common.pair;

/**
 * An implementation of a pair in which two values of any type can be stored in a single object.
 *
 * @param <T> the type of the first object.
 * @param <U> the type of the second object.
 */
public class Pair<T, U> {

  private T o1;
  private U o2;

  /**
   * Creates the Pair.
   *
   * @param o1 the first object.
   * @param o2 the second object.
   */
  public Pair(T o1, U o2) {
    this.o1 = o1;
    this.o2 = o2;
  }

  /**
   * Gets the first object.
   *
   * @return the first object.
   */
  public T getO1() {
    return this.o1;
  }

  /**
   * Gets the second object.
   *
   * @return the second object.
   */
  public U getO2() {
    return this.o2;
  }

  /**
   * Sets the first object to a new object of the same type.
   *
   * @param o1 the new object.
   * @return the altered pair.
   */
  public Pair<T, U> setO1(T o1) {
    this.o1 = o1;
    return this;
  }

  /**
   * Sets the second object to a new object of the same type.
   *
   * @param o2 the new object.
   * @return the altered pair.
   */
  public Pair<T, U> setO2(U o2) {
    this.o2 = o2;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Pair<?, ?>)) {
      return false;
    }

    Pair<?, ?> objPair = (Pair<?, ?>) obj;
    return objPair.getO1().equals(this.o1) && objPair.getO2().equals(this.o2);
  }

  @Override
  public int hashCode() {
    return this.o1.hashCode() + this.o2.hashCode();
  }

  @Override
  public String toString() {
    return this.o1.toString() + ", " + this.o2.toString();
  }
}
