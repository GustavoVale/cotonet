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
package br.com.riselabs.cotonet.test.builder.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import br.com.riselabs.cotonet.builder.commands.ExternalGitCommand;
import br.com.riselabs.cotonet.builder.commands.ExternalGitCommand.CommandType;
import br.com.riselabs.cotonet.model.beans.ChunkBlame;
import br.com.riselabs.cotonet.model.beans.MergeScenario;
import br.com.riselabs.cotonet.test.helpers.ConflictBasedRepositoryTestCase;

/**
 * 
 * @author Alcemir R. Santos
 *
 */
public class ExternalGitComandTest extends ConflictBasedRepositoryTestCase {

	@Test
	public void buildChunckBasedNetworkCommandLineBased() throws Exception {
		MergeScenario aScenario = setCollaborationScenarioInTempRepository();
		runMerge(aScenario);
		
		String mergedfilepath = db.getDirectory().getParent().concat(File.separator+"Bar.java");
		ExternalGitCommand egit = new ExternalGitCommand();
		List<ChunkBlame> chunksBlames = egit.setType(CommandType.BLAME).setDirectory(new File(mergedfilepath)).call();
		
		Iterator<ChunkBlame> iBlames = chunksBlames.iterator();
		ChunkBlame aBlame = iBlames.next();
		assertTrue(aBlame.getCommit().equals("HEAD"));
		Iterator<Entry<Integer, String>> iLines = aBlame.getResult().getLineAuthorsMap().entrySet().iterator();
		Entry<Integer, String> anEntry = iLines.next();
		assertTrue(anEntry.getKey()==3);
		assertTrue(anEntry.getValue().equals("devb@project.com"));
		anEntry = iLines.next();
		assertTrue(anEntry.getKey()==4);
		assertTrue(anEntry.getValue().equals("devc@project.com"));
		assertFalse(iLines.hasNext());
		
		aBlame = iBlames.next();
		iLines = aBlame.getResult().getLineAuthorsMap().entrySet().iterator();
		anEntry = iLines.next();
		assertTrue(anEntry.getKey()==3);
		assertTrue(anEntry.getValue().equals("deva@project.com"));
		anEntry = iLines.next();
		assertTrue(anEntry.getKey()==4);
		assertTrue(anEntry.getValue().equals("deva@project.com"));
		anEntry = iLines.next();
		assertTrue(anEntry.getKey()==5);
		assertTrue(anEntry.getValue().equals("deve@project.com"));
		assertFalse(iLines.hasNext());
		
		assertFalse(iBlames.hasNext());
	}
}