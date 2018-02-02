package com.util.jobs;

public class Job {
  private final String jobKey;
  private final Task task;

  public Job(String jobKey, Task task) {
    this.jobKey = jobKey;
    this.task = task;
  }

  public String getJobKey() {
    return jobKey;
  }

  public Task getTask() {
    return task;
  }

  @Override
  public String toString() {
    return "Job [jobKey=" + jobKey + ", task=" + task + "]";
  }

}
