package edu.wol.server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.wol.dom.WolEntity;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.space.Position;

public class KryoRepository<T extends WorldContainer<E,Position>,E extends WolEntity> implements WolRepository<T,E> {
    final static Logger logger = LoggerFactory.getLogger(KryoRepository.class);
	private Class<T> wolClass;
	private File basePath;
	private Kryo kryo;
	
	public KryoRepository(File path,Class<T> wolClass) {
		this.basePath=path;
		this.wolClass=wolClass;
		kryo = new Kryo();
	}
	
	public Collection<T> loadInstances(String nodeID){
		Collection<T> instances=new ArrayList<T>();
		if(basePath != null){
			for(File curFile:basePath.listFiles()){
				try {
					if(curFile.isFile() && curFile.canRead()){
						Input input = new Input(new FileInputStream(curFile));
						T curInstrance=kryo.readObject(input,wolClass);
						instances.add(curInstrance);
					}
				} catch (Exception e) {
					logger.error("Kryo error for file:"+curFile.getName(), e);
				}
			}
		}else{
			logger.error("Invalid Kryo path");
		}
		return instances;
	}
	
	public void insert(T newInstance) throws Exception, IOException{
		File newFile=new File(basePath,"wol_"+newInstance.hashCode()+".kryo");
		if(newFile.createNewFile()){
			internalSerialize(newInstance,newFile);
		}
		
	}
	
	public void update(Collection<T> instances){
		
	}
	
	private void internalSerialize(WorldContainer<E,Position> newInstance,File file) throws FileNotFoundException{
		Output output = new Output(new FileOutputStream(file));
		kryo.writeObject(output,newInstance);
		output.close();
	}

	@Override
	public void remove(T instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T loadInstance(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(E newInstance) throws Exception, IOException {
		// TODO Auto-generated method stub
		
	}

}
