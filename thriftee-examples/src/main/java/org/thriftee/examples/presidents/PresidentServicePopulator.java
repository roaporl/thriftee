/*
 * Copyright 2004 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thriftee.examples.presidents;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Johnston
 */
@Startup
@Singleton
public class PresidentServicePopulator {
    
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
	private final Logger LOG = LoggerFactory.getLogger(getClass()); 
	
	@PersistenceContext
    private EntityManager em;
    
    private final URL presidents = getClass().getResource("presidents.csv");
    
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
    	
    	Long count = em.createQuery("select count(p) from President p", Long.class).getSingleResult();
    	if (count > 0) {
    		LOG.info("skipping PresidentService population; existing records found: " + count);
    		return;
    	}
    	
    	LOG.info("Loading default data into PresidentService");
    	
        int id = 0;

        InputStream input = null;
        
        try {
        	input = presidents.openStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        for (String line = null; (line = reader.readLine()) != null; ) {
	        	
	            id++;
	            line = StringUtils.substringAfter(line, "\"");
	            String[] data =  line.split("\",\"");
	
	            President president = new President();
	            president.setId(id);
	
	            Name name = new Name();
	            name.setFirstName(data[0]);
	            name.setLastName(data[1]);
	            name.setNickName(data[2]);
	            president.setName(name);
	            
	            president.setTerm(data[3]);
	            president.setBorn(simpleDateFormat.parse(data[4]));
	            
	            String died = data[5];
	            if (StringUtils.isNotEmpty(died) && !died.equals("null")) {
	                president.setDied(simpleDateFormat.parse(died));
	            }
	            
	            president.setEducation(data[6]);
	            president.setCareer(data[7]);
	            president.setPoliticalParty(data[8]);
	            
	            if (LOG.isDebugEnabled()) {
	            	LOG.debug("Inserting president record: " + president.getName().getFullName());
	            }
	            em.persist(president);
	        }
        } finally {
        	if (input != null) {
        		try {
        			input.close();
        		} catch (Exception e) {}
        	}
        }
    }
    
    /*
    public void createPresidentTable() {
        jdbcTemplate.execute("CREATE TABLE president ( " 
                + " id    INTEGER NOT NULL PRIMARY KEY, " 
                + " first_name      VARCHAR(50) NOT NULL, " 
                + " last_name       VARCHAR(50) NOT NULL, "
                + " nick_name       VARCHAR(50) NOT NULL, " 
                + " term            VARCHAR(50) NOT NULL, " 
                + " born            DATE NOT NULL," 
                + " died            DATE NULL, "
                + " education       VARCHAR(100) NULL, " 
                + " career          VARCHAR(100) NOT NULL, " 
                + " political_party VARCHAR(100) NOT NULL, " 
                + " selected VARCHAR(1) NULL " 
                + " )");
    }
    */

}