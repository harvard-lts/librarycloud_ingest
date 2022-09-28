package edu.harvard.libcomm.dao;

//make more sophisticated later
import java.util.*;
import java.util.Map.Entry;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;

import edu.harvard.libcomm.model.*;

@Component
public class IngestDAO {
	@PersistenceContext
	private EntityManager em;

    Logger log = Logger.getLogger(IngestDAO.class); 

	public IngestDAO(){}

	@Transactional
	public Set<String> checkAndSaveItemChecksum(Map<String, Integer> items)
	{
		Map<String, Integer> sameMap = new HashMap<String, Integer>();
		for(Entry<String, Integer> entry : items.entrySet()){
			String query = "SELECT c from Checksum c where c.itemId = :itemId";
			Checksum checksum = null;
			
			try {
			checksum = em.createQuery(query, Checksum.class)
					.setParameter("itemId",  entry.getKey())
					.getSingleResult();
			} catch(NoResultException e)
			{
				//expected in most cases.
			}
			if(checksum != null && checksum.getChecksum().equals(entry.getValue())){
				sameMap.put(entry.getKey(), entry.getValue());
			} else
			{
				saveItemChecksum(entry.getKey(), entry.getValue(), checksum == null);
			}
		}
		return sameMap.keySet();
		
	}
	
	@Transactional
	public void saveItemChecksum(String itemId, Integer checksum, boolean newEntry){
		if (newEntry){
			insertItemChecksum(itemId, checksum);
		} else {
			String query;
			
			query = "UPDATE Checksum c SET c.itemId = :itemId, c.checksum = :checksum WHERE c.itemId = :itemId";
			em.createQuery(query).setParameter("itemId", itemId)
			.setParameter("checksum", checksum)
			.executeUpdate();

		}
	}
	
	@Transactional
	public void insertItemChecksum(String itemId, Integer checksum){
		Checksum entry = new Checksum();
		entry.setItemId(itemId);
		entry.setChecksum(checksum);
		em.persist(entry);
	}
	

}