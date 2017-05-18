package edu.wol.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

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


public class WolContainerImpl<T extends WorldContainer<E,Position>,E extends WolEntity> extends WolContainer {
	volatile boolean shutdown = false;
	private Class<T> wolClass;
	private float spacePrecision;
	private float timePrecision;
	private Collection<T> wolInstances;
	private boolean running=false;
	private Collection<Window> openWindows;
	private Map<String,List<iEvent>> eventsWindow;
	
	@Autowired(required=false)
	private WolRepository<T,E> repository;//TODO 
	
	public WolContainerImpl(Class<T> wolClass,float spacePrecision, float timePrecision) {
		this.wolClass=wolClass;
		this.spacePrecision=spacePrecision;
		this.timePrecision=timePrecision;
		openWindows=new LinkedList<Window>();
		eventsWindow=new HashMap<String,List<iEvent>>();
	}
	
	public void init() throws Exception{
		if(repository!=null){
			wolInstances=repository.loadInstances();
		}else{
			wolInstances = new ArrayList<T>();
		}
		if(wolInstances.isEmpty()){
			T newEmptyInstance= wolClass.newInstance();
			newEmptyInstance.init(spacePrecision,timePrecision);
			if(repository!=null){
				repository.insert(newEmptyInstance);
			}
			wolInstances.add(newEmptyInstance);
		}
	}

	@Override
	public void run() {
		while(!shutdown){
		for(int i=0;i<100;i++){
			for(WorldContainer<E,Position> curInstance:wolInstances){
				shutdown = Thread.currentThread().isInterrupted();
				if(!shutdown){
					curInstance.run();
				}
			}
		}
		if(repository!=null && !shutdown){
			repository.update(wolInstances);//Da valutare se farlo ogni tot o in maniera cachata
		}
		}
	}

	public Window openWindow(Position pos){
		Window newWindow=new Window(pos,new IntVector(10,10,10));
		initWindow(newWindow);
		return newWindow;
	}
	
	public List<iEvent> getEvents(String windowIdentifier){
		return eventsWindow.get(windowIdentifier);
	}
	public boolean isRunning() {
		return running;
	}

	public void stopRunning() {
		this.running = false;
	}
	
	private void initWindow(Window window){
		openWindows.add(window);
		List<iEvent> events=new ArrayList<iEvent>();
		eventsWindow.put(window.getUID(), events);
		try {
			events.add(new BackgroundChange(new URL("http://localhost:8081/shapeRenderer/image/B"+window.getUID())));
			WorldContainer<E, Position> wc=wolInstances.iterator().next();
			Space<E,Position> space= wc.getSpace();
			for(int x=0;x<window.getDimensions().getX();x++){
				for(int y=0;y<window.getDimensions().getY();y++){
					for(int z=0;z<window.getDimensions().getZ();z++){
						E curEntity=space.getEntity(new Position(x,y,z));
						if(curEntity!=null){
							Movement<E> startPositionEvent=new Movement<E>(curEntity,new Vector());
							events.add(startPositionEvent);
						}
					}
				}
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
