package in.xebia.jobs;

import in.xebia.services.MailService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

public class MailSenderJob implements Job {

  private static Logger logger = Logger.getLogger("MailSenderJob");

  @SuppressWarnings("rawtypes")
  public void execute(JobExecutionContext context)
      throws JobExecutionException {
    logger.info("In MailSenderJob");
    System.out.println("In MailSenderJob");
    MailService mailService = (MailService) context.getJobDetail().getJobDataMap().get("mailService");
    Map dataMap = context.getJobDetail().getJobDataMap();
    String recipients = (String) dataMap.get("recipients");
    logger.info("Sending mails to : " + recipients);
    System.out.println("Sending mails to : " + recipients);
    System.out.println("mails server is : " + mailService);
    mailService.send(recipients);
  }
}
