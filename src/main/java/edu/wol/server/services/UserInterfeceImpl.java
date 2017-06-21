package edu.wol.server.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.wol.dom.Prospective;
import edu.wol.dom.User;
import edu.wol.dom.commands.Command;
import edu.wol.dom.services.UserEventListener;
import edu.wol.dom.services.UserInterface;
import edu.wol.dom.space.Position;
import edu.wol.server.repository.UserRepository;
import edu.wol.server.repository.WolRepository;
@Component
public class UserInterfeceImpl implements UserInterface {
	final static Logger logger = LoggerFactory.getLogger(UserInterfeceImpl.class);
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WolRepository<?,?> wolRepo;
	@Override
	
	public User loadUser(String username) {
		if(userRepo!=null){
			User user = userRepo.find(username);
			if(user!=null){
				return user;
			}else{//New User
				Prospective p=new Prospective(null);//TODO Prospective factory
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
		logger.debug("executeUserCommand unimplemented");
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
