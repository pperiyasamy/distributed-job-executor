package com.util.actors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class JobExecutor extends AbstractActor {

  final Map<String, ActorRef> jobKeyToActor = new HashMap<>();

  static public Props props() {
    return Props.create(JobExecutor.class, () -> new JobExecutor());
  }

  static public abstract class Task implements Callable<CompletableFuture<Void>>,
  Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
  }

  static public class Job {
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

  private void onExecute(Job job) {
    String jobKey = job.getJobKey();
    ActorRef jobActor = jobKeyToActor.get(jobKey);
    if (jobActor == null) {
      jobActor = getContext().actorOf(JobActor.props(jobKey), "jobKey-" + jobKey);
      jobKeyToActor.put(jobKey, jobActor);
    }
    jobActor.forward(job, getContext());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(Job.class, this::onExecute)
           .build();
  }

}
