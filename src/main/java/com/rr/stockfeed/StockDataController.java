package com.rr.stockfeed;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@EnableBatchProcessing
	@RestController
	@RequestMapping("api/stock-data")
	class StockDataController {
		private final StockDataRepository stockdataRepository;

		public StockDataController(StockDataRepository stockdataRepository) {
			this.stockdataRepository = stockdataRepository;
		}

		@Autowired
		private JobLauncher jobLauncher;
		@Autowired
		private ApplicationContext context;
		@Autowired
		private JobExplorer jobExplorer;

		@PostMapping(path = "/bulk-insert")
		public ExitStatus runJob(@RequestBody JobLaunchRequest request) throws Exception {
			Job job = this.context.getBean(request.getName(), Job.class);
			JobParameters jobParameters =
					new JobParametersBuilder(request.getJobParameters(),
							this.jobExplorer)
							.getNextJobParameters(job)
							.toJobParameters();
			return this.jobLauncher.run(job, jobParameters).getExitStatus();
		}

		@GetMapping
		Iterable<StockData> getStockDatas() {
			return stockdataRepository.findAll();
		}

		@GetMapping("/{id}")
		List<StockData> getStockDataById(@PathVariable String id) {
			return stockdataRepository.findbyStockTicker(id);
		}

		@PostMapping(path = "/missing")
		StockData postStockData(@RequestBody StockData stockdata) {
			return  stockdataRepository.save(stockdata);
		}

/*		@PutMapping("/{id}")
		ResponseEntity<StockData> putStockData(@PathVariable String id,
											   @RequestBody StockData stockdata) {

			return (stockdataRepository.existsById(id))
					? new ResponseEntity<>(stockdataRepository.save(stockdata), HttpStatus.OK)
					: new ResponseEntity<>(stockdataRepository.save(stockdata), HttpStatus.CREATED);
		}

		@DeleteMapping("/{id}")
		void deleteStockData(@PathVariable String id) {
			stockdataRepository.deleteById(id);
		} */

	}
