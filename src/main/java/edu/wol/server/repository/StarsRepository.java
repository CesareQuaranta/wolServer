package edu.wol.server.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.wol.dom.space.Planetoid;
import edu.wol.dom.space.Position;
import edu.wol.starsystem.SolarSystem;

@Repository
@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Exception.class)
public class StarsRepository implements WolRepository<SolarSystem,Planetoid> {
	@PersistenceContext
    private EntityManager manager;
	
	

	@Override
	public Collection<SolarSystem> loadInstances(String nodeID) {
		 TypedQuery<SolarSystem> query = manager.createQuery("SELECT s FROM SolarSystem s WHERE s.nodeID = :nodeID", SolarSystem.class);
				query.setParameter("nodeID", nodeID);
		 		query.setMaxResults(100);//TODO settare max load
		return query.getResultList();
	}

	@Override
	public void insert(SolarSystem newInstance) throws Exception, IOException {
		manager.persist(newInstance);

	}
	@Override
	public void update(SolarSystem instance) {
		manager.merge(instance);
	}
	@Override
	public void update(Collection<SolarSystem> instances) {
		for(SolarSystem instance:instances){
			manager.merge(instance);
		}
	}

	@Override
	public void remove(SolarSystem instance) {
		manager.remove(instance);
	}
	
	@Override
	public void flush() {
			manager.flush();
	}

	@Override
	public SolarSystem loadInstance(long id) {
		SolarSystem instance= manager.find(SolarSystem.class, id);
		//TODO initialize?
		return instance;
	}

	@Override
	public void insert(Planetoid newInstance) throws Exception, IOException {
		manager.persist(newInstance);
	}
	@Override
	public void insert(Position newPosition) throws Exception, IOException {
		manager.persist(newPosition);
	}

}
