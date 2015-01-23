package in.xebia.jobs;

import in.xebia.repository.CustomerRepository;
import in.xebia.repository.DealRepository;
import in.xebia.services.MailService;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class PersistentJobSchedulerJob {
  private static Logger logger = Logger.getLogger("PersistentJobSchedulerJob");
  @Autowired
  private CustomerRepository CustomerRepository;
  @Autowired
  private DealRepository DealRepository;
  @Autowired
  private MailService mailService;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Scheduled(fixedRate = 30)
  public void schedulePersistentJobs() {
    int count = 1;
//    Customer jobData = CustomerRepository.findByEmail("ram@eggheadcreative.com");
    List<Customer> jobsData = CustomerRepository.findAll();
    List<Deal> dealdata = DealRepository.findAll();
    System.out.printf("   customer      :"+jobsData);
    logger.info("Retriving Jobs from Database and Scheduling One by One | Total Number of Jobs: " + jobsData.size());
    System.out.println("Retriving Jobs from Database and Scheduling One by One | Total Number of Jobs: " + jobsData.size());
    System.out.println(new Date());
    try {
      Scheduler scheduler = new StdSchedulerFactory().getScheduler();
      scheduler.start();
      for (Customer jobData : jobsData) {
        System.out.printf("   customer      :"+jobData);
//      if (jobData != null) {
        JobDetail job = newJob(MailSenderJob.class)
            .withIdentity(jobData.getFirstName())
            .usingJobData(getCustomerMap(jobData))
            .build();
       /* if (jobData.getDateOfBirth().contains(new Date())) {
          logger.info("Deleting a Job");
          if (scheduler.checkExists(new JobKey(jobData.getFirstName())))
            scheduler.deleteJob(new JobKey(jobData.getFirstName()));
          continue;
        }*/
        if (scheduler.checkExists(new JobKey("female"))) {
          logger.info("Rescheduling the Job");
          System.out.println("Rescheduling the Job");
          Trigger oldTrigger = scheduler.getTrigger(new TriggerKey(jobData.getFirstName() + "Trigger"));
          TriggerBuilder tb = oldTrigger.getTriggerBuilder();
          Trigger newTrigger = tb.withSchedule(simpleSchedule()
              .withIntervalInMilliseconds(10))
              .build();
//              .repeatForever())

          scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
        } else {
          logger.info("Scheduling the Job");
          scheduler.scheduleJob(job, getTrigger(jobData));
        }
      }
    } catch (SchedulerException e) {
      logger.error("Scheduler Exception : " + e.getMessage());
    }
  }

  private Trigger getTrigger(Customer jobData) {
    SimpleTrigger simpleTrigger = newTrigger().withIdentity(jobData.getFirstName() + "Trigger")
        .startAt(new Date())
        .withSchedule(simpleSchedule()
            .withIntervalInMilliseconds(100)).
        /*repeatForever()*/
        build();
    return simpleTrigger;
  }

  private JobDataMap getCustomerMap(Customer jobData) {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("recipients", jobData.getFirstName());
    jobDataMap.put("recipients", jobData.getGender());
    jobDataMap.put("recipients", jobData.getEmail());
    jobDataMap.put("mailService", mailService);
    return jobDataMap;
  }
}
