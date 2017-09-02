package edu.wol.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.wol.dom.Phenomen;
import edu.wol.dom.Prospective;
import edu.wol.dom.WolContainer;
import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.phisycs.MassEntity;
import edu.wol.dom.phisycs.Velocity;
import edu.wol.dom.space.Movement;
import edu.wol.dom.space.Position;
import edu.wol.dom.space.Rotation;
import edu.wol.dom.space.Vector3f;
import edu.wol.server.repository.WolRepository;
import edu.wol.space.Inertial;
//@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Exception.class)
public class WolContainerImpl<T extends WorldContainer<E,Position,?,?>,E extends MassEntity> implements WolContainer<T,E> {
	final static Logger logger = LoggerFactory.getLogger(WolContainerImpl.class);
	volatile boolean shutdown = false;
	private String nodeID;
	private Class<T> wolClass;
	private float spacePrecision;
	private float timePrecision;
	private Map<String,T> wolInstances;
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
		wolInstances = new HashMap<String,T>();
		if(repository!=null){
			Collection<T> instances = repository.loadInstances(this.nodeID);
			for(T instance:instances){
				wolInstances.put(instance.getWolID(), instance);
			}
		}
		if(wolInstances.isEmpty()){
			internalGenerteWol();
		}
	}

	@Override
	public void run() {
		long refreshTimestamp=System.currentTimeMillis();
		while(!shutdown){
			boolean empty=wolInstances.isEmpty() || (wolInstances.size()==1 && wolInstances.values().iterator().next().isEmpty());
			if(!empty){
				for(WorldContainer<E,Position,?,?> curInstance:wolInstances.values()){
					shutdown = Thread.currentThread().isInterrupted();
					if(!shutdown && !curInstance.isEmpty()){
						curInstance.run();//TODO run in separate threads
					}
				}
				if(repository!=null && !shutdown && (System.currentTimeMillis()-refreshTimestamp)>30000){
					//repository.flush();//Da valutare se farlo ogni tot o in maniera cachata
					//repository.update(wolInstances);
					refreshTimestamp=System.currentTimeMillis();
				}
				
			}else{//Waiting something appens...
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					System.out.println("Gently shutdown while sleeping");
				}
			}
		}
	}
	
	public Prospective associateNewWolProspective() throws IOException, Exception{
		T newIstance=internalGenerteWol();
		Prospective p=new Prospective(newIstance.getWolID());//TODO Prospective factory
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
	public Collection<Phenomen<E>> getAllPhenomen(String wolID) throws IOException, Exception {
		T wol=wolInstances.get(wolID);
		if(wol!=null){
			Collection<E> ce= wol.getSpace().getAllEntities();
			Collection<Phenomen<E>> phenomens=new ArrayList<Phenomen<E>>(ce.size());
			for(E e : ce){
				    Vector3f p = wol.getSpace().getPosition(e);
					Phenomen<E> ph=new Phenomen<E>();
					ph.setEntity(e);
					ph.setPosition(p);
					//Velocity v=wol.getPhisycs().getVelocity(e);
					Velocity v=((Inertial)wol.getSpace()).getVelocity(e);
					Rotation<E> av=wol.getPhisycs().getAngularVelocity(e);
					if(v!=null && !v.isEmpty() && v.getTime()<60000){//Return only for fast movement <1min otherwise movement will raise a push event
						//Convert velocity to movement
						Vector3f vv=v.getVector().clone();
						//vv.scale(v.getTime()/60000);
						Movement<E> m=new Movement<E>(e,vv);
						ph.addEffect(m);
					}
					if(av!=null && !av.isEmpty() && av.getTime()<60000){//Return only for fast rotation  <1min otherwise rotation will raise a push event
						ph.addEffect(av);
					}
					
					phenomens.add(ph);
				}
			return phenomens;
			}
		return null;
	}

	@Override
	public void insertEntity(E entity,String wolID,Position position) throws IOException, Exception {
		T wol=this.wolInstances.get(wolID);
		if(wol!=null){
			repository.insert(entity);
			repository.insert(position);
			wol.insertEntity(position, entity);
			repository.update(wol);
		}	
	}
	
	@Override
	public void castAwayEntities(String wolID, Position position, long radius) {
		T wol=wolInstances.get(wolID);
		if(wol!=null){
			wol.getPhisycs().castAwayEntities(position, radius);
		}
	}
	private T internalGenerteWol() throws IOException, Exception{
		T newEmptyInstance= wolClass.newInstance();
		newEmptyInstance.setNodeID(this.nodeID);
		newEmptyInstance.init(spacePrecision,timePrecision);
		if(repository!=null){
			repository.insert(newEmptyInstance);
			newEmptyInstance.setWolID("wol-"+newEmptyInstance.getID());
			repository.update(newEmptyInstance);
		}
		
		wolInstances.put(newEmptyInstance.getWolID(), newEmptyInstance);
		return newEmptyInstance;
	}
	
}
