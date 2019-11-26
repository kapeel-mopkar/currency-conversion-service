package com.kaps.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CurrencyExchangeServiceProxy exchangeServiceProxy;
	
	@GetMapping(path = "/currency-exchange/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal quantity) {
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversionBean> forEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversionBean.class, uriVariables);
		
		CurrencyConversionBean body = forEntity.getBody();
		
		return new CurrencyConversionBean(1000L, from, to, body.getConversionMultiple(), quantity, quantity.multiply(body.getConversionMultiple()), 
				Integer.parseInt(env.getProperty("local.server.port")));
	}
	
	@GetMapping(path = "/currency-exchange-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyUsingFeign(@PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal quantity) {
		//		Map<String, String> uriVariables = new HashMap<String, String>();
		//		uriVariables.put("from", from);
		//		uriVariables.put("to", to);
		//		ResponseEntity<CurrencyConversionBean> forEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
		//				CurrencyConversionBean.class, uriVariables);
		//		
		//		CurrencyConversionBean body = forEntity.getBody();
		CurrencyConversionBean body = exchangeServiceProxy.retrieveExchangeValue(from, to);
		
		return new CurrencyConversionBean(1000L, from, to, body.getConversionMultiple(), quantity, quantity.multiply(body.getConversionMultiple()), 
				body.getPort());
	}
}
