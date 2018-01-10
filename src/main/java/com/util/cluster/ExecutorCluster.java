package com.util.cluster;

import com.typesafe.config.ConfigFactory;
import com.util.actors.JobExecutor;
import akka.actor.ActorSystem;

public class ExecutorCluster {

  public static void startup() {
    ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.load("jobexecutor"));
    system.actorOf(JobExecutor.props(), "job-executor");;
  }

}
