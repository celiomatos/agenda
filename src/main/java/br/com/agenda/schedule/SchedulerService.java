package br.com.agenda.schedule;

import br.com.agenda.job.MyJob;
import br.com.agenda.job.OtherJob;
import br.com.agenda.quartz.JobScheduleCreator;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreator scheduleCreator;

    /**
     *
     */
    public void startAllSchedulers() {

        List<SchedulerJobInfo> jobInfoList = schedulerRepository.findAll();;

        jobInfoList.forEach(jobInfo -> {
            if (jobInfo.isEnable()) {
                scheduleNewJob(jobInfo);
            }
        });
    }

    /**
     * @param jobInfo
     */
    public boolean scheduleNewJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());

            if (scheduler.checkExists(jobKey)) {
                deleteJob(jobInfo);
            }

            JobDetail jobDetail = getJobDetail(jobInfo);
            if (jobDetail != null) {
                Trigger trigger = getTrigger(jobInfo);

                scheduler.scheduleJob(jobDetail, trigger);

            }
            return true;
        } catch (SchedulerException e) {
            System.out.println(e.getMessage() + e);
            return false;
        }
    }

    /**
     * @param jobInfo
     * @return
     */
    private JobDetail getJobDetail(SchedulerJobInfo jobInfo) {

        JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());

        System.out.println(jobInfo.isCron() ? jobInfo.getExpression() : jobInfo.getRepeatTime());
        JobDetail jobDetail = null;

        if (jobKey.getName().equalsIgnoreCase("myjob")) {
            scheduleCreator.createJob(MyJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(jobKey).build();
        } else if (jobKey.getName().equalsIgnoreCase("otherjob")) {
            scheduleCreator.createJob(OtherJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(OtherJob.class).withIdentity(jobKey).build();
        }
        return jobDetail;
    }

    /**
     * @param jobInfo
     * @return
     */
    private Trigger getTrigger(SchedulerJobInfo jobInfo) {

        if (jobInfo.isCron() && CronExpression.isValidExpression(jobInfo.getExpression())) {
            Trigger trigger = scheduleCreator.createCronTrigger(jobInfo.getName(), null,
                    jobInfo.getExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
            return trigger;
        }

        return null;
    }

    public boolean deleteJob(SchedulerJobInfo jobInfo) {
        try {
            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            return schedulerFactoryBean.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            System.out.println("Failed to delete job - {}"+ jobInfo.getName() + e);
            return false;
        }
    }
}
