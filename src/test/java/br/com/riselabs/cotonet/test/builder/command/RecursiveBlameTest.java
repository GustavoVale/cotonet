/**
 * 
 */
package br.com.riselabs.cotonet.test.builder.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.riselabs.cotonet.builder.ConflictBasedNetworkBuilder;
import br.com.riselabs.cotonet.builder.commands.RecursiveBlame;
import br.com.riselabs.cotonet.model.beans.Blame;
import br.com.riselabs.cotonet.model.beans.MergeScenario;
import br.com.riselabs.cotonet.model.beans.Project;
import br.com.riselabs.cotonet.test.helpers.ConflictBasedRepositoryTestCase;

/**
 * @author alcemirsantos
 *
 */
public class RecursiveBlameTest extends ConflictBasedRepositoryTestCase {

	private ConflictBasedNetworkBuilder builder;

	@Before
	public void setup() {
		builder = new ConflictBasedNetworkBuilder();
	}

	@Test
	public void shouldRetriveBlamesFromTheRightBranch() throws Exception {
		builder.setProject(new Project("", db));
		MergeScenario scenario = setCollaborationScenarioInTempRepository();
		RecursiveBlame blame = new RecursiveBlame();

		List<Blame> blames = blame.setRepository(db)
				.setBeginRevision(scenario.getRight())
				.setEndRevision(scenario.getBase()).setFilePath("Foo.java")
				.call();
		
		assertTrue(blames.size()==3);
		Iterator<Blame> i = blames.iterator();
		Blame b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("s3"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("s2"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("s1"));
		assertFalse(i.hasNext());
	}
	
	@Test
	public void shouldRetriveBlamesFromTheLeftBranch() throws Exception {
		builder.setProject(new Project("", db));
		MergeScenario scenario = setCollaborationScenarioInTempRepository();
		RecursiveBlame blame = new RecursiveBlame();

		List<Blame> blames = blame.setRepository(db)
				.setBeginRevision(scenario.getLeft())
				.setEndRevision(scenario.getBase()).setFilePath("Foo.java")
				.call();
		
		assertTrue(blames.size()==3);
		Iterator<Blame> i = blames.iterator();
		Blame b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("m3"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("m2"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("m1"));
		assertFalse(i.hasNext());
	}
	
	
	@Test
	public void shouldRetriveBlamesFromBothBranch() throws Exception {
		builder.setProject(new Project("", db));
		MergeScenario scenario = setCollaborationScenarioInTempRepository();
		RecursiveBlame blame = new RecursiveBlame();

		List<Blame> blames = blame.setRepository(db)
				.setBeginRevision(scenario.getRight())
				.setEndRevision(scenario.getBase()).setFilePath("Foo.java")
				.call();
		blames.addAll(blame.setRepository(db)
				.setBeginRevision(scenario.getLeft())
				.setEndRevision(scenario.getBase()).setFilePath("Foo.java")
				.call());
		
		assertTrue(blames.size()==6);
		Iterator<Blame> i = blames.iterator();
		Blame b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("s3"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("s2"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("s1"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("m3"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("m2"));
		b = i.next();
		assertTrue(b.getCommit().getFullMessage().equals("m1"));
		assertFalse(i.hasNext());
	}
}
