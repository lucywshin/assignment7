package model;

import common.pair.Pair;
import common.triplet.Triplet;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import model.alphavantage.AlphaVantageAPI;
import model.chart.ChartService;
import model.chart.IChart;
import model.chart.IChartService;
import model.portfolio.FlexiblePortfolio;
import model.portfolio.FlexiblePortfolioStore;
import model.portfolio.IAbstractPortfolioStore;
import model.portfolio.IDollarCostInvestment;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.IStock;
import model.portfolio.IStockDataSource;
import model.portfolio.Portfolio;
import model.portfolio.Portfolio.PortfolioImplBuilder;
import model.portfolio.PortfolioStore;
import model.portfolio.StockDataSourceException;
import model.portfolio.eRecurringIntervalType;

/**
 * A class that implements features for portfolio management.
 */
public class PortfolioModel implements IPortfolioModel {

  //<editor-fold desc="State variables">

  private final IAbstractPortfolioStore<IPortfolio> portfolioStore;
  private final IAbstractPortfolioStore<IFlexiblePortfolio> flexiblePortfolioStore;
  private IStockDataSource dataSource;
  private final IChartService chartService;

  private final PortfolioImplBuilder portfolioImplBuilder;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Instantiates a model for portfolio management.
   */
  public PortfolioModel() {
    try {
      this.dataSource = new AlphaVantageAPI();
    } catch (StockDataSourceException e) {
      System.out.println("Stock Data Source Init Failed: " + e.getMessage());
    }
    this.chartService = new ChartService(5, 30, 50);
    this.portfolioStore = new PortfolioStore(this.dataSource);
    this.flexiblePortfolioStore = new FlexiblePortfolioStore(this.dataSource);
    this.portfolioImplBuilder = Portfolio.getBuilder();
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  //<editor-fold desc="Regular Portfolio methods">

  @Override
  public IStock getStockDetails(String symbol) {
    return this.dataSource.getStock(symbol);
  }

  @Override
  public String setName(String name) {
    return this.portfolioImplBuilder.setName(name).getName();
  }

  @Override
  public String getName(int id) {
    return this.portfolioStore.retrieve(id).getName();
  }

  @Override
  public List<Pair<Integer, IPortfolio>> getAllPortfolios() {
    return this.portfolioStore.getAll();
  }

  @Override
  public int getPortfolioCount() {
    return this.portfolioStore.getItemCount();
  }

  @Override
  public String getPortfolioBuilderName() {
    return this.portfolioImplBuilder.getName();
  }

  @Override
  public String setPortfolioBuilderName(String name) {
    return this.portfolioImplBuilder.setName(name).getName();
  }

  @Override
  public List<IPortfolioStock> getPortfolioBuilderStocks() {
    return this.portfolioImplBuilder.getStocks();
  }

  @Override
  public List<IPortfolioStock> addStocksToPortfolioBuilder(List<Pair<String, BigDecimal>> stocks) {
    return this.portfolioImplBuilder.addStocks(dataSource, stocks).getStocks();
  }

  @Override
  public int buildPortfolio() throws InstantiationException {
    IPortfolio createdPortfolio = this.portfolioImplBuilder.create();

    return this.portfolioStore.save(createdPortfolio);
  }

  @Override
  public Pair<String, List<String>> getPortfolioComposition(int id) {
    IPortfolio p = this.portfolioStore.retrieve(id);
    return new Pair<>(p.getName(), p.getComposition());
  }

  @Override
  public IPortfolio getPortfolio(int id) {
    return this.portfolioStore.retrieve(id);
  }

  @Override
  public Pair<BigDecimal, List<IPortfolioStockValue>> getPortfolioValue(int id, Date date)
      throws StockDataSourceException {
    return this.portfolioStore.retrieve(id).getValue(this.dataSource, date);
  }

  @Override
  public void importPortfolios(String filePath) throws IOException, InstantiationException {
    Path path = Paths.get(filePath);
    this.portfolioStore.importItemsFromCsv(Files.newInputStream(path));
  }

  @Override
  public void exportPortfolios(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    this.portfolioStore.exportItemsToCsv(Files.newOutputStream(path));
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio methods">

  @Override
  public int getFlexiblePortfolioCount() {
    return this.flexiblePortfolioStore.getItemCount();
  }

  @Override
  public List<Pair<Integer, IFlexiblePortfolio>> getAllFlexiblePortfolios() {
    return this.flexiblePortfolioStore.getAll();
  }

  @Override
  public IFlexiblePortfolio getFlexiblePortfolio(int id) throws IllegalArgumentException {
    return this.flexiblePortfolioStore.retrieve(id);
  }

  @Override
  public int createFlexiblePortfolio(String portfolioName) throws InstantiationException {
    IFlexiblePortfolio createdPortfolio = new FlexiblePortfolio(dataSource, portfolioName, null);

    return this.flexiblePortfolioStore.save(createdPortfolio);
  }

  @Override
  public Pair<String, List<Pair<String, BigDecimal>>> getFlexiblePortfolioComposition(int id,
      Date date) {
    IFlexiblePortfolio p = this.flexiblePortfolioStore.retrieve(id);
    return new Pair<>(p.getName(), p.getComposition(date));
  }

  @Override
  public Pair<BigDecimal, List<IPortfolioStockValue>> getFlexiblePortfolioValue(int id, Date date)
      throws StockDataSourceException {
    return this.flexiblePortfolioStore.retrieve(id).getValue(this.dataSource, date);
  }

  @Override
  public Pair<Integer, List<IObservableFlexiblePortfolioStock>> buyStocksForFlexiblePortfolio(
      int portfolioId,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFee)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException {
    IFlexiblePortfolio portfolio = this.flexiblePortfolioStore.retrieve(portfolioId)
        .buyStocks(this.dataSource, stocks, commissionFee);
    return new Pair<>(portfolioId, portfolio.getStocks());
  }

  @Override
  public Pair<Integer, List<IObservableFlexiblePortfolioStock>> sellStocksForFlexiblePortfolio(
      int portfolioId, List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFee)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException {
    IFlexiblePortfolio portfolio = this.flexiblePortfolioStore.retrieve(portfolioId)
        .sellStocks(this.dataSource, stocks, commissionFee);
    return new Pair<>(portfolioId, portfolio.getStocks());
  }

  @Override
  public BigDecimal getCostBasis(int portfolioId, Date date)
      throws IllegalArgumentException, StockDataSourceException {
    return this.flexiblePortfolioStore.retrieve(portfolioId).getCostBasis(this.dataSource, date);
  }

  @Override
  public IChart getPerformanceChart(int portfolioId, Date startDate, Date endDate)
      throws StockDataSourceException {
    return this.flexiblePortfolioStore.retrieve(portfolioId)
        .getPerformanceChart(dataSource, this.chartService, startDate, endDate);
  }

  @Override
  public void importFlexiblePortfolios(String filePath) throws IOException, InstantiationException {
    Path path = Paths.get(filePath);
    this.flexiblePortfolioStore.importItemsFromCsv(Files.newInputStream(path));
  }

  @Override
  public void exportFlexiblePortfolios(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    this.flexiblePortfolioStore.exportItemsToCsv(Files.newOutputStream(path));
  }

  @Override
  public Pair<Integer, List<IObservableFlexiblePortfolioStock>> addDollarCostInvestment(
      int portfolioId, Date date, BigDecimal amount,
      List<Pair<String, BigDecimal>> stocksWithPercentage, BigDecimal commissionFee,
      boolean isRecurring, Date endDate, eRecurringIntervalType intervalType, Integer intervalDelta)
      throws IllegalArgumentException, StockDataSourceException {
    IFlexiblePortfolio portfolio = this.flexiblePortfolioStore.retrieve(portfolioId)
        .addDollarCostInvestment(this.dataSource, date, amount, commissionFee,
            stocksWithPercentage, isRecurring, endDate, intervalType, intervalDelta);

    return new Pair<>(portfolioId, portfolio.getStocks());
  }

  @Override
  public Pair<Integer, List<Pair<String, IDollarCostInvestment>>> getDollarCostInvestments(
      int portfolioId) throws IllegalArgumentException {
    IFlexiblePortfolio portfolio = this.flexiblePortfolioStore.retrieve(portfolioId);

    return new Pair<>(portfolioId, portfolio.getDollarCostInvestments());
  }

  //</editor-fold>

  //</editor-fold>
}
