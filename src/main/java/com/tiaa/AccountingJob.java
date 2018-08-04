package com.tiaa;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tiaa.entity.Branch;
import com.tiaa.entity.Cmfoodchain;
import com.tiaa.entity.Orderdetail;
import com.tiaa.entity.Orders;
import com.tiaa.entity.Wrapper;
import com.tiaa.exception.AccountingException;
import com.tiaa.utility.Utility;

/**
 * This is a Thread, that actually processes each file
 * 
 * @author Manu
 *
 */
public class AccountingJob implements Callable<Object> {

	private static Logger logger = Logger.getLogger(AccountingJob.class);
	private static ObjectMapper jsonMapper;

	static {
		jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(Include.NON_NULL);
		jsonMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		jsonMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	//Hold the filename that should be processed
	String fileName;
	
	///Holds the destination path where the processed file should be moved after processing
	String processedFilePath;

	AccountingJob(String file, String processedFilePath) {
		this.fileName = file;
		this.processedFilePath = processedFilePath;
	}

	public Object call() throws Exception {

		Cmfoodchain foodChain = null;
		try {
			//Process the JSON File and create objects from it to process
			Wrapper wrapper = deserializeJSON(fileName, Wrapper.class);
			if (wrapper != null && wrapper.getCmfoodchain() != null) {
				foodChain = wrapper.getCmfoodchain().get(0);
			}
		} catch (AccountingException e) {
			logger.debug("Parsing failed for JSON, trying for XML Parsing.");
			//If JSON parsing fails try to parse it using XML Parser
			try {
				foodChain = deserializeXML(fileName, Cmfoodchain.class);
			} catch (AccountingException e1) {
				logger.error("Exception Parsing the file : " + fileName);
			}
		}
		//Move the files processed to another folder. So that it is not picked again in next Job's iteration
		Utility.moveProcessedFiles(fileName, processedFilePath);
		return checkAccountingInFoodChain(foodChain);
	}

	/**
	 * Create Objects from the JSON File
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws AccountingException
	 */
	private <T> T deserializeJSON(String fileName, Class<T> clazz) throws AccountingException {
		try {
			String fileData = new String(Files.readAllBytes(Paths.get(fileName)));
			return jsonMapper.readValue(fileData, clazz);
		} catch (Exception e) {
			logger.error("Exception Reading JSON File. Try reading XML.");
			throw new AccountingException(e);
		}
	}

	/**
	 * Create Objects from the XML File
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws AccountingException
	 */
	private <T> T deserializeXML(String fileName, Class<T> clazz) throws AccountingException {
		try {
			return JAXB.unmarshal(new File(fileName), clazz);
		} catch (Exception e) {
			logger.error("Exception Reading XML File.");
			throw new AccountingException(e);
		}
	}

	/**
	 * Process the data retrieved from the file and check if the accounting matches
	 * 
	 * @param foodChain
	 * @return Branch, this will be used to write the details in output
	 */
	private Branch checkAccountingInFoodChain(Cmfoodchain foodChain) {
		Branch computedBranch = null;
		if (foodChain!=null && foodChain.getBranch() != null && foodChain.getOrders() != null) {
			Orders orders = foodChain.getOrders();
			Branch fromBranch = foodChain.getBranch().get(0);
			//Populate the details to be outputed
			computedBranch = new Branch();
			computedBranch.setLocation(fromBranch.getLocation());
			computedBranch.setLocationid(fromBranch.getLocationid());
			computedBranch.setTotalcollection(fromBranch.getTotalcollection());

			//Iterate through the order details and check if the total sum is same as the one reported by Branch.
			float sumOfOrders = 0;
			for (Orderdetail detail : orders.getOrderdetail()) {
				sumOfOrders += detail.getBillamount();
			}
			computedBranch.setSumoforder(sumOfOrders);
			//If sum and branch report matches, set the flag
			if (sumOfOrders == computedBranch.getTotalcollection()) {
				computedBranch.setCorrectAccounting(true);
			}
		}
		return computedBranch;
	}

}
