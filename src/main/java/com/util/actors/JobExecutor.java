package com.util.actors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import com.lightbend.akka.sample.Printer;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class JobExecutor extends AbstractActor {

  final Map<String, ActorRef> jobKeyToActor = new HashMap<>();

  static public Props props() {
    return Props.create(JobExecutor.class, () -> new JobExecutor());
  }

  static public class Job {
    private final String jobKey;
    private final Callable<CompletableFuture<Void>> task;

    public Job(String jobKey, Callable<CompletableFuture<Void>> task) {
      this.jobKey = jobKey;
      this.task = task;
    }

    public String getJobKey() {
      return jobKey;
    }

    public Callable<CompletableFuture<Void>> getTask() {
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
