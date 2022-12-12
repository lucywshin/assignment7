package model.store;

import common.pair.Pair;
import java.util.List;

/**
 * A generic store of items which is meant to persist in memory. The store keeps the items with in
 * incremental integer as the key.
 *
 * @param <T> The type of item that is to be stored.
 */
public interface IStore<T> {

  /**
   * Adds the item to the store.
   *
   * @param item the item to be added.
   * @return the id of the added item.
   */
  int save(T item) throws IllegalArgumentException;

  /**
   * Retrieves the item with the requested id.
   *
   * @param id the id of the item to be retrieved. The id is 0-indexed.
   * @return the item requested.
   * @throws IllegalArgumentException when the id provided is not available in the store or if the
   *                                  Id provided is invalid.
   */
  T retrieve(int id) throws IllegalArgumentException;

  /**
   * Gets all the items in the store.
   *
   * @return a list of pairs with the id and item available in this store.
   */
  List<Pair<Integer, T>> getAll();

  /**
   * Gets the current number of items in the store.
   *
   * @return the current number of items in the store.
   */
  int getItemCount();
}
