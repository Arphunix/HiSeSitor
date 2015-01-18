package HiSeSitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.PersistentLayout.Point;

/**
 * 
 * @author Victor
 * 
 *         Gestion de grafos.
 * 
 *         Incluye los metodos necesarios para crear y operar con grafos.
 */
public class Grafo {

	/* Nodos son enteros y las aristas tambien hasta nuevo aviso */
	SparseMultigraph<Nodo, Integer> g;
	/* Autoincremento para el id de las aristas */
	int edgecount;
	int x;
	int y;
	static HashMap<Integer, Integer> hm;
	static ArrayList<Nodos> nods;
	ArrayList<ArrayList<Nodo>> grafo;
	HashMap<Integer, Punto> nodtopos;
	HashMap<Punto, Integer> postonod;

	public Grafo() {
		g = new SparseMultigraph<>();
		nodtopos = new HashMap<>();
		postonod = new HashMap<>();
	}

	// COMPLETAR
	public void addNode(Nodo n, Grafo ref, int time) {
		ArrayList<Nodo> ady = ref.getAdjacents(n);
		g.addVertex(n);
		nodtopos.put(n.getId(), ref.nodtopos.get(n.getId()));
		postonod.put(ref.nodtopos.get(n.getId()), n.getId());
		for (Nodo ad : ady) {
			if (contains(ad) == true) {
				Integer i = ref.g.findEdge(n, ad);
				g.addEdge(i, new Nodo(n.id, n.score, n.getPos()), new Nodo(
						ad.id, ad.score, ad.getPos()));
			}
		}
	}

	/*
	 * Probablemente esta funcin carezca de sentido
	 */
	public void addNode(Nodo n, Grafo ref) {
		ArrayList<Nodo> ady = ref.getAdjacents(n);
		g.addVertex(n);
		nodtopos.put(n.getId(), ref.nodtopos.get(n.getId()));
		postonod.put(ref.nodtopos.get(n.getId()), n.getId());
		for (Nodo ad : ady) {
			if (contains(ad) == true) {
				Integer i = ref.g.findEdge(n, ad);
				try{
				g.addEdge(i, new Nodo(n.id, n.score, n.getPos()), new Nodo(
						ad.id, ad.score, ad.getPos()));
				}catch(Exception e){
					
				}
			}
		}
	}

	/**
	 * 
	 * @param aristas
	 *            Lista de conexiones del nodo
	 * @param n
	 *            Nodo a insertar
	 * @return True si se inserta correctamente. False en caso contrario
	 */
	/*
	 * public boolean addNode(List<Integer> aristas, Nodo n) { return true; }
	 */

	public List<Nodo> getShortestPath(Nodo n1, Nodo n2) {
		List<Nodo> lv = new ArrayList<>();
		int it = 1;
		ArrayList<Nodos> abiertos = new ArrayList<>();
		ArrayList<Nodo> tmp = new ArrayList<>();
		tmp.addAll(g.getNeighbors(n1));
		// System.out.println(tmp.toString());
		if (tmp.contains(n2)) {
			lv.add(n2);
			return lv;
		}
		tmp.addAll(g.getNeighbors(n1));
		for (int i = 0; i < tmp.size(); i++)
			abiertos.add(new Nodos(tmp.get(i), it, new ArrayList<Nodos>()));
		while (true) {
			if (abiertos.isEmpty()) {
				return lv;
			}
			if (abiertos.get(0).getId().equals(n2)) { /* Encontrado */
				Nodos n = abiertos.get(0);
				abiertos = abiertos.get(0).antecesores;
				abiertos.add(n);
				break;
			}
			tmp.clear();
			tmp.addAll(g.getNeighbors(abiertos.get(0).getId()));
			it++;
			for (int i = 0; i < tmp.size(); i++) {
				ArrayList<Nodos> l = new ArrayList<>();
				l.addAll(abiertos.get(0).antecesores);
				l.add(abiertos.get(0));
				abiertos.add(new Nodos(tmp.get(i), it, l));
			}

			abiertos.remove(0);
		}
		for (int i = 0; i < abiertos.size(); i++) {
			lv.add(abiertos.get(i).getId());
		}

		return lv;
	}

