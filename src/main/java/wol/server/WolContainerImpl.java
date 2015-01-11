package wol.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import wol.WolContainer;
import wol.dom.Entity;
import wol.dom.Window;
import wol.dom.WorldContainer;
import wol.dom.iEvent;
import wol.dom.space.IntVector;
import wol.dom.space.Movement;
import wol.dom.space.Position;
import wol.dom.space.Vector;
import wol.dom.space.iSpace;
import wol.server.dom.BackgroundChange;
import wol.server.repository.KryoRepository;


public class WolContainerImpl<T extends WorldContainer<E,Position>,E extends Entity> extends WolContainer<T,E> {
	private Class<T> wolClass;
	private float spacePrecision;
	private float timePrecision;
	private Collection<WorldContainer<E,Position>> wolInstances;
	private boolean running=false;
	private Collection<Window> openWindows;
	private Map<String,List<iEvent>> eventsWindow;
	
	@Autowired
	private KryoRepository<T,E> repository;//TODO JavaSpace
	
	public WolContainerImpl(Class<T> wolClass,float spacePrecision, float timePrecision) {
		this.wolClass=wolClass;
		this.spacePrecision=spacePrecision;
		this.timePrecision=timePrecision;
		openWindows=new LinkedList<Window>();
		eventsWindow=new HashMap<String,List<iEvent>>();
	}
	
	public void init() throws Exception{

		wolInstances=repository.loadInstances();
		if(wolInstances.isEmpty()){
			WorldContainer<E,Position> newEmptyInstance= wolClass.newInstance();
			newEmptyInstance.init(spacePrecision,timePrecision);
			repository.registry(newEmptyInstance);
			wolInstances.add(newEmptyInstance);
		}
	}

	@Override
	public void run() {
		running=true;
		while(running){
		for(int i=0;i<100;i++){
			for(WorldContainer<E,Position> curInstance:wolInstances){
				curInstance.run();
			}
		}
		repository.serialize(wolInstances);//Da valutare se farlo ogni tot o in maniera cachata
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
			events.add(new BackgroundChange(new URL("shapeRenderer/image/B"+window.getUID())));
			WorldContainer<E, Position> wc=wolInstances.iterator().next();
			iSpace<E,Position> space= wc.getSpace();
			for(int x=0;x<window.getDimensions().getX();x++){
				for(int y=0;x<window.getDimensions().getY();y++){
					for(int z=0;z<window.getDimensions().getZ();z++){
						E curEntity=space.getEntity(new Position(x,y,z));
						if(curEntity!=null){
							Movement<E> startPositionEvent=new Movement(curEntity,new Vector());
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
