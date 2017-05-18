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
import edu.wol.starsystem.StarDial;

@Repository
@Transactional
public class StarsRepository implements WolRepository<StarDial,Planetoid> {
	@PersistenceContext
    private EntityManager manager;
	
	

	@Override
	public Collection<StarDial> loadInstances() {
		List<StarDial> instances = manager.createQuery("SELECT a FROM StarDial a", StarDial.class).setMaxResults(10).getResultList();
		return instances;
	}

	@Override
	public void insert(StarDial newInstance) throws Exception, IOException {
		manager.persist(newInstance);

	}
	
	@Override
	public void update(Collection<StarDial> instances) {
		for(StarDial instance:instances){
			manager.merge(instance);
		}
	}

	@Override
	public void remove(StarDial instance) {
		manager.remove(instance);
	}

}