	public ArrayList<Nodo> getAdjacents(Nodo n) {
		ArrayList<Nodo> ln = new ArrayList<>();
		ln.addAll(g.getNeighbors(n));
		return ln;

	}

	public int getNodesCount() {
		return g.getVertexCount();
	}

	public boolean addEdge(int arista, Nodo n1, Nodo n2) {
		if (n1.equals(n2))
			return false;
		if (g.containsVertex(n1) && g.containsVertex(n2)
				&& g.getNeighbors(n1).contains(n2)) {
			return false;
		}
		return g.addEdge(arista, n1, n2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void plotGraph(String title) {
		StaticLayout<Nodo, Integer> layout = new StaticLayout<>(g);
		Transformer<Nodo, Paint> vertexColor = new Transformer<Nodo, Paint>() {
			public Paint transform(Nodo i) {
				if (i.obstaculo)
					return Color.BLACK;
				if (i.cazador)
					return Color.WHITE;
				if (i.init)
					return Color.PINK;
				else if (i.presa)
					return Color.RED;
				else if (i.cazada)
					return Color.GREEN;
				else if (i.isEstimacion())
					return Color.YELLOW;
				return Color.LIGHT_GRAY;
			}
		};
		Transformer<Nodo, Shape> vertexSize = new Transformer<Nodo, Shape>() {
			public Shape transform(Nodo i) {
				Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
				// in this case, the vertex is twice as large
				if (i.cazador)
					return AffineTransform.getScaleInstance(1, 1)
							.createTransformedShape(circle);
				else
					return circle;
			}
		};
		VisualizationViewer<Nodo, Integer> vv = new VisualizationViewer<Nodo, Integer>(
				layout, new Dimension(800, 600));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		// distance between the nodes
		int distX = 100;
		int distY = 100;
		int operatingNode = 0;
		// System.out.println(x + " y " + y);
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				layout.setLocation(
						new Nodo(operatingNode++, 0, new Point(j, i)), j
								* distX, i * distY);
			}
		}
		GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(vv);
		DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		vv.setGraphMouse(graphMouse);

		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(zoomPane);
		frame.pack();
		frame.setVisible(true);

	}

	public Nodo getNodo(int x, int y) {
		if(x < 1 || y < 1)
			return null;
		ArrayList<Nodo> al = new ArrayList<>();
		al.addAll(g.getVertices());
		int s = -1;
		try{
		s = postonod.get(new Punto(x, y));
		}catch (NullPointerException e){
			Logger.error("ERROR Grafo getNodo(int,int) - No se pudo acceder al punto "+x+" e "+y);
		}
		int posicion = al.indexOf(new Nodo(s, 0, new Point(x, y)));
		if (posicion == -1)
			return null;
		return al.get(posicion);
	}

	public ArrayList<Nodo> getListaNodos() {
		ArrayList<Nodo> aux = new ArrayList<>();
		aux.addAll(g.getVertices());
		return aux;
	}

	public void creaPresa(Nodo auxN) {
		ArrayList<Nodo> list = getListaNodos();
		for (Nodo n : list) {
			if (auxN.id == n.id) {
				if (n.obstaculo == true)
					continue;
				Logger.debug("INFO - Presa creada: " + n.toString());
				n.presa = true;
				return;
			}
		}

		System.out.println("ERROR - La presa no ha sido creada: "
				+ auxN.toString());

	}

	public Nodo getShortestPathNode(Nodo actual, Nodo objetivo) {
		List<Nodo> nl = getShortestPath(actual, objetivo);
		if (nl.size() == 1)
			return getShortestPath(actual, objetivo).get(0);
		else {
			return getShortestPath(actual, objetivo).get(1);

		}
	}

	public Nodo setCazador(Nodo nodo) {
		ArrayList<Nodo> aux = getListaNodos();
		for (Nodo n : aux) {
			if (n.cazador == true) {
				n.cazador = false;
				nodo.cazador = true;
				return nodo;
			}
		}
		nodo.cazador = true;
		// System.out.println("El cazador esta en: "+nodo.toString());
		return nodo;
	}

	public void setArbolDondeCuenta(Nodo n) {
		ArrayList<Nodo> aux = getListaNodos();
		for (Nodo nodo : aux) {
			if (nodo.equals(n)) {
				nodo.init = true;
				n.init = true;
			}
		}
	}

