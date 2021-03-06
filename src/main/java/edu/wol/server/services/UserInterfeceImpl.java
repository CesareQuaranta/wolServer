package edu.wol.server.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.wol.dom.Phenomen;
import edu.wol.dom.Prospective;
import edu.wol.dom.User;
import edu.wol.dom.WolContainer;
import edu.wol.dom.commands.Command;
import edu.wol.dom.commands.GravityPower;
import edu.wol.dom.commands.Movement;
import edu.wol.dom.services.UserEventListener;
import edu.wol.dom.services.UserInterface;
import edu.wol.dom.shape.AsteroidShapeFactory;
import edu.wol.dom.space.Asteroid;
import edu.wol.dom.space.LiquidSphere;
import edu.wol.dom.space.Planetoid;
import edu.wol.dom.space.Position;
import edu.wol.server.repository.UserRepository;
import edu.wol.starsystem.SolarSystem;
@Component
public class UserInterfeceImpl implements UserInterface<SolarSystem,Planetoid> {
	final static Logger logger = LoggerFactory.getLogger(UserInterfeceImpl.class);
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WolContainer<SolarSystem,Planetoid> wolContainer;
	@Override
	
	public User loadUser(String username) throws IOException, Exception {
		if(userRepo!=null){
			User user = userRepo.find(username);
			if(user!=null){
				return user;
			}else{//New User
				Prospective newProspective=wolContainer.associateNewWolProspective();
				user = new User(username,newProspective);
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
	public void executeUserCommand(User user, Command com) throws IOException, Exception {
		if(com instanceof GravityPower){//TODO Da rivedere
			GravityPower gp=(GravityPower)com;
			String wolID= user.getProspective().getWolID();
			if(wolID!=null){
				LiquidSphere hydrogenBubble = new LiquidSphere(Arrays.asList(new String[]{"h2","he","lt"}),163000,8);
				/*Asteroid a = new Asteroid(Collections.singletonList("h2"),gp.getMagnitudo(),gp.getMagnitudo());
				a.setShape(AsteroidShapeFactory.getInstance().generateRandomHidrogenGemShape());
				wolContainer.insertEntity(a, WolID, (Position) gp.getPosition());*/
				wolContainer.insertEntity(hydrogenBubble, wolID, (Position) gp.getPosition());
				logger.debug("Test insert entity "+ hydrogenBubble.toString() + " in position "+((Position)gp.getPosition()).toString()+" OK");
			}
		}if(com instanceof Movement){
			Movement mov = (Movement) com;
			String wolID= user.getProspective().getWolID();
			if(wolID!=null){
				user.getProspective().setPosition(mov.getPosition());
				userRepo.update(user);
				wolContainer.castAwayEntities(wolID,mov.getPosition(),10);
			}
			
		}else{
			logger.debug("Unsupported command "+com.toString());
		}
	}
	
	@Override
	public Collection<Phenomen<Planetoid>> getAllPhenomen(String wolID) throws IOException, Exception {
		return wolContainer.getAllPhenomen(wolID);
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
