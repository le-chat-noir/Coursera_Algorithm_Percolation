import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private static final int OFFSET = 1;
    private final int sideLength;
    private final WeightedQuickUnionUF ufFull;
    private final WeightedQuickUnionUF ufPerc;
    private final int size;
    private boolean[] privateMap;
    private int openedSites;

    /*
    Two WeightedQuickUnionUF are needed.
    If connecting anything at top row to virtual top node, and connecting
    anything at bottom row to virtual bottom node. Once the system is percolated,
    the virtual top and the virtual bottom will be in the same group. <- This always holds true.
    However any bottom row cell is also in the same group as the virtual bottom node,
    even if it is not really connected to other cells, this leads to the aloof cell still
    seems connected all the way to the top.

    IT IS CONNECTED BY THE GHOST VIRTUAL BOTTOM NODE!!

    If only one WeightedQuickUnionUF is used, to find percolation the cells
    in bottom row have to be looped, which hurts performance badly.

    Using two WeightedQuickUnionUF is faster because there is no looping at
    the bottom row, this is essential for benchmarking on the PercolationStats.

    The extra WeightedQuickUnionUF never connected to the virtual bottom, thus prevent
    the "virtual bottom ghost connection" issue.
    */
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("N must be great than 0.");
        size = n * n;
        ufFull = new WeightedQuickUnionUF(size + OFFSET + 1);
        ufPerc = new WeightedQuickUnionUF(size + OFFSET + 1);
        privateMap = new boolean[size + OFFSET];
        sideLength = n;
        openedSites = 0;
    }

    public void open(int row, int col) {
        checkValid(row, col);
        if (isOpen(row, col))
            return;

        int currentIndex = toIndex(row, col);
        int toWireIndex;
        privateMap[currentIndex] = true;
        openedSites++;

        // Do union:
        // Top or up cell
        if (row == 1) {
            ufFull.union(currentIndex, 0);
            ufPerc.union(currentIndex, 0);
        } else if (isOpen(row - 1, col)) {
            toWireIndex = toIndex(row - 1, col);
            ufFull.union(currentIndex, toWireIndex);
            ufPerc.union(currentIndex, toWireIndex);
        }

        // Left cell
        if (col != 1 && isOpen(row, col - 1)) {
            toWireIndex = toIndex(row, col - 1);
            ufFull.union(currentIndex, toWireIndex);
            ufPerc.union(currentIndex, toWireIndex);
        }
        // Right cell
        if (col != sideLength && isOpen(row, col + 1)) {
            toWireIndex = toIndex(row, col + 1);
            ufFull.union(currentIndex, toWireIndex);
            ufPerc.union(currentIndex, toWireIndex);
        }

        // Bottom or down cell
        if (row == sideLength) {
            ufPerc.union(currentIndex, size + OFFSET);
        } else if (isOpen(row + 1, col)) {
            toWireIndex = toIndex(row + 1, col);
            ufFull.union(currentIndex, toWireIndex);
            ufPerc.union(currentIndex, toWireIndex);
        }
    }

    public boolean isOpen(int row, int col) {
        checkValid(row, col);
        return privateMap[toIndex(row, col)];
    }

    public boolean isFull(int row, int col) {
        checkValid(row, col);
        return ufFull.find(toIndex(row, col)) == ufFull.find(0);
    }

    public int numberOfOpenSites() {
        return openedSites;
    }

    public boolean percolates() {
        return ufPerc.find(size + OFFSET) == ufPerc.find(0);
    }

    private void checkValid(int row, int col) {
        if (row > this.sideLength || col > this.sideLength || row <= 0 || col <= 0)
            throw new IllegalArgumentException("Out of range.");
    }


    private int toIndex(int row, int col) {
        return (row - 1) * sideLength + col - 1 + OFFSET;
    }
}
