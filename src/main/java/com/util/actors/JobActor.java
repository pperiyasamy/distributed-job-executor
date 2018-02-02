package com.util.actors;

import java.util.concurrent.CompletableFuture;
import com.util.jobs.Job;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class JobActor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private final String jobKey;

  public JobActor(String jobKey) {
    this.jobKey = jobKey;
  }

  public static Props props(String jobKey) {
    return Props.create(JobActor.class, jobKey);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(Job.class, job -> apply(job)).build();
  }

  private void apply(Job job) {
    if (! job.getJobKey().equals(jobKey)) {
      log.error("Job key mismatch. won't execute the job {}", job);
    }
    try {
      CompletableFuture<Void> future = job.getTask().call();
      future.whenComplete((a, ex) -> {
        if (ex != null) {
          log.error("job {} is failed with exception {}", job, ex);
        }
      }
          );
    } catch (Exception e) {
      log.error("Exception while executing the job {}", job);
    }
  }

}
