package model.store;

import common.pair.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic store of items.
 *
 * @param <T> The type of item this store will contain.
 */
public class Store<T> implements IStore<T> {

  //<editor-fold desc="State variables">

  protected final Map<Integer, T> items;
  private int itemCount;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Instantiates the store with zero items.
   */
  public Store() {
    this.items = new HashMap<>();
    this.itemCount = 0;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public int save(T item) throws IllegalArgumentException {
    if (this.items.containsValue(item)) {
      throw new IllegalArgumentException("Provided item already exists in the store!");
    }

    this.items.put(this.itemCount, item);

    return this.itemCount++;
  }

  @Override
  public T retrieve(int id) {
    if (id < 0 || this.itemCount < id) {
      throw new IllegalArgumentException("Provided id is not valid!");
    }

    return this.items.get(id);
  }

  @Override
  public List<Pair<Integer, T>> getAll() {
    List<Pair<Integer, T>> result = new ArrayList<>();
    for (var item : this.items.entrySet()) {
      result.add(new Pair<>(item.getKey(), item.getValue()));
    }

    return result;
  }

  @Override
  public int getItemCount() {
    return this.itemCount;
  }

  //</editor-fold>
}
