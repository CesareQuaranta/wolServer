package edu.wol.server.repository;

import java.io.IOException;
import java.util.Collection;

import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.space.Position;
import edu.wol.dom.space.Vector3f;

public interface WolRepository<T extends WorldContainer<E,Position>,E extends WolEntity> {
	public Collection<T> loadInstances(String nodeID);
	public T loadInstance(long id);
	public void insert(T newInstance) throws Exception, IOException;
	public void insert(E newInstance) throws Exception, IOException;
	public void insert(Position newPosition) throws Exception, IOException;
	public void update(T instance);
	public void update(Collection<T> instances);
	public void remove(T instance);
	void flush();
}
