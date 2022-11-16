package com.rr.stockfeed;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.DataBinder;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StockDataFieldSetMapper implements FieldSetMapper<StockData> {


/*

  Map<String, PropertyEditor> editors = new HashMap<>(2);
        editors.put(LocalDate.class, new PropertyEditorSupport() {

    @Override
    public void setAsText(String text) {
      super.setValue(LocalDate.parse(text),"mm/dd/yyyy");
    }
  });
        editors.put(LocalDate.class, new PropertyEditorSupport() {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
      text = text.replaceAll("[^\\d.]", "");
      if (StringUtils.isNotEmpty(text)) {
        setValue(new BigDecimal(text));
      } else {
        setValue(null);
      }
    }
  });
        return editors;
} */


  public StockData mapFieldSet(FieldSet fieldSet) {
    StockData stockdata = new StockData();
    stockdata.setTrading_qtr(fieldSet.readShort("quarter"));
    stockdata.setStock_ticker(fieldSet.readString("stock"));
    stockdata.setTrading_date(fieldSet.readDate("date","mm/dd/yyyy"));
    stockdata.setDay_opening(fieldSet.readBigDecimal("open"));
    stockdata.setDay_high(fieldSet.readBigDecimal("high"));
    stockdata.setDay_low(fieldSet.readBigDecimal("low"));
    stockdata.setDay_closing(fieldSet.readBigDecimal("close"));
    stockdata.setDay_volume(fieldSet.readLong("volume"));
    stockdata.setPercent_change_price(fieldSet.readDouble("percent_change_price"));
    stockdata.setPercent_change_volume_over_last_week(fieldSet.readDouble("percent_change_volume_over_last_wk"));
    stockdata.setPrevious_week_volume(fieldSet.readLong("previous_weeks_volume"));
    stockdata.setNext_week_opening(fieldSet.readBigDecimal("next_weeks_open"));
    stockdata.setNext_week_closing(fieldSet.readBigDecimal("next_weeks_close"));
    stockdata.setPercent_change_next_week_price(fieldSet.readBigDecimal("percent_change_next_weeks_price"));
    stockdata.setDays_to_next_dividend(fieldSet.readInt("days_to_next_dividend"));
    stockdata.setPercent_return_next_dividend(fieldSet.readDouble("percent_return_next_dividend"));

    return stockdata;
  }

}

