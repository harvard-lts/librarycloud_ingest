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
package edu.harvard.libcomm.pipeline;

import java.util.Properties;

/**
*
* Config is the standard LTS class for accessing properties from a properties file,
* 
*
*/

public class Config {
	
	public String SOLR_URL;
	public String HOLDINGS_URL;
	public String SOLR_HOLDINGS_URL;
	public String SOLR_EXTENSIONS_URL;
	public String ITEM_URL;
	public String COLLECTIONS_URL;
	public String DRSEXTENSIONS_URL;
	public String DRS2_RAW_URL;
	public String MONGO_HOST;
	public String MONGO_PORT;
	public String MONGO_USERNAME;
	public String MONGO_PASSWORD;
	public String MONGO_DB;
	public String MARC_S3_URL;

	private static Config conf;
	
	public static String propFile = "librarycloud.env.properties";
	
	private Config() {
		
		
		Properties props = new Properties();
		
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream(Config.propFile));
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load project configuration!", e);
		} 
		
		SOLR_URL = props.getProperty("solr_url");
		SOLR_HOLDINGS_URL = props.getProperty("solr_holdings_url");
		SOLR_EXTENSIONS_URL = props.getProperty("solr_extensions_url");
		HOLDINGS_URL = props.getProperty("holdings_url");
		ITEM_URL = props.getProperty("item_url");
		COLLECTIONS_URL = props.getProperty("collections_url");
		DRSEXTENSIONS_URL = props.getProperty("drsextensions_url");
		DRS2_RAW_URL = props.getProperty("drs2_raw_url");
		MONGO_HOST = props.getProperty("mongo.host");
		MONGO_PORT = props.getProperty("mongo.port");
		MONGO_USERNAME = props.getProperty("mongo.username");
		MONGO_PASSWORD = props.getProperty("mongo.password");
		MONGO_DB = props.getProperty("mongo.db");
		MARC_S3_URL = props.getProperty("marc.s3.url");

	}
	
	public static synchronized Config getInstance() {
		if (conf == null)
			conf = new Config();
		
		return conf;
	}	
	
	
}


