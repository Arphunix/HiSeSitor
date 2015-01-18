package HiSeSitor;

import java.awt.geom.Arc2D.Double;
import java.util.ArrayList;

import cern.jet.random.Exponential;

public class Estrategia_FarAway extends Estrategia {
	public int periodo;
	public ArrayList<Nodo> visitados = new ArrayList<>();
	private int div;
	private int presas = 0;
	private int itPasado = 0;
	private int jGlob = 2;
	

	protected int tam = 1;

	public Estrategia_FarAway(ArrayList<Sensor> sen, ArrayList<Integer> v) {
		super(sen, v);
		nombre = "FarAway";
		// TODO Auto-generated constructor stub
	}

	@Override
	public void asignaVariables(ArrayList<Integer> v) throws Exception {
		if (v.size() != tam) {
			new Exception("Variables no correwspondientes con la estrategua");
		}
		div = v.get(0);

	}

	

	@Override
	public double estima(Nodo n) {
		double gan = 0;
		if ((gan = dameGananciaMedia(n)) < 0) {
			gan = Math.abs(gan);
			n.score = calcula(n) * div / gan;
		} else {
			gan = Math.abs(gan);
			n.score = calcula(n) * gan / div;
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
		div = 0;
		presas = 0;
		itPasado = 0;
	};

	@Override
	public double calcula(Nodo n) {
		Nodo l = estado.mapa.getNode(n.id);
		int dist = 0;
		if(n.score<0){
			l.score = -10;
			return -10;
		}
//		if (estado.getActual().id == n.id){
//			n.score = -10;
//			l.score = -10;
//			return n.score;
//		}
		if(visitados.contains(n)){
			n.score = -5;
			l.score = -5;
			return n.score;
		}	
		visitados.add(estado.getActual());
		n.score = 9999;
		dist = this.memoria.getDistancia(n,estado.inicio);
		if(dist < n.score){
				n.score = dist;
				l.score = dist;
			}
		
		return n.score;
	}
}