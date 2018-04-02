package pl.edu.agh.mownit.Task3;

import org.jgrapht.VertexFactory;

public class VFactory implements VertexFactory<Integer> {
    private int last;

    VFactory() {
        last = 0;
    }

    @Override
    public Integer createVertex() {
        return last++;
    }
}
