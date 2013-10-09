package com.agilisto.proxyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Retrieves content from the URL provided in the "url" parameter<br>
 *
 * <b>Usage:</b> http://proxyServer/?url=http://server.to.get.content.from.com
 */
@Controller
public class ProxyServerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProxyServerController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String proxyServer(@RequestParam Map<String, String> allRequestParams, @RequestBody String body) throws UnsupportedEncodingException {
		logger.info(body);
		if(allRequestParams.get("url") == null)
		{
			return "Invalid request due to 'url' parameter missing. Usage: http://proxyServer/?url=http://server.to.get.content.from.com";
		}
		
		String completeUrlString = addParametersToUrl(allRequestParams);
		
		return getContentFromUrl(completeUrlString.toString());
	}
	
	private String addParametersToUrl(Map<String, String> allRequestParams) {
		
		StringBuffer completeUrlString = new StringBuffer(allRequestParams.get("url"));
		
		allRequestParams.remove("url");
		
		Iterator<String> parametersIterator = allRequestParams.keySet().iterator();
		
		while(parametersIterator.hasNext())
		{
			String parameter = parametersIterator.next();
				
			if(completeUrlString.indexOf("?") < 0)
			{
				completeUrlString.append("?");
			}
			
			completeUrlString = completeUrlString.append("&" + parameter + "=" + allRequestParams.get(parameter));
		}
		
		return completeUrlString.toString();
	}

	private String getContentFromUrl(String completeUrlString) throws UnsupportedEncodingException {
		
 		logger.info("New request: " + completeUrlString);
        
 		completeUrlString = completeUrlString.replaceAll(" ", "%20");
		
		logger.info("Encoded request: " + completeUrlString);
		
		String returnMessage = "";
        
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(completeUrlString).openStream()));
			
			String inputLine;
			
	        while ((inputLine = in.readLine()) != null) {
	        	returnMessage = returnMessage + inputLine;
	        }

	        in.close();
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("I/O exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		return returnMessage;
	}
	
}
