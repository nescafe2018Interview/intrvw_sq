package com.tiaa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.tiaa.entity.Branch;
import com.tiaa.entity.Cmfoodchain;
import com.tiaa.entity.Wrapper;
import com.tiaa.utility.Utility;

/**
 * 
 * This class creates multiple threads to process the input files.
 * After all the thread has completed the execution, it combines the result of each thread to form the output.
 * 
 * Output is 2 files : Match.json for all the accounts of branches that matched and
 * 					   Mismatch.json for all the accounts of branches that didn't match
 * Timestamp appended to the file name just to ensure new files are created.
 * @author Manu
 *
 */
public class AccountingEngine implements Job{

	private static final Logger logger = Logger.getLogger(AccountingEngine.class);
	
	//Create a FixedPool Execution service
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	//The path where the source files should be configured
	private static final String SOURCE_FILE_PATH = "D:\\Files\\source";
	
	//The path where the results of Successful Matched files will be stored. The file name will be appended with timestamp
	private static final String RESULT_FILE_PATH = "D:\\Files\\result\\Match";
	
	//The path where the results of Mis-Matched files will be stored. The file name will be appended with timestamp
	private static final String MISMATCH_RESULT_FILE_PATH = "D:\\Files\\result\\Mismatch";
	
	//The path where the source files will be moved after their processing
	private static final String PROCESSED_FILE_PATH = "D:\\Files\\processed\\";
	
	public void execute(JobExecutionContext context) throws JobExecutionException {

		//List of future objects to hold the results of each thread
		List<Future<Object>> foodChainDetails = new ArrayList<Future<Object>>();
		try {
			
			//Fetch all the files available to be processed
			List<String> filesToProcess = getAllFilesToProcess(new File(SOURCE_FILE_PATH));
			
			int count = 0;
			//Use a thread from the threadpool to process a file
			while (count < filesToProcess.size()) {
				Future<Object> future = executorService.submit(new AccountingJob(filesToProcess.get(count++),PROCESSED_FILE_PATH));
				foodChainDetails.add(future);
			}
			
			//Shutdown the Execution service
			executorService.shutdown();
			
			//Process the output of all the threads
			processResultsIfAllTasksDone(foodChainDetails);
			
		} catch (Exception e) {
			logger.error("Exception occcured while  executing Accounting Job",e);
		}
	
	}
	
	/**
	 * 
	 * Fetch all the files available to be processed present in the folder
	 * @param folder
	 * 
	 * 
	 * @return List<String> list of fileNames 
	 */
	public List<String> getAllFilesToProcess(File folder) {
		List<String> filesToProcess = new ArrayList<String>();
	    for (File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	filesToProcess.add(fileEntry.getAbsolutePath());
	        } 
	    }
	    return filesToProcess;
	}
	
	/**
	 * 
	 * Process all the Future tasks i.e output from all thread to fetch the Matched and Mismatched results
	 * 
	 * @param foodChainDetails
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void processResultsIfAllTasksDone(List<Future<Object>> foodChainDetails) throws InterruptedException, ExecutionException {
		//Keep checking if all the threads have finished execution
		while (true) {
			//Check if all the thread has returned the results after processing
			if (executorService.isTerminated()) {
				List<Branch> honestbranches = new ArrayList<Branch>();
				List<Branch> corruptedBranches = new ArrayList<Branch>();
				
				//Iterate through all the Future task and check for Matched and Mismatched Branches
				for (Future<Object> future : foodChainDetails) {
					Branch branch = (Branch) future.get();
					//Check the flag if the accountings match
					if (branch.isCorrectAccounting()) {
						honestbranches.add(branch);
					} else {
						corruptedBranches.add(branch);
					}
				}

				//Create the output file for Matched accounting Branches
				createResults(honestbranches,RESULT_FILE_PATH);

				//Create the output files for Mismatched accounting Branches
				createResults(corruptedBranches,MISMATCH_RESULT_FILE_PATH);
				
				//Break the while loop
				break;
			} else {
				try {
					Thread.sleep(2000);// Wait for 2 seconds to check again.
				} catch (InterruptedException e) {
					logger.error("Main thread interrupted");
					throw e; 
				}
			}
		}
	}
	
	/**
	 * Create the output files
	 * 
	 * @param branchesData
	 * @param resultFilePath
	 */
	private void  createResults(List<Branch> branchesData, String resultFilePath) {
		if (!branchesData.isEmpty()) {
			
			Cmfoodchain resultFoodChain = new Cmfoodchain();
			resultFoodChain.setBranch(branchesData);
			
			Wrapper wrapper = new Wrapper();
			List<Cmfoodchain> foodChains = new ArrayList<Cmfoodchain>();
			foodChains.add(resultFoodChain);
			wrapper.setCmfoodchain(foodChains);

			String resultJSON = Utility.serializeJSON(wrapper);
			Utility.createFile(resultJSON, resultFilePath+System.currentTimeMillis()+".json");
		}
	}
}