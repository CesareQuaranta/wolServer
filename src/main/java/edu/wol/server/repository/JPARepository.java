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
import edu.wol.starsystem.StarDial;

@Repository
@Transactional
public class JPARepository<T extends WorldContainer<E,Position>,E extends WolEntity> implements WolRepository<T,E> {
	@PersistenceContext
    private EntityManager manager;
	private Class<T> wolClass;
	
	public JPARepository(Class<T> wolClass) {
		this.wolClass=wolClass;
	}

	@Override
	public Collection<T> loadInstances() {
		List<T> instances = manager.createQuery("Select a From Entity a", this.wolClass).getResultList();
		return instances;
	}

	@Override
	public void registry(T newInstance) throws Exception, IOException {
		manager.persist(newInstance);

	}
	
	@Override
	public void serialize(Collection<T> instances) {
		for(T instance:instances){
			manager.persist(instance);
		}
	}

	@Override
	public void remove(T instance) {
		manager.remove(instance);
	}

}
