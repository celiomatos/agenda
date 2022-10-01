package br.com.agenda.schedule;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "scheduler_job_info")
public class SchedulerJobInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "job_name", unique = true, nullable = false)
    private String name;

    @Column(name = "job_group", nullable = false)
    private String group;

    @Column(name = "job_enable", nullable = false)
    private boolean enable;

    @Column(name = "job_cron_expression")
    private String expression;

    @Column(name = "job_repeat_time")
    private Long repeatTime;

    @Column(name = "job_cron", nullable = false)
    private boolean cron;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Long getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(Long repeatTime) {
        this.repeatTime = repeatTime;
    }

    public boolean isCron() {
        return cron;
    }

    public void setCron(boolean cron) {
        this.cron = cron;
    }
}
