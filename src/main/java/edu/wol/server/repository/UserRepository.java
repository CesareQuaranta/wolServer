package edu.wol.server.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.wol.dom.User;
import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.space.Position;
import edu.wol.dom.space.Planetoid;
import edu.wol.starsystem.SolarSystem;
import edu.wol.starsystem.StarDial;

@Repository
@Transactional
public class UserRepository {
	@PersistenceContext
    private EntityManager manager;
	
	

	public User find(String username) {
		return manager.find(User.class, username);
	}

	public void insert(User newUser) throws Exception, IOException {
		manager.persist(newUser);
	}


	public void delete(User usern) {
		manager.remove(usern);
	}

}