	public Nodo setCazador() {
		Random random = new Random();
		ArrayList<Nodo> list = getListaNodos();

		int tam = list.size();
		int rng = random.nextInt(tam);
		Nodo nodo = list.get(55);
		if (nodo.isObstaculo())
			nodo = setCazador();
		return setCazador(nodo);
	}

	public ArrayList<Nodo> getPresas() {
		ArrayList<Nodo> presasL = new ArrayList<>();
		ArrayList<Nodo> list = getListaNodos();
		for (Nodo n : list) {
			if (n.presa == true) {
				presasL.add(n);
			}
		}
		return presasL;
	}

	public int getDistancia(Nodo actual, Nodo inicio) {
		// return getShortestPath(actual, inicio).size();
		int res = 99;
		Transformer<Integer, Double> wtTransformer = new Transformer<Integer, Double>() {
			public Double transform(Integer link) {
				return 1.0;
			}
		};

		DijkstraShortestPath<Nodo, Integer> alg = new DijkstraShortestPath<Nodo, Integer>(
				g, wtTransformer);
		Number dist = alg.getDistance(actual, inicio);
		if (dist != null) {
			res = dist.intValue();
		}
		return res;
	}

	public boolean contains(Nodo n) {
		ArrayList<Nodo> ar = new ArrayList<>();
		ar.addAll(g.getVertices());
		boolean b = ar.contains(n);
		return b;
	}

	public void union(Grafo sensorGraph) {
		ArrayList<Nodo> sglist = sensorGraph.getListaNodos();
		for (Nodo n : sglist) {
			if (contains(n) == false) {
				addNode(n, sensorGraph);
			} else {
				Nodo tm = this.getNode(n.id);
				if (tm.score == -1) {
					tm.copyNode(n);
				}
				int s = tm.diffOfInfo(n);
				if (s == 1) {
					/*
					 * No se hace nada, puesto que nuestra memoria tiene un nodo
					 * que aporta mas info
					 */

				} else if (s == -1) { /* El nodo nuevo aporta m�s info y se copia */
					n.setGanancia(n.getGanancia() - tm.getGanancia());
					tm.copyNode(n);
				} else if (s == 2) { /*
									 * Los nodos son estimaciones y los vamos a
									 * unir
									 */
					tm.joinNode(n);
				} else {
					// Nada
				}
			}
		}
	}

	public Nodo getCazador() {
		ArrayList<Nodo> lista = getListaNodos();
		for (Nodo n : lista) {
			if (n.cazador == true)
				return n;
		}
		return null;
	}

	public void borraPresa(Nodo aux) {
		aux.presa = false;

	}

	public void InitSensorGraph(Grafo s) {
		ArrayList<ArrayList<Nodo>> tmp = new ArrayList<>();
		int w = 0;
		int edgecount = 0;
		x = s.x;
		y = s.y;
		for (int i = 0; i < s.y; i++) {
			tmp.add(new ArrayList<Nodo>());
			for (int j = 0; j < s.x; j++) {
				tmp.get(i).add(new Nodo(w, -1, new Point(j, i)));
				nodtopos.put(w, new Punto(j, i));
				postonod.put(new Punto(j, i), w);
				w++;
			}
		}

		// plotGraph("Holis");
	}

