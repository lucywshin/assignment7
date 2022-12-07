package features;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import model.portfolio.eRecurringIntervalType;

/**
 * An interface representing the features supported by this application.
 */
public interface IPortfolioManagerFeatures {

  /**
   * Loads the list of portfolios.
   */
  void loadPortfolioList();

  /**
   * Creates a flexible portfolio with the specified name.
   *
   * @param portfolioName The name of the new portfolio.
   */
  void createPortfolio(String portfolioName);

  /**
   * Import portfolios from the specified file.
   *
   * @param filePath The target file's path.
   * @return whether the operation was successful.
   */
  boolean importPortfolios(String filePath);

  /**
   * Exports portfolios to the specified file.
   *
   * @param filePath The target file's path.
   * @return whether the operation was successful.
   */
  boolean exportPortfolios(String filePath);

  /**
   * Load the stocks for the specified portfolio.
   *
   * @param portfolioId The id of the portfolio.
   * @return whether the operation was successful.
   */
  boolean loadStocksForPortfolio(int portfolioId);

  /**
   * Buy the specified stock for the specified portfolio.
   *
   * @param portfolioId     the id of the portfolio.
   * @param symbol          the symbol of the stock being bought.
   * @param volume          the volume of the purchase.
   * @param transactionDate the date on which the stock is being purchased.
   * @param commissionFees  the commission fees charged for the transaction.
   * @return whether the operation was successful.
   */
  boolean buyStocksForPortfolio(int portfolioId, String symbol, BigDecimal volume,
      Date transactionDate, BigDecimal commissionFees);

  /**
   * Sell the specified stock for the specified portfolio.
   *
   * @param portfolioId     the id of the portfolio.
   * @param symbol          the symbol of the stock being sold.
   * @param volume          the volume of the purchase.
   * @param transactionDate the date on which the stock is being sold.
   * @param commissionFees  the commission fees charged for the transaction.
   * @return whether the operation was successful.
   */
  boolean sellStocksForPortfolio(int portfolioId, String symbol, BigDecimal volume,
      Date transactionDate, BigDecimal commissionFees);

  /**
   * Gets the composition of the portfolio specified.
   *
   * @param portfolioId the id of the portfolio.
   * @param date        the date on which the composition is to be fetched.
   * @return whether the operation was successful.
   */
  boolean getPortfolioComposition(int portfolioId, Date date);

  /**
   * Gets the value of the portfolio specified.
   *
   * @param portfolioId the id of the portfolio.
   * @param date        the date on which the value is to be fetched.
   * @return whether the operation was successful.
   */
  boolean getPortfolioValue(int portfolioId, Date date);

  /**
   * Gets the cost-basis of the portfolio specified.
   *
   * @param portfolioId the id of the portfolio.
   * @param date        the date on which the cost-basis is to be fetched.
   * @return whether the operation was successful.
   */
  boolean getPortfolioCostBasis(int portfolioId, Date date);

  /**
   * Loads the dollar cost investments for the specified portfolio.
   *
   * @param portfolioId the id of the portfolio.
   * @return whether the operation was successful.
   */
  boolean loadDollarCostInvestmentsForPortfolio(int portfolioId);

  /**
   * Adds a dollar cost investment to the specified portfolio.
   *
   * @param portfolioId          the id of the portfolio.
   * @param date                 the date on which the investment is to be made. This is the start
   *                             date if the investment is recurring.
   * @param amount               the amount to be invested. The currency is USD.
   * @param stocksWithPercentage a list of pairs containing the stock symbol and the percentage of
   *                             the stock in the investment. The percentages should add up to 100.
   * @param commissionFee        the commission fee charged per transaction.
   * @param isRecurring          whether the investment is a recurring investment.
   * @param endDate              the end date if the investment is a recurring investment.
   * @param intervalType         the interval type of recurrence if the investment is a recurring
   *                             investment.
   * @param intervalDelta        the interval delta of recurrence if the investment is a recurring
   *                             investment.
   * @return whether the operation was successful.
   */
  boolean addDollarCostInvestmentForPortfolio(int portfolioId, Date date, BigDecimal amount,
      List<Pair<String, BigDecimal>> stocksWithPercentage, BigDecimal commissionFee,
      boolean isRecurring, Date endDate, eRecurringIntervalType intervalType,
      Integer intervalDelta);

  /**
   * Loads the performance chart for the specified portfolio in the specified time range.
   *
   * @param portfolioId the id of the portfolio.
   * @param startDate   the start date of the time range.
   * @param endDate     the end date of the time range.
   * @return whether the operation was successful.
   */
  boolean loadPerformanceChart(int portfolioId, Date startDate, Date endDate);
}
