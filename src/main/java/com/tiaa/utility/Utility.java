package com.tiaa.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tiaa.exception.AccountingException;

/**
 * 
 * Utility Class
 * 
 * @author Manu
 *
 */
public class Utility {
	
	//Mapper to Marshall/Unmarshall data
	private static ObjectMapper jsonMapper;
	private static Logger logger = Logger.getLogger(Utility.class);
	
	static {
		jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(Include.NON_NULL);
		jsonMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		jsonMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	private Utility(){}
	
	/**
	 * Create JSON file using the classes
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws AccountingException
	 */
	public static String serializeJSON(Object data) {
		try {
			return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
		} catch (Exception e) {
			logger.error("Exception creating JSON File",e);
		}
		return null;
	}

	/**
	 * Create Objects from the JSON File
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws AccountingException
	 */
	public static <T> T deserializeJSON(String data, Class<T> clazz) {
		try {
			return jsonMapper.readValue(data, clazz);
		} catch (IOException e) {
			logger.error("Exception Reading JSON File. Try reading XML");
		}
		return null;
	}
	
	/**
	 * Create Objects from the XML File
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws AccountingException
	 */
	public static <T> T deserializeXML(String fileName, Class<T> clazz) {
		try {
			return JAXB.unmarshal(new File(fileName), clazz);
		} catch (Exception e) {
			logger.error("Exception Reading XML File.",e);
		}
		return null;
	}
	
	/**
	 * 
	 * Create a file using the filename and the content
	 * 
	 * @param fileContent
	 * @param fileName
	 */
	public static void createFile(String fileContent, String fileName) {
		try {
			Files.write(Paths.get(fileName), fileContent.getBytes());
		} catch (IOException e) {
			logger.error("Exception writing result to file - "+fileName,e);
		}
	}
	
	/**
	 * Move the processed file to another folder specified
	 * 
	 * @param fromFilePath
	 * @param toFilePath
	 */
	public static void moveProcessedFiles(String fromFilePath, String toFilePath) {
		try {
			File afile = new File(fromFilePath);

			if (afile.renameTo(new File(toFilePath + afile.getName()+System.currentTimeMillis()))) {
				logger.debug("File is moved successful!");
			} else {
				logger.debug("File is failed to move!");
			}
		} catch (Exception e) {
			logger.error("Exception moving the file - " + fromFilePath, e);
		}
	}
	
}
