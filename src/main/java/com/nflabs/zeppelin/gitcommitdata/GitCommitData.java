package com.nflabs.zeppelin.gitcommitdata;

import java.io.IOException;

import org.apache.zeppelin.helium.Application;
import org.apache.zeppelin.helium.ApplicationArgument;
import org.apache.zeppelin.helium.ApplicationException;
import org.apache.zeppelin.helium.Signal;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.data.TableData;
import org.apache.zeppelin.interpreter.dev.ZeppelinApplicationDevServer;
import org.apache.zeppelin.resource.WellKnownResource;
import org.eclipse.jgit.api.errors.GitAPIException;



public class GitCommitData extends Application {
  private static String REPO = "gitRepo";
  private static String BRANCH = "branch";
  private static String LOAD_BUTTON = "load";
  private static String MSG = "msg";
  private static String DEFAULT_REPO = "https://github.com/apache/incubator-zeppelin.git";
  private static String DFFAULT_BRANCH = "master";
  private InterpreterContext context;

  @Override
  protected void onChange(String name, Object oldObject, Object newObject) {
    if (name.equals(LOAD_BUTTON)) {

      TableData tableData;
      CommitReader reader = new CommitReader();
      try {
        put(context, MSG, "");
        tableData = reader.read((String) get(context, REPO), (String) get(context, BRANCH));
        put(context, MSG, tableData.length() + " commits loaded");

        context.getResourcePool().put(
            WellKnownResource.resourceName(
                WellKnownResource.TABLE_DATA,
                WellKnownResource.INSTANCE_RESULT,
                context.getNoteId(), context.getParagraphId()),
            tableData);

      } catch (IOException | GitAPIException e) {
        put(context, MSG, e.getMessage());
      }

      // set to idle
      put(context, LOAD_BUTTON, "idle");
    }
  }

  @Override
  public void signal(Signal signal) {
  }

  @Override
  public void load() throws ApplicationException, IOException {
  }

  @Override
  public void run(ApplicationArgument arg, InterpreterContext context) throws ApplicationException,
      IOException {
    this.context = context;
    context.out.writeResource("git-commit-data/GitCommitData.html");

    String gitRepo = (String) get(context, REPO);
    if (gitRepo == null || gitRepo.isEmpty()) {
      put(context, REPO, DEFAULT_REPO);
    }


    String gitBranch = (String) get(context, BRANCH);
    if (gitBranch == null || gitBranch.isEmpty()) {
      put(context, BRANCH, DFFAULT_BRANCH);
    }

    put(context, MSG, "");
    put(context, LOAD_BUTTON, "idle");
    watch(context, LOAD_BUTTON);
  }

  @Override
  public void unload() throws ApplicationException, IOException {
  }

  /**
   * Development mode
   * @param args
   * @throws Exception
   */
  public static void main(String [] args) throws Exception {
    // create development server
    ZeppelinApplicationDevServer dev = new ZeppelinApplicationDevServer(GitCommitData.class.getName());

    // start
    dev.server.start();
    dev.server.join();
  }
}
