# distributed-job-executor

This project aims at executing java tasks in fault tolerant way in distributed
environment using Akka Remoting/Clustering

JobDelegator can run in multiple systems which are part of jobdelegator
cluster role.

JobExecutor runs only on specific node which receives jobs from JobDelegator
and execute it using JobActor.