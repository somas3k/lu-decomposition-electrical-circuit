package pl.edu.agh.mownit.Task3;

import java.util.Objects;

public class Vertex implements Comparable<Vertex> {
    public final Integer value;
    public int vNumber;
    Vertex(int value){
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(value, vertex.value);
    }

    @Override
    public int compareTo(Vertex o) {
        return Integer.compare(value, o.value);
    }

    @Override
    public String toString() {
        return value + "";
    }
}
