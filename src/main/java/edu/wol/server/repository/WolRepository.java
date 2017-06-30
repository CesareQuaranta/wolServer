package edu.wol.server.repository;

import java.io.IOException;
import java.util.Collection;

import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.space.Position;

public interface WolRepository<T extends WorldContainer<E,Position>,E extends WolEntity> {
	public Collection<T> loadInstances();
	public T loadInstance(long id);
	public void insert(T newInstance) throws Exception, IOException;
	public void insert(E newInstance) throws Exception, IOException;
	public void update(Collection<T> instances);
	public void remove(T instance);
	void flush();
}
