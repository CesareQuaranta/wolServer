package edu.wol.server.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.space.Position;
import edu.wol.dom.space.Planetoid;
import edu.wol.starsystem.SolarSystem;
import edu.wol.starsystem.StarDial;

@Repository
@Transactional
public class StarsRepository implements WolRepository<SolarSystem,Planetoid> {
	@PersistenceContext
    private EntityManager manager;
	
	

	@Override
	public Collection<SolarSystem> loadInstances() {
		List<SolarSystem> instances = manager.createQuery("SELECT a FROM SolarSystem a", SolarSystem.class).setMaxResults(10).getResultList();
		return instances;
	}

	@Override
	public void insert(SolarSystem newInstance) throws Exception, IOException {
		manager.persist(newInstance);

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

}
