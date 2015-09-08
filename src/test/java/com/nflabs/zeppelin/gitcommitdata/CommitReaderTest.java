package com.nflabs.zeppelin.gitcommitdata;

import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Test;

import com.nflabs.zeppelin.gitcommitdata.CommitReader;

public class CommitReaderTest {

  @Test
  public void testRead() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
    CommitReader commit = new CommitReader();
    commit.read("https://github.com/apache/incubator-zeppelin.git", "master");
  }

}
