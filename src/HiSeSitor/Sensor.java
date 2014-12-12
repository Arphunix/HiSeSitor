package HiSeSitor;

import java.util.ArrayList;
import java.util.Random;

public class Sensor {
	//Funciones DUMMIE
	public Grafo sensorKnoledge;
	public Grafo memoria;
	public Grafo mapa;

	public boolean isVisto(Nodo n) {
		return sensorKnoledge.contains(n);
	}

	public void updateKnoledge() {
		sensorKnoledge.union(getSensorGraph());

	}
	grafo getSensorGraph() {
		Grafo sensor = new Grafo();
		ArrayList<Nodo> list = getGraphList();
		Nodo actual = mapa.getActual();
		for (Nodo n : list) {
			if (checkNode(n, actual) == true) sensor.add(n);
		}

	}

	/**
	*Estra funcion es lo que diferenciará unos sensores de otros
	*/
	private boolean checkNode(Nodo check, Nodo actual) {
		//dummie
		if (getDist() > 1) {
			return false;
		}
		return true;
	}


}