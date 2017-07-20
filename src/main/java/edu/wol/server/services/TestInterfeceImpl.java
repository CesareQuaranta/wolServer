package edu.wol.server.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.wol.dom.services.TestInterface;
import edu.wol.dom.shape.AsteroidShape;
import edu.wol.dom.shape.AsteroidShapeFactory;
@Component
public class TestInterfeceImpl implements TestInterface {
	final static Logger logger = LoggerFactory.getLogger(TestInterfeceImpl.class);

	@Override
	public AsteroidShape generateAsteroidShape() throws IOException, Exception {
		return AsteroidShapeFactory.getInstance().generateShape();
	}


	


}
