package HiSeSitor;

import java.awt.geom.Arc2D.Double;
import java.util.ArrayList;

import cern.jet.random.Exponential;

public class Estrategia_espiral_testv3 extends Estrategia {
	public int periodo;
	public ArrayList<Nodo> visitados = new ArrayList<>();
	private int espiral = -1;
	private int espiralAux = 0;
	private int pasoPasado = 0;
	private int div = 0;
	private int presas = 0;
	private int itPasado = 0;

	protected int tam = 1;

	public Estrategia_espiral_testv3(ArrayList<Sensor> sen, ArrayList<Integer> v) {
		super(sen, v);
		nombre = "Espiral v3";
		// TODO Auto-generated constructor stub
	}

	@Override
	public void asignaVariables(ArrayList<Integer> v) throws Exception {
		if (v.size() != tam) {
			new Exception("Variables no correwspondientes con la estrategua");
		}
		espiral = v.get(0);
		div = v.get(1);

	}

	@Override
	public void update() {
		int i = espiral;
		int j = 2;
		int paso = pasoPasado;
		pasoPasado=(pasoPasado+1)%4;
		int it = itPasado;
		int cont = 0;
		itPasado++;
		Nodo actual = estado.getActual();
		visitados.add(actual);

		int x = (int) actual.pos.x;
		int y = (int) actual.pos.y;
		espiralAux = espiral;

		int MAX = memoria.getListaNodos().size();
		
		if (presas != estado.presas) {

			presas = estado.presas;
			paso = 0;
			pasoPasado = 1;
			it = 0;
			itPasado = 1;
		}

		while (espiralAux > 0 && cont < MAX) {
			Nodo bus;
			if (j-- == 0) {
				j = 2;
				it++;
			}
			switch (paso) {

			// arriba
			case 0:
				y -= it;
				break;
			// derehca
			case 1:
				x += it;
				break;
			// abajo
			case 2:
				x -= it;
				break;
			// izquierda
			default:
				y += it;
				break;
			}
			if ((bus = memoria.getNodo(x, y)) != null) {
				paso=(paso+1)%4;
				calculaEstima(bus);
				espiralAux--;
			}
			cont++;
		}

	}

	@Override
	public double estima(Nodo n) {
		double gan = 0;
		if ((gan = dameGananciaMedia(n)) < 0) {
			gan = Math.abs(gan);
			n.score = espiralAux * div / gan;
		} else {
			gan = Math.abs(gan);
			n.score = espiralAux * gan / div;
		}
		return n.score;
	}

	public double dameGananciaMedia(Nodo n) {
		int ganancia = 0;
		int num = 0;
		for (Nodo ady : memoria.getAdjacents(n)) {
			if (!ady.isEstimacion()) {
				ganancia += ady.ganancia;
				num++;
			}
		}
		if (num != 0) {
			return ganancia / num;
		}
		return 0;

	}

	@Override
	public void reset_ext() {
		visitados = new ArrayList<>();
		espiral = -1;
		espiralAux = 0;
		pasoPasado = 0;
		div = 0;
		presas = 0;
		itPasado = 0;
	};

	@Override
	public double calcula(Nodo n) {
		if (estado.getActual() == n)
			n.score = -10;
		n.score = espiralAux;

		return n.score;
	}
}