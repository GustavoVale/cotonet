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
package br.com.riselabs.cotonet.model.dao.validators;

import br.com.riselabs.cotonet.model.beans.ConflictBasedNetwork;
import br.com.riselabs.cotonet.model.beans.DeveloperEdge;
import br.com.riselabs.cotonet.model.beans.DeveloperNode;
import br.com.riselabs.cotonet.model.dao.ConflictBasedNetworkDAO;
import br.com.riselabs.cotonet.model.dao.DAOFactory;
import br.com.riselabs.cotonet.model.dao.DeveloperNodeDAO;
import br.com.riselabs.cotonet.model.dao.DAOFactory.CotonetBean;
import br.com.riselabs.cotonet.model.enums.NetworkType;

/**
 * @author Alcemir R. Santos
 *
 */
public class DeveloperEdgeValidator implements Validator<DeveloperEdge> {

	@Override
	public boolean validate(DeveloperEdge edge) {
		if (edge == null || edge.getLeft() == null || edge.getRight() == null
				|| edge.getNetworkID() == null) {
			return false;
		}
		DeveloperNodeDAO dndao = (DeveloperNodeDAO) DAOFactory
				.getDAO(CotonetBean.NODE);
		if (dndao.get(new DeveloperNode(edge.getLeft(), null, null, null)) == null
				|| dndao.get(new DeveloperNode(edge.getRight(), null, null,
						null)) == null) {
			return false;
		}
		ConflictBasedNetworkDAO cndao = (ConflictBasedNetworkDAO) DAOFactory
				.getDAO(CotonetBean.CONFLICT_NETWORK);
		if (((cndao.get(new ConflictBasedNetwork(edge.getNetworkID(), null,
				NetworkType.CHUNK_BASED)) != null) && (cndao
				.get(new ConflictBasedNetwork(edge.getNetworkID(), null,
						NetworkType.FILE_BASED)) == null))
				|| ((cndao.get(new ConflictBasedNetwork(edge.getNetworkID(),
						null, NetworkType.CHUNK_BASED)) == null) && (cndao
						.get(new ConflictBasedNetwork(edge.getNetworkID(),
								null, NetworkType.FILE_BASED)) != null))) {
			return false;
		}

		return true;
	}

}
