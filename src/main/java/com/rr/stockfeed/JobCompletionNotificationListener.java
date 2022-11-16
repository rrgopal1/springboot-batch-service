package com.rr.stockfeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component

public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED !! It's time to verify the results!!");


            List<StockData> results = jdbcTemplate.query(
                    "SELECT * FROM dj_index_data WHERE id = 100", new RowMapper<StockData>() {

                        @Override
                        public StockData mapRow(ResultSet rs, int row) throws SQLException {
                            return new StockData(rs.getLong(1), rs.getShort(2),rs.getString(3),rs.getDate(4), rs.getBigDecimal(5),rs.getBigDecimal(6),rs.getBigDecimal(7),rs.getBigDecimal(8),rs.getLong(9),rs.getDouble(10),rs.getDouble(11),rs.getLong(12),rs.getBigDecimal(13),rs.getBigDecimal(14),rs.getBigDecimal(15),rs.getInt(16),rs.getDouble(17));
                        }
                    });
                for (StockData stock : results) {
                    log.info("Found <" + stock + "> in the database.");
                }
            }
            }
        }
