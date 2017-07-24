package edu.wol.server.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.wol.dom.services.TestInterface;
import edu.wol.dom.shape.AsteroidShape;
import edu.wol.dom.shape.AsteroidShapeFactory;
import edu.wol.dom.shape.PlaneShape;
import edu.wol.dom.shape.PlanetShapeFactory;
@Component
public class TestInterfeceImpl implements TestInterface {
	final static Logger logger = LoggerFactory.getLogger(TestInterfeceImpl.class);

	@Override
	public AsteroidShape generateHidrogenGemShape(float length1,float length2,float length3, float length4,double angoloX,double angolo3X,double angolo1Y,double angolo2Y,double angolo3Y,double angoloZ,float correctXZ,float correctY) throws IOException, Exception {
		return AsteroidShapeFactory.getInstance().generateHidrogenGemShape(length1,length2,length3,length3,angoloX,angolo3X,angolo1Y,angolo2Y,angolo3Y,angoloZ,correctXZ,correctY);
	}

	@Override
	public PlaneShape generateIsland(int lod, double roughness, double maxHeigth) {
		return PlanetShapeFactory.getInstance().generateIsland(lod, roughness, maxHeigth);
	}


	


}
