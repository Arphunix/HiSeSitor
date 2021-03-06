package HiSeSitor;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author HiSeSiTor Co.
 *
 */
public abstract class Estrategia {
	public String nombre;
	public ArrayList<Sensor> sensores;
	public Estado estado;
	public Grafo memoria;
	public ArrayList<Integer> ponderaciones = new ArrayList<>();

	/**
	 * Creador de estrategia
	 * @param sensores
	 * @param vars
	 */
	public Estrategia(ArrayList<Sensor> sensores, ArrayList<Integer> vars) {

		// Comprueba que tiene los sensores necesarios
		if (checkSensores(sensores) == -1) {
			return;
		}
		this.sensores = sensores;
		// Asigna variables iterables (iteraciones)
		try {
			asignaVariables(vars);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int checkSensores(ArrayList<Sensor> s) {
		return 0;
	}

	public void asignaVariables(ArrayList<Integer> v) throws Exception {

	}
	/**
	 * Actualiza el conocimiento de los sensores
	 * @param time
	 * @param x
	 * @param y
	 * @param m
	 */
	public void updateSensores() {
		for (Sensor s : sensores) {
			s.updateKnowledge();
		}
	}
	/**
	 * Creador alternativo que recibe unicamente una lista de sensores.
	 * @param time
	 * @param x
	 * @param y
	 * @param m
	 */
	public Estrategia(ArrayList<Sensor> sen) {
		sensores = sen;
	}
	/**
	 * Crea un nodo estimado en la posicion x,y
	 * @param time
	 * @param x
	 * @param y
	 * @param m
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	/**
	 * Devuelve la lista de sensores de la estrategia
	 * @return
	 */
	public ArrayList<Sensor> getSensores() {
		return sensores;
	}

	public void reset() {
		for (Sensor s : getSensores()) {
			s.reset();
		}
		memoria = null;
		estado = null;
		reset_ext();
	}

	public void reset_ext() {
		// TODO:
	}

	public void init(ArrayList<Sensor> sensores) {
		// Depende de la estrategia inicializar unos u otros sensores
		this.sensores = sensores;
	}

	public void updateMemoria() {
		if (memoria == null) {
			memoria = new Grafo();
			memoria.InitSensorGraph(estado.mapa);
			//memoria.plotGraph("Memoria");
		}
		for (Sensor s : sensores) {
			agregaSensorMemoria(s);
		}
		generaEstimacion();
		update();
	}

	// Funcion a reimplementar
	public abstract double estima(Nodo n);

	// Funcion a reimplementar
	public abstract double calcula(Nodo n);

	public void generaEstimacion() {

		ArrayList<Nodo> lista = memoria.getListaNodos();
		int x, y;
		int xAux, yAux;
		int cont = 0;

		for (Nodo n : lista) {
			x = (int) n.pos.x;
			y = (int) n.pos.y;

			cont = 0;
			try {
				ArrayList<Integer> aristas = n.getListaAristas();
				for (int i : aristas) {

					switch (cont) {
					case 0:
						xAux = 0;
						yAux = -1;
						break;
					case 1:
						xAux = 1;
						yAux = -1;
						break;
					case 2:
						xAux = 1;
						yAux = 0;
						break;
					case 3:
						xAux = 1;
						yAux = 1;
						break;
					case 4:
						xAux = 0;
						yAux = 1;
						break;
					case 5:
						xAux = -1;
						yAux = 1;
						break;
					case 6:
						xAux = -1;
						yAux = 0;
						break;
					default:
						xAux = -1;
						yAux = -1;
						break;
					}
					cont++;
					if (i == 1 && memoria.getNodo(x + xAux, y + yAux) == null) {
						memoria.creaNodoEstimacion(estado.time, x + xAux, y
								+ yAux, estado.mapa); // habla con vitcot
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// dependera de cada estrategia
	public void update() {
		// Grafo memoria = estado.memoria;
		for (Nodo n : memoria.getListaNodos())
			calculaEstima(n);
	}

	public Nodo getObjetivo() {

		ArrayList<Nodo> nodos = estado.getAdyacentes(estado.getActual());
		int max = 0;
		Nodo dest = null;
		for (Nodo n : nodos) {
			if (max < memoria.getNode(n.id).score) {
				max = (int) memoria.getNode(n.id).score;
				dest = n;
			}
		}
		if (dest == null) {// No deberia hacerse esto, pero asi evitamos algun
			// que otro pete
			Random r = new Random();
			dest = nodos.get(r.nextInt(nodos.size()));
		}
		return dest;
	}

	public void agregaSensorMemoria(Sensor sensor) {
		memoria.union(sensor.getSensorGraph());
	}

	public double calculaEstima(Nodo n) {

		if (memoria.isEstimacion(n)) {
			return estima(n);
		}
		return calcula(n);
	}
}
