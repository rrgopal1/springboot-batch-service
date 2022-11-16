package com.rr.stockfeed;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StockDataRepository extends CrudRepository<StockData, Long> {
  List<StockData> findbyStockTicker(String stock_ticker);

}
