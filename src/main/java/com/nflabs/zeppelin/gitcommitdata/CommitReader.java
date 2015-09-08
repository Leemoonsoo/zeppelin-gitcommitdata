package com.nflabs.zeppelin.gitcommitdata;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.interpreter.data.TableData;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitReader {
  public CommitReader() {
  }

  public TableData read(String repoUrl, String branch) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
    File tmpDir = File.createTempFile("CommitReader", "");
    tmpDir.delete();

    Git result = Git.cloneRepository().setURI(repoUrl).setDirectory(tmpDir).call();
    Repository repo = result.getRepository();
    Git git = new Git(repo);
    Iterable<RevCommit> commits = git.log().call();

    StringWriter writer = new StringWriter();
    writer.write("%table commitId\ttime\tname\temail\tmessage\n");
    for (RevCommit commit : commits) {
      writer.write(commit.name());
      writer.write("\t");

      writer.write(Integer.toString(commit.getCommitTime()));
      writer.write("\t");

      writer.write(commit.getAuthorIdent().getName());
      writer.write("\t");

      writer.write(commit.getAuthorIdent().getEmailAddress());
      writer.write("\t");

      writer.write(commit.getShortMessage());
      writer.write("\n");
    }

    repo.close();
    FileUtils.deleteDirectory(tmpDir);

    String data = writer.toString();
    return new TableData(new InterpreterResult(Code.SUCCESS, data));
  }
}
