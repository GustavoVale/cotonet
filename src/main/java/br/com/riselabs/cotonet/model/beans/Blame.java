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
package br.com.riselabs.cotonet.model.beans;

import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * @author Alcemir R. Santos
 *
 */
public class Blame<T> {
	
	private RevCommit revision; 
	private T result;
	
	public Blame(RevCommit aCommit, T aResult){
		setRevision(aCommit);
		setResult(aResult);
	}
	
	public RevCommit getRevision() {
		return revision;
	}

	public void setRevision(RevCommit commit) {
		this.revision = commit;
	}
	
	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		String filepath = null;
		if (result instanceof CommandLineBlameResult) {
			filepath = ((CommandLineBlameResult)result).getFilePath();
		}else if (result instanceof BlameResult) {
			filepath = ((BlameResult)result).getResultPath();
		}
		return "['"+ filepath +"' blame @ "
				+ "Commit ('"
				+ this.revision.getFullMessage() + "')]";
	}
	
}
