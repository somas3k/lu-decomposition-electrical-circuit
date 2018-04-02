package pl.edu.agh.mownit.Task3;

import org.jgrapht.generate.HyperCubeGraphGenerator;
import org.jgrapht.graph.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


public class GraphGenerator {

    public static void generateConsistentGraph(String fileName, int n, int SEM, int maxR, double prob){
        Map<Integer,List<Integer>> coincidence = new HashMap<>();
        for(int i = 0; i < n; ++i){
            List<Integer> x = new ArrayList<>();
            for(int j = i+1; j < n; ++j){
                x.add(j);
            }
            coincidence.put(i, x);
        }
        Random r = new Random();

        for(Integer v : coincidence.keySet()){
            List<Integer> xx = coincidence.get(v);
            int size = xx.size();
            for(int j = 0; j < size; ++j){
                double p = 1-Math.random();
                if(p > prob && xx.size() > 1){
                    xx.remove(r.nextInt(xx.size()));
                }
            }
        }

        for(Integer v : coincidence.keySet()){
            System.out.print(v+"-> ");
            for(Integer x : coincidence.get(v)){
                System.out.print(x+" ");
            }
            System.out.println();
        }
        r = new Random();
        int s = r.nextInt(n);
        int t = r.nextInt(n);
        while(t == s){
            t = r.nextInt();
        }
        if(s>t){
            int tmp = s;
            s=t;
            t=tmp;
        }

        try(PrintWriter pw = new PrintWriter(fileName)){
            pw.println(s+" "+t+" "+SEM);
            pw.println(s+" "+t+" 0");

            for(Integer v : coincidence.keySet()){
                List<Integer> xx = coincidence.get(v);
                if(xx.size()>1) {
                    for (Integer x : xx) {
                        if((v != s) || (x != t)) {
                            pw.println(v + " " + x + " " + (r.nextInt(maxR) + 1));
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



    public static void generateConnectedGraph(String path, int nodes, int edges, int SEM, int maxR){
        Random r = new Random();
        List<Integer> nodeList = new ArrayList<>();
        Set<Edge> edgeSet = new HashSet<>();
        VFactory factory = new VFactory();
        for(int i = 0; i < nodes; ++i){
            nodeList.add(factory.createVertex());
        }

        Set<Integer> S = new HashSet<>(nodeList);
        Set<Integer> T = new HashSet<>();

        Integer currentNode = (Integer)S.toArray()[r.nextInt(S.size())];
        S.remove(currentNode);
        T.add(currentNode);

        while(!S.isEmpty()){
            Integer neighborNode = (Integer)S.toArray()[r.nextInt(S.size())];
            if(!T.contains(neighborNode)){
                Edge e = new Edge(currentNode, neighborNode);
                edgeSet.add(e);
                S.remove(neighborNode);
                T.add(neighborNode);
            }
            currentNode = neighborNode;
        }
        if(edges > nodes*(nodes-1)/2) edges = nodes*(nodes-1)/2;
        while(edgeSet.size() < edges){
            Integer node1 = r.nextInt(nodes);
            Integer node2 = r.nextInt(nodes);
            while(node2.equals(node1)) node2 = r.nextInt(nodes);
            edgeSet.add(new Edge(node1, node2));
        }

        for(Edge e : edgeSet){
            System.out.println(e);
        }

        int s = r.nextInt(nodes);
        int t = r.nextInt(nodes);
        while(s == t) t = r.nextInt(nodes);
        Edge power = new Edge(s, t);


        try(PrintWriter pw = new PrintWriter(path)){
            pw.println(s+" "+t+" "+SEM);
            pw.println(s+" "+t+" 0");

            for(Edge e : edgeSet){
                if(!e.equals(power)) {
                    pw.println(e.node1 + " " + e.node2 + " " + (r.nextInt(maxR) + 1));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void generateCubicGraph(){
        HyperCubeGraphGenerator<Integer,DefaultEdge> gen = new HyperCubeGraphGenerator<>(3);
        DefaultDirectedGraph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        gen.generateGraph(graph, new VFactory(), null);
        System.out.println();
    }

    public static void generateBridgedGraph(String fileName,int N, int SEM, int maxR){
        Random r = new Random();
        List<Integer> nodeList1 = new ArrayList<>();
        List<Integer> nodeList2 = new ArrayList<>();
        VFactory factory = new VFactory();

        int div = r.nextInt(N/8) + N/2 - N/8;
        for(int i = 0; i < N; ++i){
            if(i < div) {
                nodeList1.add(factory.createVertex());
            }
            else
                nodeList2.add(factory.createVertex());
        }


        Set<Edge> edgeSet1 = new HashSet<>();
        Set<Edge> edgeSet2 = new HashSet<>();

        Set<Integer> S1 = new HashSet<>(nodeList1);
        Set<Integer> T1 = new HashSet<>();

        Set<Integer> S2 = new HashSet<>(nodeList2);
        Set<Integer> T2 = new HashSet<>();

        Integer currentNode1 = (Integer)S1.toArray()[r.nextInt(S1.size())];
        S1.remove(currentNode1);
        T1.add(currentNode1);

        Integer currentNode2 = (Integer)S2.toArray()[r.nextInt(S2.size())];
        S2.remove(currentNode2);
        T2.add(currentNode2);

        while(!S1.isEmpty()){
            Integer neighborNode1 = (Integer)S1.toArray()[r.nextInt(S1.size())];
            if(!T1.contains(neighborNode1)){
                Edge e = new Edge(currentNode1, neighborNode1);
                edgeSet1.add(e);
                S1.remove(neighborNode1);
                T1.add(neighborNode1);
            }
            currentNode1 = neighborNode1;
        }

        while(!S2.isEmpty()){
            Integer neighborNode2 = (Integer)S2.toArray()[r.nextInt(S2.size())];
            if(!T2.contains(neighborNode2)){
                Edge e = new Edge(currentNode2, neighborNode2);
                edgeSet2.add(e);
                S2.remove(neighborNode2);
                T2.add(neighborNode2);
            }
            currentNode2 = neighborNode2;
        }

        int n1 = nodeList1.size();
        int n2 = nodeList2.size();

        int edges1 = n1*(n1-2)/2 - n1;
        edges1 = r.nextInt(edges1) / r.nextInt(n1/2);

        int edges2 = n2*(n2-2)/2 - n2;
        edges2 = r.nextInt(edges2) / r.nextInt(n2/2);

        while(edgeSet1.size() < edges1){
            Integer node1 = nodeList1.get(r.nextInt(n1));
            Integer node2 = nodeList1.get(r.nextInt(n1));
            while(node2.equals(node1)) node2 = nodeList1.get(r.nextInt(n1));
            edgeSet1.add(new Edge(node1, node2));
        }

        while(edgeSet2.size() < edges2){
            Integer node1 = nodeList2.get(r.nextInt(n2));
            Integer node2 = nodeList2.get(r.nextInt(n2));
            while(node2.equals(node1)) node2 = nodeList2.get(r.nextInt(n2));
            edgeSet2.add(new Edge(node1, node2));
        }


        for(Edge e : edgeSet1) System.out.println(e);
        System.out.println();
        for(Edge e : edgeSet2) System.out.println(e);

        int s = nodeList1.get(r.nextInt(n1));
        int t = nodeList2.get(r.nextInt(n2));

        int b1 = nodeList1.get(r.nextInt(n1));
        int b2 = nodeList2.get(r.nextInt(n2));
        while(b1 == s || b1 == t){
            b1 = nodeList1.get(r.nextInt(n1));
        }
        while(b2 == t || b2 ==s){
            b2 = nodeList2.get(r.nextInt(n2));
        }
        edgeSet1.add(new Edge(b1, b2));
        Edge power = new Edge(s, t);


        try(PrintWriter pw = new PrintWriter(fileName)){
            pw.println(s+" "+t+" "+SEM);
            pw.println(s+" "+t+" 0");

            for(Edge e : edgeSet1){
                if(!e.equals(power)) {
                    pw.println(e.node1 + " " + e.node2 + " " + (r.nextInt(maxR) + 1));
                }
            }

            for(Edge e : edgeSet2){
                if(!e.equals(power)) {
                    pw.println(e.node1 + " " + e.node2 + " " + (r.nextInt(maxR) + 1));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void generateGridGraph(String fileName, int rows, int cols, int SEM, int maxR){
        Map<Integer, Integer> map = new TreeMap<>();
        VFactory vertexFactory = new VFactory();

        // Adding all vertices to the set
        for (int i = 0; i < (rows * cols); i++) {
            Integer vertex = vertexFactory.createVertex();
            map.put(i + 1, vertex);
        }

        Random r = new Random();

        int s = 0;
        int t = rows*cols-1;


        try(PrintWriter pw = new PrintWriter(fileName)){
            pw.println(s+" "+t+" "+SEM);
            pw.println(s+" "+t+" 0");

            for (int i : map.keySet()) {
                for (int j : map.keySet()) {
                    if ((((i % cols) > 0) && ((i + 1) == j)) || ((i + cols) == j)) {
                        pw.println(map.get(i) + " " + map.get(j) + " " + (r.nextInt(maxR) + 1));
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
class Edge{
    Integer node1;
    Integer node2;

    Edge(Integer node1, Integer node2){
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (Objects.equals(node1, edge.node1) &&
                Objects.equals(node2, edge.node2)) || ((Objects.equals(node2, edge.node1) &&
                Objects.equals(node1, edge.node2)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1, node2) + Objects.hash(node2, node1);
    }

    @Override
    public String toString() {
        return node1 + " ---> " + node2;
    }
}

