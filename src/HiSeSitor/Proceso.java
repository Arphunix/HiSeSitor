package HiSeSitor;

import gestionDatos.Datos;
import gestionDatos.DatosIteracion;

import java.util.ArrayList;

public class Proceso {

	public static boolean enableGUI;
	public int SALTO;
	public ArrayList<Sensor> sensores = new ArrayList<Sensor>();
	public Simulacion simulacion;
	public static Datos dato = new Datos();
	public DatosIteracion mejorCapturados = new DatosIteracion("masCapturados");
	public DatosIteracion mejorOptimizado = new DatosIteracion("masOptimizados");
	public DatosIteracion masVisibles = new DatosIteracion("masOptimizados");
	private int incMax = 50;
	private int fraccion = -10/11;

	public int flag = 1;
	/**
	 * el salto lo podemos manejar desde aqui
	 * 
	 * @param s
	 * @param sim
	 */
	public Proceso(ArrayList<Sensor> s, Simulacion sim) {
		sensores = s;
		simulacion = sim;
		SALTO = 4;
		Logger.debug = true;
		enableGUI = false;
	}

	/**
	 * recibe un array de estrategias o por lo menos deberia indicar el numero
	 * de estrategias que se quieren y un array de arrays de enteros. el tamao
	 * del array grande debe ser igual que el numero de estrategias.
	 * 
	 * @param es
	 * @param v
	 */
	public void iteraEstrategias(ArrayList<Estrategia> es,
			ArrayList<ArrayList<Integer>> v) {

		for (int i = 0; i < es.size(); i++) {
			itera(es.get(i), v.get(i), dato);
//ESTO HAY QUE TOCARLO
			mejorCapturados = dato.mejorIteracionCapturados();
			mejorOptimizado = dato.mejorIteracionOptimizada();
			masVisibles = dato.mejorIteracionVisibles();
		}

	}

	
	
	public void preparaSimulacion(Estrategia estr, Simulacion s) {
		s.InitSimulacion();
		estr.reset();
	}
	
	
	/**
	 * itera lo voy explicando entre el codigo. recibe una estrategia y el array
	 * de variables de la misma
	 * 
	 * @param e
	 * @param vars
	 * @param d
	 */
	public int itera(Estrategia e, ArrayList<Integer> vars, Datos d) {
		return iteraAux(vars.size(), e, vars, d);
	}
	

	public int funcionDecisionParametros(int index, int inc, ArrayList<Integer> vars) {
		int p = vars.get(index);
		vars.remove(index);
		vars.add(index, p+inc);
		return p+inc;
	}
	
	public int iteraAux(int num, Estrategia e, ArrayList<Integer> vars, Datos d ) {
		int inc = incMax;
		int ret1;
		int ret2;
		int it=0;
		int top = 37;
		if (num > 0) {
			ret1 = iteraAux(num-1, e, vars, d);
			while (top--!=0) {
				funcionDecisionParametros(num, inc, vars);
				ret2 = iteraAux(num-1, e, vars, d);
				if (ret1 > ret2) {
					inc = fraccion*inc;
					it = 0;
				} else if (ret1 < ret2) {
					ret1 = ret2;
					it = 0;
				} else {
					it++;
				}
				
				if (it == 3) {
					return ret1;
				}
			}
		} else { 
			ret1 = simulacion.correSimulacion(e, d, this.toString(vars));
			while (top--!=0) {
				funcionDecisionParametros(num, inc, vars);
				ret2 = simulacion.correSimulacion(e, d, this.toString(vars));
				if (ret1 > ret2) {
					inc = fraccion*inc;
					it = 0;
				} else if (ret1 < ret2) {
					ret1 = ret2;
					it = 0;
				} else {
					it++;
				}
				
				if (it == 3) {
					return ret1;
				}
			}
		}
		return ret1;
	}

	public String toString(ArrayList<Integer> array) {

		String cadena = array.toString();
		cadena = cadena.replace(",", ".");
		return cadena;
	}
}
