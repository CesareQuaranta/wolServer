package edu.wol.server.services;

import java.util.Collections;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.wol.dom.Prospective;
import edu.wol.dom.User;
import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.commands.Command;
import edu.wol.dom.commands.GravityPower;
import edu.wol.dom.services.UserEventListener;
import edu.wol.dom.services.UserInterface;
import edu.wol.dom.shape.AsteroidShape;
import edu.wol.dom.shape.AsteroidShapeFactory;
import edu.wol.dom.space.Asteroid;
import edu.wol.dom.space.Planetoid;
import edu.wol.dom.space.Position;
import edu.wol.physics.starsystem.SolarSystemPhisycs;
import edu.wol.server.repository.UserRepository;
import edu.wol.server.repository.WolRepository;
import edu.wol.starsystem.SolarSystem;
@Component
@Transactional(propagation=Propagation.REQUIRED, readOnly=false, noRollbackFor=Exception.class)
public class UserInterfeceImpl implements UserInterface<SolarSystem> {
	final static Logger logger = LoggerFactory.getLogger(UserInterfeceImpl.class);
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WolRepository<SolarSystem,Planetoid> wolRepo;
	@Override
	
	public User loadUser(String username) {
		if(userRepo!=null){
			User user = userRepo.find(username);
			if(user!=null){
				return user;
			}else{//New User
				SolarSystem wol=wolRepo.loadInstances().iterator().next();
				String wolID=null;
				if(wol!=null){
					wolID="SolarSystem-"+wol.getID();
				}
				Prospective p=new Prospective(wolID);//TODO Prospective factory
				p.getPosition().setZ(5);
				user = new User(username,p);
				try {
					userRepo.insert(user);
					return user;
				} catch (Exception e) {
					logger.error("Errer insert new User "+username, e);
				} 
			}
		}
		return null;
	}

	@Override
	public void moveUser(User user, Position pos) {
		logger.debug("Move User unimplemented");
	}

	@Override
	public void rotateUser(User user, Position newHorizon) {
		logger.debug("Rotate User unimplemented");
	}

	@Override
	public void executeUserCommand(User user, Command com) {
		if(com instanceof GravityPower){
			GravityPower gp=(GravityPower)com;
			String wolID= user.getProspective().getWolID();
			if(wolID!=null){
				SolarSystem wol= wolRepo.loadInstance(Long.parseLong(wolID.split("-")[1]));
				if(wol!=null){
					Asteroid a = new Asteroid(Collections.singletonList("h2"),gp.getMagnitudo(),gp.getMagnitudo());
					a.setShape(AsteroidShapeFactory.getInstance().generateShape());
					wol.insertEntity((Position) gp.getPosition(), a);
				}
			}
		}else{
			logger.debug("Unsupported command "+com.toString());
		}
	}

	@Override
	public void addUserListner(User user, UserEventListener listener) {
		logger.debug("addUserListner unimplemented");
	}

	@Override
	public void removeUserListner(User user, UserEventListener listener) {
		logger.debug("removeUserListner unimplemented");
	}

	@Override
	public SolarSystem loadWol(long ID) {
		return wolRepo.loadInstance(ID);
	}

}
