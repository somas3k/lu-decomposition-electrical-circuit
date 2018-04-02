package pl.edu.agh.mownit.Task3;

import org.jgrapht.graph.DefaultEdge;

import java.text.DecimalFormat;

public class WeightedEdge extends DefaultEdge implements Comparable {
    private double weight;
    private int edgeNumber;
    private double I;

    public void setParams(int edgeNumber,double weight) {
        this.edgeNumber = edgeNumber;
        this.weight = weight;
    }
    public int getEdgeNumber(){
        return edgeNumber;
    }

    public double getWeight(){
        return weight;
    }

    public Object getSource(){
        return super.getSource();
    }

    public Object getTarget(){
        return super.getTarget();
    }

    public void setI(double I){ this.I = I; }

    public double getI() {
        return I;
    }

    @Override
    public String toString(){
        return new DecimalFormat("#0.00").format(I);
    }

    @Override
    public int compareTo(Object o) {
        if(((Vertex)this.getSource()).value > ((Vertex)((WeightedEdge)o).getSource()).value)
            return 1;
        if(this.getSource().equals(((WeightedEdge)o).getSource())) {
            if(((Vertex)this.getTarget()).value > ((Vertex)((WeightedEdge)o).getTarget()).value)
                return 1;
            if(this.getTarget().equals(((WeightedEdge)o).getTarget())) return 0;
            return -1;
        }
        return -1;
    }
}
