package wol.server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import wol.dom.Entity;
import wol.dom.WorldContainer;
import wol.dom.space.Position;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoRepository<T extends WorldContainer<E,Position>,E extends Entity> {
    final static Logger logger = LoggerFactory.getLogger(KryoRepository.class);
	private Class<T> wolClass;
	private File basePath;
	private Kryo kryo;
	
	public KryoRepository(File path,Class<T> wolClass) {
		this.basePath=path;
		this.wolClass=wolClass;
		kryo = new Kryo();
	}
	
	public Collection<WorldContainer<E,Position>> loadInstances(){
		Collection<WorldContainer<E,Position>> instances=new ArrayList<WorldContainer<E,Position>>();
		if(basePath != null){
			for(File curFile:basePath.listFiles()){
				try {
					if(curFile.isFile() && curFile.canRead()){
						Input input = new Input(new FileInputStream(curFile));
						WorldContainer<E,Position> curInstrance=kryo.readObject(input,wolClass);
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
	
	public void registry(WorldContainer<E,Position> newInstance) throws Exception, IOException{
		File newFile=new File(basePath,"wol_"+newInstance.hashCode()+".kryo");
		if(newFile.createNewFile()){
			internalSerialize(newInstance,newFile);
		}
		
	}
	
	public void serialize(Collection<WorldContainer<E,Position>> instances){
		
	}
	
	private void internalSerialize(WorldContainer<E,Position> newInstance,File file) throws FileNotFoundException{
		Output output = new Output(new FileOutputStream(file));
		kryo.writeObject(output,newInstance);
		output.close();
	}

}
