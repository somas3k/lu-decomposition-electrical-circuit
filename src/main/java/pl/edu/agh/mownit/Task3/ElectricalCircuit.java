package pl.edu.agh.mownit.Task3;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.simple.SimpleGridLayout;
import com.sun.istack.internal.Nullable;
import org.apache.commons.math3.linear.*;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.jgraph.graph.DefaultEdge;

import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;
import java.util.List;

public class ElectricalCircuit {
    private Vertex s;
    private Vertex t;
    private double SEM;
    private String filePath;
    private DefaultDirectedGraph<Vertex, WeightedEdge> graph;
    private double[] solution;
    private double maxI;
    private Set<WeightedEdge> edges;
    private int edgesSize;
    private List<Vertex> nodes;
    private int nodesSize;

    public ElectricalCircuit(String filepath) {
        this.filePath = filepath;
        this.graph = new DefaultDirectedGraph<>(WeightedEdge.class);
    }

    public void loadGraphFromFile(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath)).useLocale(Locale.US);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert scanner != null;
        if(scanner.hasNext()) s = new Vertex(scanner.nextInt());
        if(scanner.hasNext()) t = new Vertex(scanner.nextInt());
        if(scanner.hasNext()) SEM = scanner.nextDouble();
        int edgeNumber = 0;
        Map<Vertex,List<Map<Vertex,Double>>> coincidence = getCoincidence(scanner);

        List<Vertex> nodes2 = new ArrayList<>(coincidence.keySet());
        Collections.sort(nodes2);
        for(Vertex n : nodes2){
            graph.addVertex(n);
        }
        for(Vertex n : nodes2){
            List<Map<Vertex,Double>> entries = coincidence.get(n);
            for(Map<Vertex,Double> entry : entries){
                for(Vertex n2 : entry.keySet()){
                    if(addEdge(n, n2, entry.get(n2), edgeNumber)) edgeNumber++;
                }
            }
        }

        List<Vertex> nodes = new ArrayList<>(graph.vertexSet());
        Collections.sort(nodes);

        int vNumber = 0;
        for(Vertex v : nodes){
            if(v.value.equals(s.value)) s.vNumber = vNumber;
            if(v.value.equals(t.value)) t.vNumber = vNumber;
            v.vNumber = vNumber;
            vNumber++;
        }
    }

    public void solveKirchoffLaws() {
        edges = graph.edgeSet();
        nodes = new ArrayList<>(graph.vertexSet());
        Collections.sort(nodes);
        edgesSize = edges.size();
        nodesSize = nodes.size();

        double[][] a = null;
        double[] b = new double[nodesSize];
        for (Vertex node : graph.vertexSet()) {
            if (a == null) a = new double[1][edgesSize];
            else {
                a = Arrays.copyOf(a, a.length + 1);
                a[a.length - 1] = new double[edgesSize];
            }
            //in
            for (WeightedEdge nonNeigh : graph.incomingEdgesOf(node)) {
                a[a.length - 1][nonNeigh.getEdgeNumber()] = 1;
            }

            //out
            for (WeightedEdge neigh : graph.outgoingEdgesOf(node)) {
                a[a.length - 1][neigh.getEdgeNumber()] = -1;
            }
        }

        AsUndirectedGraph<Vertex, WeightedEdge> undirectedGraph = new AsUndirectedGraph<>(graph);
        PatonCycleBase<Vertex, WeightedEdge> cycleBase = new PatonCycleBase<>();
        cycleBase.setGraph(undirectedGraph);

        for (List<Vertex> cycle : cycleBase.findCycleBase()) {
            cycle.add(cycle.get(0));

            assert a != null;
            a = Arrays.copyOf(a, a.length + 1);
            a[a.length - 1] = new double[edgesSize];

            b = Arrays.copyOf(b, b.length + 1);
            if (cycle.contains(s) && cycle.contains(t)) {
                b[b.length - 1] = SEM;
            } else b[b.length - 1] = 0;

            for (int i = 0; i < cycle.size() - 1; i++) {
                Vertex[] edgeInGraph = new Vertex[2];
                Vertex[] edgeInCycle = new Vertex[2];
                edgeInGraph[0] = edgeInCycle[0] = cycle.get(i);
                edgeInGraph[1] = edgeInCycle[1] = cycle.get(i + 1);
                if (notContainsEdge(edgeInCycle[0], edgeInCycle[1])) {
                    edgeInGraph[0] = cycle.get(i + 1);
                    edgeInGraph[1] = cycle.get(i);
                }
                WeightedEdge e = getEdge(edgeInGraph);
                assert e != null;
                if (Arrays.equals(edgeInCycle, edgeInGraph)) {
                    a[a.length - 1][e.getEdgeNumber()] = e.getWeight();
                } else {
                    a[a.length - 1][e.getEdgeNumber()] = -e.getWeight();
                }
            }
        }

        RealMatrix A = new Array2DRowRealMatrix(a, false);
        RealVector B = new ArrayRealVector(b, false);

        DecompositionSolver solver = new QRDecomposition(A).getSolver();

        solution = solver.solve(B).toArray();
        WeightedEdge[] edges = new WeightedEdge[graph.edgeSet().size()];
        graph.edgeSet().toArray(edges);
        maxI = Double.MIN_VALUE;
        for (WeightedEdge e : edges) {
            double I = solution[e.getEdgeNumber()];
            if (Math.abs(I) > maxI) maxI = Math.abs(I);
            if (I < 0) {
                graph.removeEdge(e);
                WeightedEdge e2 = graph.addEdge(((Vertex) e.getTarget()), ((Vertex) e.getSource()));
                e2.setParams(e.getEdgeNumber(), e.getWeight());
                e2.setI((-1) * I);
            } else e.setI(I);
        }
    }

    public void solveNodesPotentials(){
        edges = graph.edgeSet();
        nodes = new ArrayList<>(graph.vertexSet());
        Collections.sort(nodes);
        edgesSize = edges.size();
        nodesSize = nodes.size();

        double[][] a = new double[nodesSize][nodesSize];
        double[] b = new double[nodesSize];
        NeighborIndex<Vertex, WeightedEdge> x = new NeighborIndex<>(graph);

        int i = 0;
        for(Vertex node : graph.vertexSet()){
            if(node.equals(s)){
                a[i][node.vNumber] = 1;
            }
            else{
                if(node.equals(t)){
                    a[i][node.vNumber] = 1;
                    b[i] = SEM;
                }
                else{
                    for(Vertex neigh : x.neighborListOf(node)){
                        neigh = nodes.get(nodes.indexOf(neigh));
                        Vertex[] edge = new Vertex[2];
                        edge[0] = node;
                        edge[1] = neigh;

                        if(notContainsEdge(edge[0], edge[1])){
                            edge[0] = neigh;
                            edge[1] = node;
                        }
                        a[i][node.vNumber] += 1.0/graph.getEdge(edge[0], edge[1]).getWeight();
                        a[i][neigh.vNumber] -= 1.0/graph.getEdge(edge[0], edge[1]).getWeight();
                    }
                }
            }
            i++;
        }

        RealMatrix A = new Array2DRowRealMatrix(a, false);
        RealVector B = new ArrayRealVector(b, false);

        DecompositionSolver solver = new LUDecomposition(A).getSolver();

        double[] potentials = solver.solve(B).toArray();

        double[] currents = new double[edgesSize];

        for(WeightedEdge e : edges){
            Vertex source = (Vertex)e.getSource();
            source = nodes.get(nodes.indexOf(source));
            Vertex target = (Vertex)e.getTarget();
            target = nodes.get(nodes.indexOf(target));
            if(!source.equals(s) || !target.equals(t)){
                double v_s  = potentials[source.vNumber];
                double v_t  = potentials[target.vNumber];
                currents[e.getEdgeNumber()] = (v_t-v_s)/e.getWeight();
            }
        }

        WeightedEdge e = graph.getEdge(s, t);

        for(WeightedEdge nonNeigh : graph.incomingEdgesOf(s)){
            currents[e.getEdgeNumber()] += currents[nonNeigh.getEdgeNumber()];
        }

        for(WeightedEdge neigh : graph.outgoingEdgesOf(s)){
            if(neigh != e){
                currents[e.getEdgeNumber()] -= currents[neigh.getEdgeNumber()];
            }
        }

        solution = currents;

        WeightedEdge[] edges = new WeightedEdge[graph.edgeSet().size()];
        graph.edgeSet().toArray(edges);
        maxI = Double.MIN_VALUE;
        for (WeightedEdge edge : edges) {
            double I = solution[edge.getEdgeNumber()];
            if (Math.abs(I) > maxI) maxI = Math.abs(I);
            if (I < 0) {
                graph.removeEdge(edge);
                WeightedEdge e2 = graph.addEdge((Vertex) edge.getTarget(), (Vertex) edge.getSource());
                e2.setParams(edge.getEdgeNumber(), edge.getWeight());
                e2.setI((-1) * I);
            } else edge.setI(I);
        }
    }

    public boolean verify(double eps){
        int non_valid = 0;
        for(Vertex v : graph.vertexSet()){
            double sumCurrents = 0;
            for(WeightedEdge e : graph.incomingEdgesOf(v)){
                sumCurrents += e.getI();
            }
            for(WeightedEdge e : graph.outgoingEdgesOf(v)){
                sumCurrents -= e.getI();
            }
            if(sumCurrents > eps){
                non_valid++;
            }
        }

        return non_valid == 0;
    }

    public void showGraph(boolean isGrid, @Nullable Integer cellsPerRow){
        JGraphModelAdapter<Vertex,WeightedEdge> model = new JGraphModelAdapter<>(graph);

        for(WeightedEdge e : graph.edgeSet()){
            DefaultEdge edge = model.getEdgeCell(e);

            GraphConstants.setExactSegmentLabel(edge.getAttributes(), true);
            GraphConstants.setLineColor(edge.getAttributes(), Color.BLACK);
            GraphConstants.setLabelAlongEdge(edge.getAttributes(), true);
            GraphConstants.setLineWidth(edge.getAttributes(), (float)((e.getI()/maxI)*5)+0.2f);
            GraphConstants.setFont(edge.getAttributes(), new Font(Font.DIALOG, Font.BOLD, 18));
        }

        for(Vertex v : graph.vertexSet()){
            DefaultGraphCell cell = model.getVertexCell(v);
            GraphConstants.setAutoSize(cell.getAttributes(),true);
        }

        JGraph jGraph = new JGraph(model);

        JGraphFacade facade = new JGraphFacade(jGraph);
        facade.setOrdered(true);

        JGraphLayout layout;
        if(isGrid) {

            layout = new SimpleGridLayout();
            ((SimpleGridLayout) layout).setActOnUnconnectedVerticesOnly(false);
            ((SimpleGridLayout) layout).setNumCellsPerRow(cellsPerRow);
            ((SimpleGridLayout) layout).setWidthSpacing(50);
            ((SimpleGridLayout) layout).setOffsetX(50);
            ((SimpleGridLayout) layout).setOffsetY(50);
            ((SimpleGridLayout) layout).setHeightSpacing(50);
        }
        else{
            layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE);
            ((JGraphSimpleLayout)layout).setMaxx(10);
            ((JGraphSimpleLayout)layout).setMaxy(10);
        }

        facade.setOrdered(true);
        facade.setCircleRadiusFactor(0.5);
        layout.run(facade);
        Map nested = facade.createNestedMap(true, false);

        jGraph.getGraphLayoutCache().edit(nested);

        JFrame frame = new JFrame();
        JScrollPane pane = new JScrollPane(jGraph);

        pane.setSize(800, 600);

        frame.getContentPane().add(pane);

        frame.pack();
        frame.setSize(1920,1080);
        frame.setVisible(true);
    }

    private boolean addEdge(Vertex v1, Vertex v2, double weight, int edgeNumber){
        if(graph.getEdge(v2,v1) == null && graph.getEdge(v1,v2) == null) {
            WeightedEdge e = graph.addEdge(v1, v2);
            e.setParams(edgeNumber, weight);
            return true;
        }
        return false;
    }

    private Map<Vertex,List<Map<Vertex,Double>>> getCoincidence(Scanner scanner){
        Map<Vertex,List<Map<Vertex,Double>>> coincidence = new HashMap<>();
        while(scanner.hasNext()){
            int v1 = scanner.nextInt();
            int v2 = scanner.nextInt();
            double weight = scanner.nextDouble();
            List<Map<Vertex,Double>> entries;
            if(!coincidence.keySet().contains(new Vertex(v1))){
                entries=new ArrayList<>();
                coincidence.put(new Vertex(v1), entries);
            }
            else {
                entries = coincidence.get(new Vertex(v1));
            }
            if(!coincidence.keySet().contains(new Vertex(v2))){
                coincidence.put(new Vertex(v2), new ArrayList<>());
            }

            Map<Vertex,Double> entry = new HashMap<>();
            entry.put(new Vertex(v2), weight);
            entries.add(entry);
        }
        return coincidence;
    }

    private boolean notContainsEdge(Vertex source, Vertex target){
        for(WeightedEdge edge : edges){
            if(edge.getSource().equals(source)  && edge.getTarget().equals(target)){
                return false;
            }
        }
        return true;
    }

    private WeightedEdge getEdge(Vertex[] x){
        for(WeightedEdge edge : edges){
            if(edge.getSource().equals(x[0]) && edge.getTarget().equals(x[1])){
                return edge;
            }
        }
        return null;
    }
}
