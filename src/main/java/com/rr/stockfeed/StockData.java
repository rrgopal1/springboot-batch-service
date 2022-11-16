package com.rr.stockfeed;

// import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="dj_index_data")
@NamedQuery(name = "StockData.findbyStockTicker",
 query ="SELECT c FROM StockData c WHERE c.stock_ticker = ?1")

public class StockData {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private Short trading_qtr;
    private String stock_ticker;
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
    private Date trading_date;
    private BigDecimal day_opening,day_high,day_low,day_closing;
    private Long day_volume;
    private Double  percent_change_price,percent_change_volume_over_last_week;
    private Long  previous_week_volume;
    private BigDecimal next_week_opening,next_week_closing,percent_change_next_week_price;
    private Integer days_to_next_dividend;
    private Double percent_return_next_dividend;


}