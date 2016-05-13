package br.com.riselabs.cotonet.test.model.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jgit.junit.RepositoryTestCase;
import org.junit.Before;
import org.junit.Test;

import br.com.riselabs.cotonet.model.beans.ConflictBasedNetwork;
import br.com.riselabs.cotonet.model.beans.DeveloperEdge;
import br.com.riselabs.cotonet.model.beans.DeveloperNode;
import br.com.riselabs.cotonet.model.dao.validators.ConflictBasedNetworkValidator;
import br.com.riselabs.cotonet.model.exceptions.InvalidCotonetBeanException;

public class ConflictBasedNetworkTest extends RepositoryTestCase {

	private ConflictBasedNetwork connet;

	@Before
	public void setUp(){
		connet = new ConflictBasedNetwork();
	}
	
	@Test
	public void checkWithEdgesButEmptyNodes() {
		connet.add(new DeveloperEdge(1,2));
		assertTrue(connet.getNodes().isEmpty());
		assertFalse(connet.getEdges().isEmpty());
		try {
			new ConflictBasedNetworkValidator().validate(connet);
		} catch (InvalidCotonetBeanException e) {
			assertTrue( e.getCause() instanceof IllegalArgumentException);
		}finally{
			fail("should have thrown invalid bean exception");
		}
	}
	
	@Test
	public void checkWithNodeButEmptyEdges() {
		connet.add(new DeveloperNode("test@mail"));
		assertFalse(connet.getNodes().isEmpty());
		assertTrue(connet.getEdges().isEmpty());
		try {
			new ConflictBasedNetworkValidator().validate(connet);
		} catch (InvalidCotonetBeanException e) {
			assertTrue( e.getCause() instanceof IllegalArgumentException);
		}finally{
			fail("should have thrown invalid bean exception");
		}
	}
	
	@Test
	public void getNodeByNameAndEmail(){
		ConflictBasedNetwork connet = new ConflictBasedNetwork();
		assertTrue(connet.getNodes().isEmpty());
		String aName = "DevA";
		String anEmail = "deva@project.com";
		DeveloperNode aNode = new DeveloperNode(1,aName, anEmail);
		connet.add(aNode);
		assertEquals(1, connet.getNodes().size());
		DeveloperNode resultingNode = connet.getNode(aName, anEmail);
		assertNotNull("the node should not be null.", resultingNode);
		assertTrue("both nodes should be equal.", resultingNode.equals(aNode));
	}

}
