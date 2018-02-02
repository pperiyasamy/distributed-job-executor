package com.util.actors;

import java.util.HashMap;
import java.util.Map;
import com.util.jobs.Job;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class JobExecutor extends AbstractActor {
  final Map<String, ActorRef> jobKeyToActor = new HashMap<>();

  static public Props props() {
    return Props.create(JobExecutor.class, () -> new JobExecutor());
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