	@SuppressWarnings("unused")
	public void generaGrafo(int[] coordenadas, int def) {

		ArrayList<ArrayList<Nodo>> tmp = new ArrayList<>();
		int edgecount = 0;
		int w = 0;
		int a, b, c, d;
		boolean stop = false;

		for (int i = 0; i < coordenadas.length; i++) {
			coordenadas[i] = (coordenadas[i] * def) / 100;
		}
		this.x = coordenadas[0];
		this.y = coordenadas[1];

		for (int i = 0; i < y; i++) {
			tmp.add(new ArrayList<Nodo>());
			for (int j = 0; j < x; j++) {
				tmp.get(i).add(new Nodo(w, 0, new Point(j, i)));
				nodtopos.put(w, new Punto(j, i));
				postonod.put(new Punto(j, i), w);
				w++;
			}
		}
		grafo = tmp;
		for (int i = 0; i < tmp.size(); i++) {
			for (int j = 0; j < tmp.get(i).size(); j++) {
				Nodo n = tmp.get(i).get(j);

				if (i > 0) {
					g.addEdge(edgecount, tmp.get(i - 1).get(j), n);
					edgecount++;
				}
				if (j < y - 1) { // Derecha
					g.addEdge(edgecount, tmp.get(i).get(j + 1), n);
					edgecount++;
				}
				if (j > 0 && i < y - 1) { // Abajo izquierda
					g.addEdge(edgecount, tmp.get(i + 1).get(j - 1), n);
					edgecount++;
				}
				if (j < x - 1 && i < y - 1) { // Abajo derecha
					g.addEdge(edgecount, tmp.get(i + 1).get(j + 1), n);
					edgecount++;
				}
			}

		}
		for (int i = 2; i < coordenadas.length; i += 4) {
			int xini = coordenadas[i];
			int yini = coordenadas[i + 1];
			int xfin = coordenadas[i + 2];
			int yfin = coordenadas[i + 3];
			if (xini == 0 && yini == 0 && xfin == 0 && yfin == 0)
				break;
			if (euclideanDist(new Punto(xini, yini), new Punto(xfin, yfin)) <= 1) {
				ArrayList<Integer> aris = new ArrayList<>();
				Nodo n1 = new Nodo(postonod.get(new Punto(xini, yini)), 0,
						new Point(xini, yini));
				Nodo n2 = new Nodo(postonod.get(new Punto(xfin, yfin)), 0,
						new Point(xfin, yfin));
				aris.addAll(g.findEdgeSet(n1, n2));
				g.removeEdge(aris.get(0));
				continue;

			}
			// System.out.println(xini + " " + yini + " " + xfin + " " + yfin);
			for (int v = yini; v <= yfin; v++) {
				if (stop == true) {
					stop = false;
					break;
				}
				for (int u = xini; u <= xfin; u++) {
					if (u == xini && v == yini || u == xfin && v == yfin) {
						continue;
					} else {
						Nodo n1 = new Nodo(postonod.get(new Punto(u, v)), 0,
								new Point(u, v));
						ArrayList<Nodo> neig = new ArrayList<>();
						ArrayList<Integer> aris = new ArrayList<>();
						neig.addAll(g.getNeighbors(n1));
						/*
						 * for (int l = 0; l < neig.size(); l++) {
						 * aris.addAll(g.findEdgeSet(n1, neig.get(l)));
						 * g.removeEdge(aris.get(0)); n1.obstaculo=true;
						 * g.addVertex(n1); aris.clear(); }
						 */
						g.removeVertex(n1);
						n1.obstaculo = true;
						n1.score = -1;
						g.addVertex(n1);

					}

				}
			}

		}
		ArrayList<Nodo> nodes = new ArrayList<>();
		nodes.addAll(g.getVertices());
		ArrayList<Nodo> neig = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			Nodo n = nodes.get(i);
			int x = (int) n.getPos().x;
			int y = (int) n.getPos().y;

			if (x == 0) {
				n.oeste = Nodo.MENOS;
				n.noroeste = Nodo.MENOS;
				n.suroeste = Nodo.MENOS;
			}
			if (x == (this.x - 1)) {
				n.este = Nodo.MENOS;
				n.noreste = Nodo.MENOS;
				n.sureste = Nodo.MENOS;
			}
			if (y == 0) {
				n.noreste = Nodo.MENOS;
				n.noroeste = Nodo.MENOS;
				n.norte = Nodo.MENOS;

			}
			if (y == (this.y - 1)) {
				n.sureste = Nodo.FIN;
				n.suroeste = Nodo.FIN;
				n.sur = Nodo.FIN;
			}
			neig.clear();
			neig.addAll(g.getNeighbors(n));
			for (int j = 0; j < neig.size(); j++) {
				Nodo nei = neig.get(j);
				int ny = (int) nei.getPos().y;
				int nx = (int) nei.getPos().x;
				if (x > nx && y == ny)
					n.oeste = Nodo.MAS;
				if (x < nx && y == ny)
					n.este = Nodo.MAS;
				if (x == nx && y > ny)
					n.norte = Nodo.MAS;
				if (x == nx && y < ny)
					n.sur = Nodo.MAS;
				if (x < nx && y < ny)
					n.sureste = Nodo.MAS;
				if (x < nx && y > ny)
					n.noreste = Nodo.MAS;
				if (x > nx && y < ny)
					n.suroeste = Nodo.MAS;
				if (x > nx && y > ny)
					n.noroeste = Nodo.MAS;
			}

		}

	}

	public int euclideanDist(Punto p1, Punto p2) {
		int x = (int) (p2.x - p1.x);
		int y = (int) (p2.y - p1.y);
		x = (int) Math.pow(x, 2);
		y = (int) Math.pow(y, 2);
		int res = x + y;
		res = (int) Math.sqrt(res);
		return res;
	}

	public void plotNewGraph() {
		JFrame jf = new JFrame("Plotting new Graph");
		jf.setSize(400, 400);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
		jf.add(jp);
		ArrayList<JPanel> list = new ArrayList<>();
		for (int i = 0; i < y; i++) {
			list.add(new JPanel());
			list.get(i).add(new JLabel(i + ""));
			for (int j = 0; j < x; j++) {
				list.get(i).add(new JButton(grafo.get(i).get(j).toString()));
			}
			jp.add(list.get(i));
		}
		jf.setVisible(true);
	}

	public void updateNodo(Nodo src, Nodo dst) {
		int i = 0;
		ArrayList<Integer> lista = dst.getListaAristas();
		for (int ar : src.getListaAristas()) {
			if (lista.get(i) < ar) {
				lista.remove(i);
				lista.add(i, ar);
			}
			i++;
		}
		i = 0;
		dst.setNorte(lista.get(i));
		i++;
		dst.setNoreste(lista.get(i));
		i++;
		dst.setEste(lista.get(i));
		i++;
		dst.setSureste(lista.get(i));
		i++;
		dst.setSur(lista.get(i));
		i++;
		dst.setSuroeste(lista.get(i));
		i++;
		dst.setOeste(lista.get(i));
		i++;
		dst.setNoroeste(lista.get(i));
		i++;
	}

	public Nodo getNode(int id) {
		ArrayList<Nodo> al = new ArrayList<>();
		al.addAll(g.getVertices());
		int l = al.indexOf(new Nodo(id, 0, null));
		if (l < 0)
			return null;
		return al.get(l);
	}

	public boolean isEstimacion(Nodo n) {
		return n.isEstimacion();
	}

	public void creaNodoEstimacion(int time, int x, int y, Grafo m) {
		if(x < 0 || y < 0)
			return;
		int id = postonod.get(new Punto(x, y));
		Nodo n = new Nodo(id, 0, new Point(x, y));
		n.setEstimacion(true);

		addNode(n, m);
	}

	public void creaNodoEstimacion(int time, double x, double y, Grafo m) {
		int ix = (int) x;
		int iy = (int) y;
		creaNodoEstimacion(time, ix, iy, m);
	}
}

