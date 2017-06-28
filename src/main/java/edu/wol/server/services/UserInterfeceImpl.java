package edu.wol.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import edu.wol.dom.space.Position;
import edu.wol.server.repository.UserRepository;
import edu.wol.server.repository.WolRepository;
@Component
public class UserInterfeceImpl implements UserInterface {
	final static Logger logger = LoggerFactory.getLogger(UserInterfeceImpl.class);
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WolRepository<WorldContainer<WolEntity,Position>,WolEntity> wolRepo;
	@Override
	
	public User loadUser(String username) {
		if(userRepo!=null){
			User user = userRepo.find(username);
			if(user!=null){
				return user;
			}else{//New User
				WorldContainer<WolEntity,Position> wol=wolRepo.loadInstances().iterator().next();
				Prospective p=new Prospective(null);//TODO Prospective factory
				p.getPosition().setZ(5);
				p.setWol(wol);
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
			WorldContainer<WolEntity,Position> wol = user.getProspective().getWol();
			if(wol!=null){
				Asteroid a = new Asteroid(gp.getMagnitudo(),gp.getMagnitudo());
				a.setShape(AsteroidShapeFactory.getInstance().generateShape());
				wol.insertEntity((Position) gp.getPosition(), a);
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

}
