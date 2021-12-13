import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96d;
    private final int trials;
    private final int sideLength;
    private final double[] pRate;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("N and Trails must be greater than 0.");
        this.trials = trials;
        sideLength = n;
        pRate = new double[trials];
        int totalSites = n * n;
        for (int t = 0; t < trials; t++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int openSite = StdRandom.uniform(totalSites);
                perc.open(getRow(openSite), getCol(openSite));
            }
            pRate[t] = (double) perc.numberOfOpenSites() / totalSites;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(pRate);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(pRate);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return (mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return (mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials));
    }

    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        StdOut.print("mean\t\t\t\t    = " + ps.mean());
        StdOut.println();
        StdOut.print("stddev\t\t\t\t    = " + ps.stddev());
        StdOut.println();
        StdOut.print("95% confidence interval = [" + ps.confidenceLo() + ", " + ps.confidenceHi() + "]");
        StdOut.println();
    }

    private int getRow(int index) {
        return index / sideLength + 1;
    }

    private int getCol(int index) {
        return index % sideLength + 1;
    }

}

