package pl.edu.agh.mownit.Task1;

public class GaussJordan {

    private final int length;

    private double[][] a;

    private double[] result;

    public GaussJordan(double[][] A, double[] b) {
        length = b.length;
        double maxInRow;


        a = new double[length][length+1];

        for( int i = 0; i < length; ++i){

            maxInRow = Double.MIN_VALUE;
            for( int j = 0; j < length+1; ++j){
                if( j < length ) a[i][j]=A[i][j];
                else a[i][j]=b[i];
                if(Math.abs(a[i][j]) > maxInRow) maxInRow = Math.abs(a[i][j]);
            }
            scaling(maxInRow, i);
        }
    }

    private void scaling(double divider, int row){
        for(int i = 0; i < length+1; ++i ) {
            a[row][i] /= divider;
        }

    }

    public void show(){
        for( int i = 0; i < length; ++i){
            for( int j = 0; j < length+1; ++j){
                if( j < length ) System.out.print(a[i][j]+" ");
                else System.out.print(" | " +a[i][j]);
            }
            System.out.print("\n");
        }
    }

    private void swapRows(int row1, int row2){
        if(row1 == row2 ) return;
        double[] tmp = a[row1];
        a[row1] = a[row2];
        a[row2] = tmp;

    }

    private void subtractRows(int row){

        for(int i = row+1; i < length; ++i){
            double m = a[i][row]/a[row][row];
            //System.out.println(m);
            for(int j = row; j < length + 1; ++j){
                a[i][j] -= m*a[row][j];
            }
        }
    }

    private int findPivot(int col) {
        double max = 0;
        int row_to_swap = 0;

        for (int i = col; i < length; ++i) {
            if(Math.abs(a[i][col]) > max ){
                max = a[i][col];
                row_to_swap = i;
            }
        }
        return row_to_swap;
    }

    private void fillResult(){
        result = new double[length];
        for(int i = 0; i < length; ++i){
            result[i] = a[i][length];
        }
    }

    public double[] getResult() {
        return result.clone();
    }

    public void showResult(){
        if(result != null){
            for (int i = 0; i < length; i++) {
                System.out.println(result[i]);
            }
        }
    }

    public void solve(){

        for(int i = 0; i < length; ++i){
            swapRows(i,findPivot(i));

            subtractRows(i);

        }

        for(int i = length-1; i >=0; i--){
            double sum = 0;
            for(int j = i+1; j<length; ++j) {
                sum+=a[i][j]*a[j][length];
            }
            a[i][length] = (a[i][length]-sum)/ a[i][i];
        }

        fillResult();
    }
}