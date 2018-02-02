package com.util.actors;

import static java.util.concurrent.TimeUnit.SECONDS;
import com.util.jobs.Job;
import akka.actor.AbstractActor;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import scala.concurrent.duration.Duration;

public class JobDelegator extends AbstractActor {
  private String path;
  private ActorRef jobExecutor;

  public JobDelegator(String path) {
    this.path = path;
    sendIdentifyRequest();
  }

  private void sendIdentifyRequest() {
    getContext().actorSelection(path).tell(new Identify(path), self());
    getContext().system().scheduler().scheduleOnce(Duration.create(3, SECONDS), self(),
        ReceiveTimeout.getInstance(), getContext().dispatcher(), self());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(ActorIdentity.class, identity -> {
      jobExecutor = identity.getRef();
      if (jobExecutor == null) {
        System.out.println("Remote actor not available: " + path);
      } else {
        getContext().watch(jobExecutor);
        getContext().become(handleJob, true);
      }
    }).match(ReceiveTimeout.class, x -> {
      sendIdentifyRequest();
    }).build();
  }

  Receive handleJob = receiveBuilder().match(Job.class, job -> {
    // send the job to job executor actor
    jobExecutor.tell(job, self());
  }).match(Terminated.class, terminated -> {
    System.out.println("JobExecutor terminated");
    sendIdentifyRequest();
    getContext().unbecome();
  }).match(ReceiveTimeout.class, message -> {
    // ignore
  }).build();

}