/**
 * Clase auxiliar para la busqueda del camino mas corto
 * 
 * @author Victor
 * 
 */
class Nodos {
	Nodo id;
	int coste;
	ArrayList<Nodos> antecesores;

	public Nodos(Nodo id, int coste, ArrayList<Nodos> antecesores) {
		super();
		this.id = id;
		this.coste = coste;
		this.antecesores = antecesores;
	}

	public Nodo getId() {
		return id;
	}

	public void setId(Nodo id) {
		this.id = id;
	}

	public int getCoste() {
		return coste;
	}

	public void setCoste(int coste) {
		this.coste = coste;
	}

	public String toString() {
		String s = "ID: " + id + " Coste: " + coste;
		return s;
	}

}

class Punto {
	int x;
	int y;

	public Punto(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		return x * 1000 + y;
	}

	public String toString() {
		return "[X: " + x + " e Y: " + y + "]";
	}

	int compareTo(Punto n) {
		if (n.x == x && n.y == y)
			return 0;
		else
			return 1;
	}

	@Override
	public boolean equals(Object object) {
		boolean sameSame = false;

		if (object != null && object instanceof Punto) {
			sameSame = this.x == ((Punto) object).x;
			sameSame = this.y == ((Punto) object).y && sameSame;
		}

		return sameSame;
	}
}