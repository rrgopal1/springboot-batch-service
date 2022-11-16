DROP TABLE  dj_index_data;
CREATE TABLE dj_index_data(
                              id BIGINT not null primary key auto_increment,
                              trading_qtr INT ,
                              stock_ticker varchar(10),
                              trading_date TIMESTAMP,
                              opening Double,
                              day_high Double,
                              day_low Double,
                              day_closing DOUBLE,
                              day_Volume INTEGER,
                              percent_change_price  DOUBLE ,
                              percent_change_volume_over_last_week DOUBLE,
                              previous_week_volume INTEGER  ,
                              next_week_opening DOUBLE  ,
                              next_week_closing DOUBLE ,
                              percent_change_next_week_price DOUBLE,
                              days_to_next_dividend INTEGER ,
                              percent_return_next_dividend DOUBLE);

