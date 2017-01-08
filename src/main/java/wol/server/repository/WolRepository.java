package wol.server.repository;

import java.io.IOException;
import java.util.Collection;

import wol.dom.Entity;
import wol.dom.WorldContainer;
import wol.dom.space.Position;

public interface WolRepository<T extends WorldContainer<E,Position>,E extends Entity> {
	public Collection<WorldContainer<E,Position>> loadInstances();
	public void registry(WorldContainer<E,Position> newInstance) throws Exception, IOException;
	public void serialize(Collection<WorldContainer<E,Position>> instances);
}
