package pl.edu.agh.mownit.Task2;

public class LUFactorization {
    private double[][] U;
    private double[][] L;
    private final int n;
    private double[][] P;
    private double[] scalingFactors;

    public LUFactorization(double[][] data){
        n = data.length;
        double maxInRow;
        U = new double[n][n];
        P = new double[n][n];
        scalingFactors = new double[n];
        L = new double[n][n];

        for (int i = 0; i < n; i++) {
            P[i][i]=1;
            maxInRow = 0;
            for( int j = 0; j < n; ++j) {
                U[i][j] = data[i][j];
                if (Math.abs(data[i][j]) > maxInRow) maxInRow = Math.abs(data[i][j]);
            }
            scaling(maxInRow, i);
        }
    }

    private void scaling(double divider, int row){

        for(int i = 0; i < n; ++i ) {
            U[row][i] /= divider;

        }
        scalingFactors[row] = divider;
    }

    private void prepareL(){
        for (int i = 0; i < n; ++i){
            L[i][i]=1;
        }
    }

    private void swapRows(int row1, int row2){
        if(row1 == row2) return;
        double[] tmp = U[row1];
        U[row1] = U[row2];
        U[row2] = tmp;
        tmp = L[row1];
        L[row1]=L[row2];
        L[row2]=tmp;
        tmp = P[row1];
        P[row1] = P[row2];
        P[row2] = tmp;
    }

    private int findPivot(int col) {
        double max = 0;
        int row_to_swap = 0;

        for (int i = col; i < n; ++i) {
            if(Math.abs(U[i][col]) > max ){
                max = U[i][col];
                row_to_swap = i;
            }
        }
        return row_to_swap;
    }

    private void subtractRows(int row){
        for(int i = row+1; i < n; ++i){
            double m = U[i][row]/U[row][row];
            L[i][row] = m;
            for(int j = row; j < n; ++j){
                U[i][j] -= m*U[row][j];
            }
        }
    }

    public void decompose(){
        for(int i = 0; i < n; ++i){
            swapRows(i, findPivot(i));
            subtractRows(i);
        }
        prepareL();
    }

    public double[][] getL() {
        return L.clone();
    }

    public double[][] getU() {
        return U.clone();
    }

    public double[] getScalingFactors() {
        return scalingFactors;
    }

    public double[][] getP() {
        return P.clone();
    }
}
