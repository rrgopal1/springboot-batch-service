package com.rr.stockfeed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Slf4j
public class TickerItemProcessor implements ItemProcessor<StockData, StockData> {

// private static final Logger LOGGER = LoggerFactory.getLogger(TickerItemProcessor.class);

    @Override
    public StockData process(final StockData stockdata) throws Exception {
		final long Id = 1234567910;
	short Quarter = stockdata.getTrading_qtr();
        final String Stock = stockdata.getStock_ticker();
	final Date Date = stockdata.getTrading_date();
	final BigDecimal Open = stockdata.getDay_opening();
	final BigDecimal High = stockdata.getDay_high();
	final BigDecimal Low = stockdata.getDay_low();
	final BigDecimal Close = stockdata.getDay_closing();
	final long Volume = stockdata.getDay_volume();
	final double Percent_change_price = stockdata.getPercent_change_price();
	final double Percent_change_volume_over_last_wk = stockdata.getPercent_change_volume_over_last_week();
	final long Previous_weeks_volume =stockdata.getPrevious_week_volume();
	final BigDecimal Next_weeks_open = stockdata.getNext_week_opening();
	final BigDecimal Next_weeks_close = stockdata.getNext_week_closing();
	final BigDecimal Percent_change_next_weeks_price =stockdata.getPercent_change_next_week_price();
	final int Days_to_next_dividend =stockdata.getDays_to_next_dividend();
	final double Percent_return_next_dividend = stockdata.getPercent_return_next_dividend();

        StockData transformedTicker = new StockData(Id,Quarter,Stock,Date,Open,High,Low,Close,Volume,Percent_change_price,Percent_change_volume_over_last_wk,Previous_weeks_volume,Next_weeks_open,Next_weeks_close,Percent_change_next_weeks_price,Days_to_next_dividend,Percent_return_next_dividend);
		log.info("Converting ( {} ) into ( {} )", stockdata, transformedTicker);

        return transformedTicker;
    }
}
