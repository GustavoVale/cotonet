/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alcemir R. Santos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package br.com.riselabs.cotonet.test.helpers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.eclipse.jgit.junit.TestRepository;
import org.eclipse.jgit.junit.TestRepository.BranchBuilder;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import br.com.riselabs.cotonet.model.beans.MergeScenario;

/**
 * 
 * @author Alcemir R. Santos
 *
 */
public abstract class ConflictBasedRepositoryTestCase extends
		RepositoryTestCase {

	public void printDBpath() {
		System.out.println(db.getDirectory().getAbsolutePath());
	}

	public MergeResult runMerge(MergeScenario scenario)
			throws RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		Git git = Git.wrap(db);
		CheckoutCommand ckoutCmd = git.checkout();
		ckoutCmd.setName(scenario.getLeft().getName());
		ckoutCmd.setStartPoint(scenario.getLeft());
		ckoutCmd.call();

		MergeCommand mergeCmd = git.merge();
		mergeCmd.setCommit(false);
		mergeCmd.include(scenario.getRight());
		return mergeCmd.call();
	}

	public static void printMergeResult(MergeResult result) {
		Map<String, int[][]> allConflicts = result.getConflicts();

		// looping over the files
		for (String path : allConflicts.keySet()) {
			int[][] c = allConflicts.get(path);
			System.out.println("Conflicts in file " + path);

			// looping over the file conflicts
			for (int i = 0; i < c.length; ++i) {
				System.out.println("  Conflict #" + i);

				// looping over the conflicting chunks.
				for (int j = 0; j < (c[i].length) - 1; ++j) {
					if (c[i][j] >= 0)
						System.out.println("    Chunk for "
								+ result.getMergedCommits()[j]
								+ " starts on line #" + c[i][j]);
				}
			}
		}
	}

	public static void logAllCommits(Repository repo) throws Exception {
		Git git = Git.wrap(repo);
		Iterable<RevCommit> commits = git.log().all().call();
		for (RevCommit c : commits) {
			System.out.println("time(s): " + c.getCommitTime() + ", author: "
					+ c.getAuthorIdent().getName());
		}
	}

	protected Map<String, PersonIdent> devs = getDevs();

	private Map<String, PersonIdent> getDevs() {
		Map<String, PersonIdent> devs = new HashMap<String, PersonIdent>();
		devs.put("devY", new PersonIdent("Dev Y", "devy@project.com"));
		devs.put("devX", new PersonIdent("Dev X", "devx@project.com"));
		devs.put("devA", new PersonIdent("Dev A", "deva@project.com"));
		devs.put("devB", new PersonIdent("Dev B", "devb@project.com"));
		devs.put("devC", new PersonIdent("Dev C", "devc@project.com"));
		devs.put("devD", new PersonIdent("Dev D", "devd@project.com"));
		devs.put("devE", new PersonIdent("Dev E", "deve@project.com"));
		return devs;
	}

	/**
	 * Creates a collaboration scenario with five developers (Devs A, B, C, D,
	 * and E) and two files (Foo.java and Bar.java).
	 * 
	 * @return
	 * @throws Exception
	 */
	public MergeScenario setCollaborationScenarioInTempRepository()
			throws Exception {
		Git git = Git.wrap(db);

		RevCommit mergeBaseCommit, lastMasterCommit, lastSideCommit;

		// first versions of Foo and Bar
		writeTrashFile("Foo.java", "1");
		writeTrashFile("Bar.java", "1");
		git.add().addFilepattern("Foo.java").addFilepattern("Bar.java").call();
		git.commit().setMessage("initial commit").setAuthor(devs.get("devY"))
				.call();

		writeTrashFile("Foo.java", "1\n2\n3\n4\n5\n6\n7\n8\n");
		writeTrashFile("Bar.java", "1\n2\n3\n4\n");
		git.add().addFilepattern("Foo.java").addFilepattern("Bar.java").call();
		mergeBaseCommit = git.commit().setMessage("m0")
				.setAuthor(devs.get("devX")).call();

		// Dev E changes Foo
		writeTrashFile("Foo.java", "1\n2\n3\n4\n5-master\n6\n7\n8\n");
		git.add().addFilepattern("Foo.java").call();
		git.commit().setMessage("m1").setAuthor(devs.get("devE")).call();

		// Dev C changes Foo
		writeTrashFile("Foo.java", "1\n2\n3\n4-master\n5\n6\n7\n8\n");
		writeTrashFile("Bar.java", "1\n2\n3-master\n4-master\n");
		git.add().addFilepattern("Foo.java").addFilepattern("Bar.java").call();
		git.commit().setMessage("m2").setAuthor(devs.get("devC")).call();

		// Dev B changes
		writeTrashFile("Bar.java", "1\n2\n3\n4-master\n");
		git.add().addFilepattern("Bar.java").call();
		lastMasterCommit = git.commit().setMessage("m3")
				.setAuthor(devs.get("devB")).call();

		// updating the tree with the changes
		createBranch(mergeBaseCommit, "refs/heads/side");
		checkoutBranch("refs/heads/side");

		// Dev D changes Foo
		writeTrashFile("Foo.java", "1\n2\n3\n4-side\n5\n6\n7\n8\n");
		git.add().addFilepattern("Foo.java").call();
		git.commit().setMessage("s1").setAuthor(devs.get("devD")).call();

		// Dev E changes Bar
		writeTrashFile("Bar.java", "1\n2\n3\n4\n5\n");
		git.add().addFilepattern("Bar.java").call();
		git.commit().setMessage("s2").setAuthor(devs.get("devE")).call();

		// Dev A changes Bar
		writeTrashFile("Bar.java", "1\n2\n3-side\n4-side\n5\n");
		git.add().addFilepattern("Bar.java").call();
		lastSideCommit = git.commit().setMessage("s3")
				.setAuthor(devs.get("devA")).call();

		return new MergeScenario(null, mergeBaseCommit, lastMasterCommit,
				lastSideCommit, null, null);
	}

	/**
	 * Creates a collaboration scenario with five developers (Devs A, B, C, D,
	 * and E) and two files (Foo.java and Bar.java).
	 * 
	 * @return
	 * @throws Exception
	 */
	public MergeScenario setCollaborationScenarioInBareRepository()
			throws Exception {
		Git git = Git.wrap(db);

		RevCommit mergeBaseCommit, lastMasterCommit, lastSideCommit;

		TestRepository<Repository> db_t = new TestRepository<Repository>(db);

		BranchBuilder master = db_t.branch("master");

		// first versions of Foo and Bar
		master.commit().add("Foo.java", "1").add("Bar.java", "1")
				.message("initial commit").author(devs.get("devY")).create();

		mergeBaseCommit = master.commit()
				.add("Foo.java", "1\n2\n3\n4\n5\n6\n7\n8\n")
				.add("Bar.java", "1\n2\n3\n4\n").message("m0")
				.author(devs.get("devX")).create();

		// Dev E changes Foo
		master.commit().add("Foo.java", "1\n2\n3\n4\n5-master\n6\n7\n8\n")
				.message("m1").author(devs.get("devE")).create();

		// Dev C changes Foo
		master.commit().add("Foo.java", "1\n2\n3\n4-master\n5\n6\n7\n8\n")
				.add("Bar.java", "1\n2\n3-master\n4-master\n").message("m2")
				.author(devs.get("devC")).create();

		// Dev B changes
		lastMasterCommit = master.commit()
				.add("Bar.java", "1\n2\n3\n4-master\n").message("m3")
				.author(devs.get("devB")).create();

		// updating the tree with the changes
		db_t.getRevWalk().parseCommit(mergeBaseCommit);

		// creating a new branc: side
		BranchBuilder side = db_t.branch("side");

		// Dev D changes Foo
		side.commit().parent(mergeBaseCommit)
				.add("Foo.java", "1\n2\n3\n4-side\n5\n6\n7\n8\n").message("s1")
				.author(devs.get("devD")).create();

		// Dev E changes Bar
		side.commit().add("Bar.java", "1\n2\n3\n4\n5\n").message("s2")
				.author(devs.get("devE")).create();

		// Dev A changes Bar
		lastSideCommit = side.commit()
				.add("Bar.java", "1\n2\n3-side\n4-side\n5\n").message("s3")
				.author(devs.get("devA")).create();

		git.checkout().setName("master").setStartPoint(lastMasterCommit).call();
		return new MergeScenario(null, mergeBaseCommit, lastMasterCommit,
				lastSideCommit, null, null);
	}

	/**
	 * Merges the scenario created by
	 * {@code #setCollaborationScenarioInBareRepository()} or
	 * {@code #setCollaborationScenarioInTempRepository()} .
	 * 
	 * @throws Exception
	 */
	public void setResolvedMergeConflictSceario() throws Exception {
		MergeScenario ms = setCollaborationScenarioInTempRepository();
		TestRepository<Repository> db_t = new TestRepository<Repository>(db);
		BranchBuilder master = db_t.branch("master");

		// Dev Y changes Bar
		master.commit().parent(ms.getLeft()).parent(ms.getRight())
				.add("Bar.java", "1\n2\n3-merged\n4-merged\n5\n")
				.add("Foo.java", "1\n2\n3\n4-merged\n5\n6\n7\n8\n")
				.message("merge s3 into m3").author(devs.get("devY")).create();
	}
	
	/**
	 * Merges the scenario created by
	 * {@code #setCollaborationScenarioInBareRepository()} or
	 * {@code #setCollaborationScenarioInTempRepository()} .
	 * 
	 * @throws Exception
	 */
	public void setResolvedMergeConflictScenario() throws Exception {
		MergeScenario ms = setCollaborationScenarioInTempRepository();
		Git git = Git.wrap(db);
		checkoutBranch("refs/heads/master");
		// Dev Y changes Bar
		git.merge().include(ms.getRight()).call();
		writeTrashFile("Bar.java", "1\n2\n3-merged\n4-merged\n5\n");
		writeTrashFile("Foo.java", "1\n2\n3\n4-merged\n5\n6\n7\n8\n");
		git.add().addFilepattern("Bar.java").addFilepattern("Foo.java").call();
		git.commit().setMessage("merge s3 into m3").setAuthor(devs.get("devY")).call();
	}
}
