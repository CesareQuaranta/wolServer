package edu.wol.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.wol.dom.Phenomen;
import edu.wol.dom.Prospective;
import edu.wol.dom.WolContainer;
import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.phisycs.Velocity;
import edu.wol.dom.space.Movement;
import edu.wol.dom.space.Position;
import edu.wol.dom.space.Rotation;
import edu.wol.dom.space.Vector3f;
import edu.wol.server.repository.WolRepository;
//@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Exception.class)
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
		long refreshTimestamp=System.currentTimeMillis();
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
				if(repository!=null && !shutdown && (System.currentTimeMillis()-refreshTimestamp)>30000){
					//repository.flush();//Da valutare se farlo ogni tot o in maniera cachata
					//repository.update(wolInstances);
					refreshTimestamp=System.currentTimeMillis();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("Gently shutdown while sleeping");
				}
			}else{
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
				    Vector3f p = wol.getSpace().getPosition(e);
					Phenomen<E> ph=new Phenomen<E>();
					ph.setEntity(e);
					ph.setPosition(p);
					Velocity v=wol.getPhisycs().getVelocity(e);
					Velocity av=wol.getPhisycs().getAngularVelocity(e);
					if(v!=null && !v.isEmpty() && v.getTime()<60000){//Return only for fast movement <1min otherwise movement will raise a push event
						//Convert velocity to movement
						Vector3f vv=v.getVector().clone();
						vv.scale(v.getTime()/60000);
						Movement<E> m=new Movement<E>(e,vv);
						ph.addEffect(m);
					}
					if(av!=null && !av.isEmpty() && av.getTime()<60000){//Return only for fast rotation  <1min otherwise rotation will raise a push event
						//Convert velocity to movement
						Vector3f vv=av.getVector().clone();
						float factorTime=(float)av.getTime()/60000;
						vv.scale(factorTime);
						Rotation<E> m=new Rotation<E>(e,vv);
						ph.addEffect(m);
					}
					
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
			repository.insert(position);
			wol.insertEntity(position, entity);
			repository.update(wol);
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
