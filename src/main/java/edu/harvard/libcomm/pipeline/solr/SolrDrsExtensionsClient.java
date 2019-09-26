/**********************************************************************
 * Copyright (c) 2012 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Contact information
 *
 * Office for Information Systems
 * Harvard University Library
 * Harvard University
 * Cambridge, MA  02138
 * (617)495-3724
 * hulois@hulmail.harvard.edu
 **********************************************************************/
package edu.harvard.libcomm.pipeline.solr;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.IProcessor;
/**
*
*
* SolrDrsExtensionsClient is a singleton class for retrieving the solr connection
*
*/
public class SolrDrsExtensionsClient {
	private static HttpSolrClient client = null;

  public static HttpSolrClient getSolrConnection() {
		try {
        //			client = new HttpSolrClient.Builder(Config.getInstance().SOLR_EXTENSIONS_URL).build();
        client = new HttpSolrClient(Config.getInstance().SOLR_EXTENSIONS_URL);

		} catch (Exception e) {
			// TO DO - error handling
			System.out.println( e);
		}

		return client;
	}
}
