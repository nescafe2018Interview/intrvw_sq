package com.tiaa;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;

/**
 * This class Schedules the Accounting Check Job as per the cron expression.
 * 
 * 
 * @author Manu
 *
 */
public class JobScheduler {

	private static final Logger logger = Logger.getLogger(JobScheduler.class);
	
	/**
	 * Cron expression to run the Job daily at 11.59 pm
	 * */
	private static final String CRON_EXPRESSION = "0 59 23 1/1 * ? *";
	
	public static void main(String[] args) {

		//Create Jobdetails and the execution classs
		JobDetailImpl job = new JobDetailImpl();
		job.setName("AccountingEngine");
		job.setJobClass(AccountingEngine.class);

		// Schedule the Job as per the Cron expression configured
		ScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(CRON_EXPRESSION);
		Trigger trigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder).build();

		Scheduler scheduler;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.scheduleJob(job, trigger);
			
			//Start the scheduler
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error("Exception while scheduling Job.");
		}

	}

}
