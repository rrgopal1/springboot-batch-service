package com.rr.stockfeed;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.LineTokenizer;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FileLineTokenizer implements LineTokenizer {
private String delimiter = ",";
private String[] names = new String[] {"quarter",
 			"stock",
 			"date",
 			"open",
 			"high",
			 "low",
 			"close",
 			"volume",
 			"percent_change_price",
 			"percent_change_volume_over_last_wk",
 			"previous_weeks_volume",
 			"next_weeks_open",
 			"next_weeks_close",
 			"percent_change_next_weeks_price",
 			"days_to_next_dividend",
 			"percent_return_next_dividend"};
private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
public FieldSet tokenize(String record) {
String[] fields = record.split(delimiter);
List<String> parsedFields = new ArrayList<>();
for (int i = 0; i < fields.length; i++) {
	parsedFields.add(fields[i]);
	if (fields[i] == "") {
		if (i==0||i==7||i==10||i==14||i==13||i==15) {
			parsedFields.set(i, "0");
		}
		}

if (i == 3 ||i==4 ||i==5||i==6||i==8||i==9||i==11||i==12) {
			  try {
				  String text=fields[i];
				  text=text.replaceAll("[^\\d.]", "");
				  if (StringUtils.isNotEmpty(text)) {
					  parsedFields.set(i,(text));
				  } else {
					  parsedFields.set(i,"0.00");
				  }
                	} catch (Exception e) {
                   	 throw new RuntimeException(e);
                	}
}

}
	return fieldSetFactory.create(parsedFields.toArray(new String[0]), names);
}
}