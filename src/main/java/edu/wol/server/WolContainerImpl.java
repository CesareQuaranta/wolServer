package edu.wol.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.wol.dom.Phenomen;
import edu.wol.dom.Prospective;
import edu.wol.dom.WolEntity;
import edu.wol.dom.Window;
import edu.wol.dom.WolContainer;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.iEvent;
import edu.wol.dom.server.BackgroundChange;
import edu.wol.dom.space.IntVector;
import edu.wol.dom.space.Movement;
import edu.wol.dom.space.Position;
import edu.wol.dom.space.Vector;
import edu.wol.dom.space.Space;
import edu.wol.server.repository.WolRepository;

public class WolContainerImpl<T extends WorldContainer<E,Position>,E extends WolEntity> implements WolContainer<T,E> {
	final static Logger logger = LoggerFactory.getLogger(WolContainerImpl.class);
	volatile boolean shutdown = false;
	private String nodeID;
	private Class<T> wolClass;
	private float spacePrecision;
	private float timePrecision;
	private Collection<T> wolInstances;
	private boolean running=false;
	
	@Autowired(required=false)
	private WolRepository<T,E> repository;
	
	public WolContainerImpl(Class<T> wolClass,String nodeID,float spacePrecision, float timePrecision) {
		this.nodeID=nodeID;
		this.wolClass=wolClass;
		this.spacePrecision=spacePrecision;
		this.timePrecision=timePrecision;
	}
	
	public void init() throws Exception{
		if(repository!=null){
			wolInstances=repository.loadInstances(this.nodeID);
		}else{
			wolInstances = new ArrayList<T>();
		}
		if(wolInstances.isEmpty()){
			internalGenerteWol();
		}
	}

	@Override
	public void run() {
		while(!shutdown){
			boolean empty=wolInstances.isEmpty() || (wolInstances.size()==1 && wolInstances.iterator().next().isEmpty());
			if(!empty){
				for(int i=0;i<100;i++){
					for(WorldContainer<E,Position> curInstance:wolInstances){
						shutdown = Thread.currentThread().isInterrupted();
						if(!shutdown && !curInstance.isEmpty()){
							curInstance.run();
						}
					}
				}
				if(repository!=null && !shutdown){
					repository.flush();//Da valutare se farlo ogni tot o in maniera cachata
					//repository.update(wolInstances);
				}
			}else{
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					System.out.println("Gently shutdown while sleeping");
				}
			}
		}
	}
	
	public Prospective associateNewWolProspective() throws IOException, Exception{
		T newIstance=internalGenerteWol();
		Prospective p=new Prospective("SolarSystem-"+newIstance.getID());//TODO Prospective factory
		p.getPosition().setZ(5);
		return p;
	}

	public boolean isRunning() {
		return running;
	}

	public void stopRunning() {
		this.running = false;
	}
	
	@Override
	public Collection<Phenomen<E>> getAllPhenomen(long wolID)
			throws IOException, Exception {
		T wol=findWol(wolID);
		if(wol!=null){
			Collection<E> ce= wol.getSpace().getAllEntities();
			Collection<Phenomen<E>> phenomens=new ArrayList<Phenomen<E>>(ce.size());
			for(E e : ce){
					Position p=wol.getSpace().getPosition(e);
					Phenomen<E> ph=new Phenomen<E>();
					ph.setEntity(e);
					ph.setPosition(p);
					phenomens.add(ph);
				}
			return phenomens;
			}
		return null;
	}

	@Override
	public void insertEntity(E entity,long wolID,Position position) throws IOException, Exception {
		T wol=findWol(wolID);
		if(wol!=null){
			repository.insert(entity);
			wol.insertEntity(position, entity);
		}	
	}
	private T internalGenerteWol() throws IOException, Exception{
		T newEmptyInstance= wolClass.newInstance();
		newEmptyInstance.init(spacePrecision,timePrecision);
		if(repository!=null){
			repository.insert(newEmptyInstance);
		}
		wolInstances.add(newEmptyInstance);
		return newEmptyInstance;
	}
	private T findWol(long wolID){
		for(T curInstance: wolInstances){
			if(curInstance.getID()==wolID){
				return curInstance;
			}
		}
		return null;
	}
}
