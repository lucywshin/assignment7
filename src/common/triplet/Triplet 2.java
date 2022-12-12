package common.triplet;

/**
 * An implementation of a triplet in which three values of any type can be stored in a single
 * object.
 *
 * @param <T> the type of the first object.
 * @param <U> the type of the second object.
 * @param <V> the type of the third object.
 */
public class Triplet<T, U, V> {

  private T o1;
  private U o2;
  private V o3;

  /**
   * Creates the Pair.
   *
   * @param o1 the first object.
   * @param o2 the second object.
   * @param o3 the third object.
   */
  public Triplet(T o1, U o2, V o3) {
    this.o1 = o1;
    this.o2 = o2;
    this.o3 = o3;
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
   * Gets the third object.
   *
   * @return the third object.
   */
  public V getO3() {
    return this.o3;
  }

  /**
   * Sets the first object to a new object of the same type.
   *
   * @param o1 the new object.
   * @return the altered triplet.
   */
  public Triplet<T, U, V> setO1(T o1) {
    this.o1 = o1;
    return this;
  }

  /**
   * Sets the second object to a new object of the same type.
   *
   * @param o2 the new object.
   * @return the altered triplet.
   */
  public Triplet<T, U, V> setO2(U o2) {
    this.o2 = o2;
    return this;
  }

  /**
   * Sets the third object to a new object of the same type.
   *
   * @param o3 the new object.
   * @return the altered triplet.
   */
  public Triplet<T, U, V> setO3(V o3) {
    this.o3 = o3;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Triplet<?, ?, ?>)) {
      return false;
    }

    Triplet<?, ?, ?> objTriplet = (Triplet<?, ?, ?>) obj;
    return objTriplet.getO1().equals(this.o1)
        && objTriplet.getO2().equals(this.o2)
        && objTriplet.getO3().equals(this.o3);
  }

  @Override
  public int hashCode() {
    return this.o1.hashCode()
        + this.o2.hashCode()
        + this.o3.hashCode();
  }

  @Override
  public String toString() {
    return this.o1.toString() + ", " + this.o2.toString() + ", " + this.o3.toString();
  }
}
