package com.rr.stockfeed;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;


public class StockDataEditor  extends PropertyEditorSupport {


	protected void initBinder(DataBinder binder) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy");
		binder.registerCustomEditor(LocalDate.class, new StockDataEditor() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				if (StringUtils.hasText(text)) {
					setValue(LocalDate.parse(text, formatter));
				} else {
					setValue(null);
				}
			}
		});
		binder.registerCustomEditor(LocalDate.class, new StockDataEditor() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				text = text.replaceAll("[^\\d.]", "");
				if (StringUtils.hasText(text)) {
					setValue(new BigDecimal(text));
				} else {
					setValue(null);
				}
			}


		});
	}

}

