

import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.util.actors.JobDelegator;
import com.util.actors.JobExecutor;
import com.util.jobs.Job;
import com.util.jobs.Task;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

public class JobExecutorTest {

  public static void createJobExecutorSystem() {
    final ActorSystem system =
        ActorSystem.create("JobExecutorSystem", ConfigFactory.load(("jobexecutor")));
    system.actorOf(Props.create(JobExecutor.class), "jobexecutor");
  }

  public static void createJobDelegatorSystem() {
    String[] ports = {"2552", "2553"};
    for (String port : ports) {
      Config config = ConfigFactory
          .parseString("akka.remote.netty.tcp.port=" + port)
          .withFallback(ConfigFactory.load("jobdelegator"));

      ActorSystem system = ActorSystem.create("JobExecutorLookupSystem", config);

      final String path = "akka.tcp://JobExecutorSystem@127.0.0.1:2551/user/jobexecutor";
      final ActorRef actor = system.actorOf(Props.create(JobDelegator.class, path), "jobdelegator");
      Job job1 = getJob("jk1");
      Job job2 = getJob("jk2");

      System.out.println("Started JobExecutorLookupSystem");

      final Random r = new Random();
      system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, SECONDS),
          new Runnable() {
            @Override
            public void run() {
              if (r.nextInt(100) % 2 == 0) {
                actor.tell(job1, null);
              } else {
                actor.tell(job2, null);
              }

            }
          }, system.dispatcher());
    }
  }

  private static Job getJob(String jobKey) {
    return new Job(jobKey, new Task() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public CompletableFuture<Void> call() throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        System.out.println("running the task: " + jobKey);
        future.complete(null);
        return future;
      }
    });
  }

  public static void main(String[] args) {
    if (args.length == 0 || args[0].equals("JobExecutor"))
      createJobExecutorSystem();
    if (args.length == 0 || args[0].equals("JobDelegator"))
      createJobDelegatorSystem();
  }

}
