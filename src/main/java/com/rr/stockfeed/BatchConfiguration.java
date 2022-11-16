package com.rr.stockfeed;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${file.input}")
    private String fileInput;


    @Autowired
    @StepScope
    @Bean
    public FlatFileItemReader<StockData>  reader(@Value("#{jobParameters[file]}") String resource) {
        FlatFileItemReader<StockData> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource(resource));
        flatFileItemReader.setLinesToSkip(0);
        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames(new String[]{"quarter", "stock", "date", "open", "high", "low", "close", "volume", "percent_change_price", "percent_change_volume_over_last_wk", "previous_weeks_volume", "next_weeks_open", "next_weeks_close", "percent_change_next_weeks_price", "days_to_next_dividend", "percent_return_next_dividend" });
        BeanWrapperFieldSetMapper<StockData> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(StockData.class);
        fieldSetMapper.setConversionService(dtoConversionService());
        defaultLineMapper.setLineTokenizer(new FileLineTokenizer());
        defaultLineMapper.setFieldSetMapper(new StockDataFieldSetMapper());
        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;

    }
     @Bean
    public TickerItemProcessor processor() {
        return new TickerItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO dj_index_data  (  trading_qtr, stock_ticker, trading_date, day_opening, day_high, day_low, day_closing,day_volume,percent_change_price,percent_change_volume_over_last_week,previous_week_volume,next_week_opening,next_week_closing,percent_change_next_week_price,days_to_next_dividend,percent_return_next_dividend) VALUES ( :trading_qtr,:stock_ticker,:trading_date,:day_opening,:day_high,:day_low,:day_closing,:day_volume,:percent_change_price,:percent_change_volume_over_last_week,:previous_week_volume,:next_week_opening,:next_week_closing,:percent_change_next_week_price,:days_to_next_dividend,:percent_return_next_dividend)")
		        .beanMapped()
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job job(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("stockdatajob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(FlatFileItemReader<StockData> itemReader, JdbcBatchItemWriter writer) {
        return stepBuilderFactory.get("step1")
                .<StockData, StockData> chunk(10)
                .reader(itemReader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet stockDataTasklet(
            @Value("#{jobParameters['name']}") String name,
            @Value("#{jobParameters['fileinput']}") String fileName) {

        return (contribution, chunkContext) -> {

            System.out.println(
                    String.format("Hello, %s!", name));
            System.out.println(
                    String.format("fileName = %s", fileName));

            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public ConversionService dtoConversionService() {
        DefaultConversionService dtoConversionService = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(dtoConversionService);
        dtoConversionService.addConverter(new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String text) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy");
                    return LocalDate.parse(text, formatter);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        dtoConversionService.addConverter(new Converter<String, BigDecimal>() {
            @Override
            public BigDecimal convert(String text) {
                try {
                    text=text.replaceAll("[^\\d.]", "");
                    if (StringUtils.isNotEmpty(text)) {
                        return new BigDecimal(text);
                    } else {
                        return new BigDecimal("0.00");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return dtoConversionService;
    }
}



