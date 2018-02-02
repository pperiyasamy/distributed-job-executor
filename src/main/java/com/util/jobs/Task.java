package com.util.jobs;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public abstract class Task implements Callable<CompletableFuture<Void>>, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
}
