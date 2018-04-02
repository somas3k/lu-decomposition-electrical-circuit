package pl.edu.agh.mownit;

import pl.edu.agh.mownit.Task3.ElectricalCircuit;
import pl.edu.agh.mownit.Task3.GraphGenerator;

public class Main {

    private static void showTestResults(int resultSize, double[]... results){
        System.out.println("GJ:\t\t\t\t\t\tLU:\t\t\t\t\t\tQR:");
        for(int i = 0; i < resultSize; ++i){
            for(double[] result : results){
                System.out.print(String.format("%.15f   \t",result[i]));
            }
            System.out.println();

        }
    }

    public static void main(String[] args) throws InterruptedException {
//        int n = 5;
//        double[][] A = new double[n][n];
//        //double[][] A = {{2, -3, 1},{-1, 1, 2}, {1, -2, -1}};
//        double[] b = new double[n];
//        //double[] b = {4, 3, -1};
//        Random random = new Random();
//
//        long times[] = new long[4];
//        long startTime;
//
//        for (int i = 0; i < n; ++i) {
//            for (int j = 0; j < n; ++j) {
//                A[i][j] = random.nextDouble() * random.nextInt(100);
//
//            }
//            b[i] = random.nextDouble() * random.nextInt(100);
//        }
////
////        RealMatrix A_test = new Array2DRowRealMatrix(A, true);
////        RealVector b_test = new ArrayRealVector(b, false);
////
////        startTime = System.currentTimeMillis();
////        GaussJordan solver = new GaussJordan(A, b);
////        solver.solve();
////        double[] solution = solver.getResult();
////        times[0] = System.currentTimeMillis() - startTime;
////
////        Thread.sleep(1000);
////
////        startTime = System.currentTimeMillis();
////        LUDecomposition lu_test1 = new LUDecomposition(A_test);
////        RealVector solution_test1 = lu_test1.getSolver().solve(b_test);
////        times[1] = System.currentTimeMillis() - startTime;
////
////        Thread.sleep(1000);
////
////        startTime = System.currentTimeMillis();
////        QRDecomposition qr_test2 = new QRDecomposition(A_test);
////        RealVector solution_test2 = qr_test2.getSolver().solve(b_test);
////        times[2] = System.currentTimeMillis() - startTime;
////
////        Thread.sleep(1000);
////
////        showTestResults(solution.length, solution, solution_test1.toArray(), solution_test2.toArray());
////
////        System.out.println();
////        System.out.println("GJ:\t\t"+times[0]);
////        System.out.println("LU:\t\t"+times[1]);
////        System.out.println("QR:\t\t"+times[2]);
//
//
//
//        RealMatrix matrix = new Array2DRowRealMatrix(A, true);
//        LUDecomposition lu_test = new LUDecomposition(matrix);
//        System.out.println("A:");
//        for (int i = 0; i < n; ++i) {
//            for (int j = 0; j < n; ++j) {
//                System.out.print(String.format("%.2f \t", A[i][j]));
//            }
//            System.out.println();
//        }
//        System.out.println();
//        double[][] lu2 = lu_test.getL().getData();
//        double[][] u2 = lu_test.getU().getData();
//
//        LUFactorization lu = new LUFactorization(A);
//        lu.decompose();
//        double[][] L = lu.getL();
//        System.out.println("L:");
//        for (int i = 0; i < n; ++i) {
//            for (int j = 0; j < n; ++j) {
//               System.out.print(String.format("%.2f \t",L[i][j]));
//            }
//            System.out.println();
//        }
//        System.out.println();
//
//        System.out.println();
//        double[] S = lu.getScalingFactors();
//        double[][] U = lu.getU();
//        System.out.println("U:");
//        for (int i = 0; i < n; ++i) {
//            for (int j = 0; j < n; ++j) {
//                System.out.print(String.format("%.2f \t",U[i][j]));
//            }
//            System.out.println();
//        }
//        System.out.println();
//
//        RealMatrix L1 = new Array2DRowRealMatrix(L);
//        RealMatrix U1 = new Array2DRowRealMatrix(U);
//
//        RealMatrix P = new Array2DRowRealMatrix(lu.getP());
//        System.out.println("P:");
//        for (int i = 0; i < n; ++i) {
//            for (int j = 0; j < n; ++j) {
//                System.out.print(String.format("%.1f \t",lu.getP()[i][j]));
//            }
//            System.out.println();
//        }
//        System.out.println();
//        P = MatrixUtils.inverse(P);
//        L1 = P.multiply(L1);
//        RealMatrix result = L1.multiply(U1);
//
//        System.out.println("A = P^{-1}LU  with rescaling");
//        double[][] res = result.getData();
//        for (int i = 0; i < n; ++i) {
//            for (int j = 0; j < n; ++j) {
//                System.out.print(String.format("%.2f \t",res[i][j]*S[i]));
//            }
//            System.out.println();
//        }

        //GraphGenerator.generateCubicGraph();

        //GraphGenerator.generateBridgedGraph("bridged.txt", 60, 24,15);

        int rows = 8;
        int cols = 6;
        GraphGenerator.generateGridGraph("gridGraph.txt", rows, cols, 24, 10);
//        GraphGenerator.generateConsistentGraph("randomConsistentGraph.txt", 25, 24, 20, 0.1d);
        //GraphGenerator.generateConnectedGraph("connectedGraph.txt", 30, 30, 24, 10);
        ElectricalCircuit circuit = new ElectricalCircuit("gridGraph.txt");
        circuit.loadGraphFromFile();

        circuit.solveKirchoffLaws();
        //circuit.printSolution();
        //circuit.solveNodesPotentials();
        if(circuit.verify(10e-10)){
            System.out.println("Valid");
        }
        //circuit.printSolution();
        //circuit.printSolution();
        //circuit.showGraph();
        circuit.showGraph(true, cols);








    }


}
