package model.portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import model.store.IStore;

/**
 * This interface represents a store of portfolios.
 */
public interface IAbstractPortfolioStore<T extends IAbstractPortfolio> extends IStore<T> {

  /**
   * Imports portfolios from the provided {@code InputStream} into the store.
   *
   * @param inputStream source for import of portfolios.
   * @throws IOException            when an error occurs in the input/output operations with the
   *                                {@code InputStream}.
   * @throws InstantiationException when the creation of the portfolio fails.
   */
  void importItemsFromCsv(InputStream inputStream) throws IOException, InstantiationException;

  /**
   * Exports all available portfolios to the provided {@code OutputStream}.
   *
   * @param outputStream destination for export of portfolios.
   * @throws IOException when an error occurs in the input/output operations with the
   *                     {@code OutputStream}.
   */
  void exportItemsToCsv(OutputStream outputStream) throws IOException;
}
