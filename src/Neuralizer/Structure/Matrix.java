package Neuralizer.Structure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.DoubleStream;

/**
 * I have now made a Matrix class for CS151, CS231, CS251, and CS461. Not bad. Because
 * of bad OO implementations (poorly designed equals/clone methods, overloading with
 * identical formal size, etc.) chosen to keep with Heaton's description of the class's
 * functionalities, it is fundamental that this class is final to ensure expected
 * behavior.
 *
 * <br></br>See "Effective Java Programming," 37, for treatment of the Cloneable interface
 * Created by Matt Levine on 4/9/14. The reasons discussed therewithin also
 * requisite that this be a final class.
 *
 * <br>Several methods were tested for copying arrays - System.arrayCopy is by far the fastest, at
 * about twice as fast as calling clone on an array (which involves a new Object creation). A
 * simple loop is much slower than a System.arrayCopy, but not paralytically so.</br>
 *
 * <br>This class makes use of some cool Java 8 features to optimize its operations, especially
 * Streams, which are very interesting for data analysis.</br>
 *
 * <br>Methods such as isZero and sum run in constant time (in almost all conditions). The class
 * is designed to be as efficient as possible in MOST circumstances, though some extreme though
 * potential optimizations were left out, like advanced matrix multiplication. </br>
 */

public final class Matrix implements Cloneable{

    // we leave this non-final for use with Java 8 DoubleStream
    private final double[] data;
    private final int rows;
    private final int cols;

    private int isZero = 0;
    private double sum = 0;

    /** Returns an empty matrix of the given size
     * @param rows number of rows
     * @param cols number of columns
     */
    public Matrix( final int rows, final int cols ){
        this(new double[rows * cols],rows,cols,0,0);
    }

    /** Returns a matrix defined by the n by m array. This is not a fast
     * constructor.
     * @param aData the constructor definition
     */
    public Matrix( final double[][] aData ){
        if (aData == null ) throw new MatrixInstantiationException();
        if (aData.length <= 0 || aData[0].length <= 0 )
            throw new MatrixInstantiationException(aData[0].length,aData.length);

        cols = aData.length;
        rows = aData[0].length;
        data = new double[rows*cols];

        for (int col = 0; col < cols; col++ )
            for (int row = 0; row < rows; row++){
                data[row*(cols)+col] = aData[col][row];
                isZero += aData[col][row] == 0 ? 0 : 1;
                sum += aData[col][row];
            }
    }

    /** Internally used for constructing new Matrices **/
    private Matrix(final double[] newData, final int rows, final int cols, final int isZero,
                   final double sum){
        if (newData == null )
            throw new MatrixInstantiationException();
        if (newData.length <= 0 || newData.length != rows*cols) {
            throw new MatrixInstantiationException(rows, cols, newData.length);
        }
        data = newData;
        this.rows = rows;
        this.cols = cols;
        this.isZero = isZero;
        this.sum = sum;
    }

    /** Internally used for copying matrices **/
    private Matrix(Matrix copy){
        rows = copy.rows;
        cols = copy.cols;
        isZero = copy.isZero;
        sum = copy.sum;

        data = copy.data.clone();

    }

/** Static method which creates a matrix with a single column.
     * @param input the value of that column
     * @return single column matrix
     */
    public static Matrix createColumnMatrix(final double input[]){
        double[] data = input.clone();
        double isZero = DoubleStream.of(input).map(operand -> operand == 0 ? 0 : 1).sum();
        return new Matrix(data,input.length,1,
                (int)isZero,
                DoubleStream.of(input).sum()
        );
    }

/** Static method which creates a matrix with a single row.
     * @param input the value of that row
     * @return single row matrix
     */
    public static Matrix createRowMatrix(final double input[]){
        double[] data = input.clone();
        double isZero = DoubleStream.of(input).map(operand -> operand == 0 ? 0 : 1).sum();
        return new Matrix(data,1,input.length,
                (int) isZero,
                DoubleStream.of(input).sum()
        );
    }


/** Adds the specified value to every cell in the matrix.
     * @param row the row to which to add
     * @param col the column to which to add
     * @param value the value to add
     */

    public void add(final int row, final int col, final double value){
        isZero = isZero - (get(row,col)==0?0:1) + ((get(row,col)+value)==0?0:1);
        sum += value;
        set(row,col,value + get(row,col));
    }


/** Sets every cell in a matrix to zero.  */
    public void clear(){
        Arrays.setAll(data,i->0);
        isZero = 0;
        sum = 0;
    }

/** Creates an exact copy of the matrix. We accept the Cloneable interface,
 * despite the numerous shortcomings outlined by Bloch around page 37, because
 * Heaton requests the clone method by name. He probably did not mean to
 * enforce this EXACT method, but more its functionality; we follow his
 * stated convention none-the-less.
**/
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Matrix clone() {
        return new Matrix(this);
    }

    /** Returns true if equal to the other matrix. This violates the conventions
     * in Skrien and Bloch by request of Heaton, whose model we follow in this class.
      * @param matrix the comparison matrix
     * @return equality
     */
    public boolean equals(final Matrix matrix){
        return  matrix.rows == rows || matrix.cols == cols || Arrays.equals(matrix.data, data);
    }

    /** For functionality with equals - we follow strategy on Bloch, 33.
     * @return integer hash value
     */
    @Override
    public int hashCode() {
        int result = 23919 + 38 * rows + cols;
        for (int i = 0; i < cols*rows; i++) {
            long f = Double.doubleToLongBits(data[i]);
            result = 37*result + (int) (f ^ (f >>> 32));
        }
        return result;
    }

    /** Returns true if the two matrices are equal to a given precision
     * This alternative is slightly slower. The misleading overload is
     * again blamed on Heaton's stylizations.
     * @param matrix the comparator matrix
     * @param precision the error
     * @return equality
     */
    public boolean equals(final Matrix matrix, final int precision){
        long metric = (long) Math.pow(10,precision);
        if (matrix.rows != rows || matrix.cols != cols) return false;
        for (int i = 0; i < cols*rows; i++){
            if (  ((int)(matrix.data[i]*metric))/metric != data[i])
                return false;
        }
        return true;
    }

    /** Returns the value at the indicated position
     * @param row row of value
     * @param col column of value
     * @return value
     */
    public double get(final int row, final int col){
        if (row > rows || col > cols)
            throw new UnsupportedMatrixOperation(this,row,col);
        return data[row*cols+col];
    }

/** Return the column as a new matrix object
     * @param col the column index
     * @return the column
     */
    public Matrix getCol(final int col){
        if (col >= cols )
            throw new UnsupportedMatrixOperation(this,"\"retrieve column "+col+"\"");
        double[] data = Arrays.copyOfRange(this.data,col*rows,rows*(col+1));
        return new Matrix(data,data.length,1,
                DoubleStream.of(data).allMatch(value -> value == 0) ? -1 : 0,
                DoubleStream.of(data).sum());
    }

/** Returns the number of columns in the matrix
     * @return number of columns
     */
    public int getCols(){return cols;}

/** Returns the rows of the matrix as new matrix
     * @return rows
     */
    public Matrix getRow(final int row){
        double[] newData = new double[cols];
        double sum = 0;
        int isZero = 0;
        for (int i = 0; i < cols; i++) newData[i] = get(row,i);
        return new Matrix(newData,1,cols,isZero,sum);
    }

/** Returns the number of rows in the matrix
     * @return rows of columns
     */
    public int getRows(){return rows;}

/** Returns true if the matrix is a single row or column **/
    public boolean isVector(){return rows == 1 || cols == 1;}

    /** Returns true if all values of the matrix are zero **/
    public boolean isZero(){
        return isZero == -1 ? calcIsZero() : isZero == 0;
    }

    /** Calculates isZero, stores it and returns it
     *  (violating OO principle for convenience).
     */
    private boolean calcIsZero(){
        int count = 0; //nothing fancy here, I'm tired
        for (double aData : data) count += aData != 0 ? 1 : 0;
        isZero = count;
        return isZero();
    }

    /**
     * Sets the particular value at the particular position. Assumes valid
     * dimensionality. The function used to map (Z,Z) into Z is not injective
     * mathematically, but rather forced into injectivity by definition.
     * @param row the row to which to set
     * @param col the column index
     * @param value the value to set
     */
    public void set(final int row, final int col, final double value){
        if (row >= rows || col >= cols) throw new UnsupportedMatrixOperation(this,row,col);
        isZero -= data[row*(cols)+col] == 0 ? 0 : 1;
        sum -= data[row*(cols)+col];
        data[row*(cols)+col] = value;
        isZero += value == 0 ? 0 : 1;
        sum += value;
    }

    /** Returns the sum of the matrix **/
    public double sum(){ return sum != Double.MIN_VALUE ? sum : calcSum(); }

    /** Internally used infrequently to manually calculate the sum **/
    private double calcSum() {
        double count = 0;
        for (double aData : data ) count += aData;
        sum = count;
        return sum();
    }

    /** Returns an equivalent one dimensional array **/
    public double[] toPackedArray(){
        return data.clone();
    }

    /** This static class is a basic Matrix operational library for communicating between
     * matrices. All functions return a new Matrix and do not modify the inputs. The class
     * is nested for efficient access to protected Matrix features.
     */
    public static class MatrixMath {

        /** Adds two matrices and returns the sum
         * @param a the first matrix
         * @param b the second matrix
         * @return the sum matrix
         */
        public static Matrix add(final Matrix a, final Matrix b){
            if (a.rows != b.rows || a.cols != b.cols)
                throw new UnsupportedMatrixOperation(a,b);
            double[] data = new double[a.rows*a.cols];
            Arrays.setAll(data, i->a.data[i] + b.data[i]);
            return new Matrix(data,a.getRows(),a.getCols(),-1,a.sum+b.sum);
        }

        /** Divides two matrices and returns the outcome
         * @param a the first matrix
         * @param b the second matrix
         * @return the outcome matrix
         */
        public static Matrix divide(final Matrix a,final double b){
            if (b == 0)
                throw new UnsupportedMatrixOperation(a,"divide by 0");
            double[] data = new double[a.rows*a.cols];
            Arrays.setAll(data, i->a.data[i] / b);
            return new Matrix(data,a.getRows(),a.getCols(),a.isZero,a.sum/b);

        }

        /** Dots two matrices and returns the outcome. Assumes Vectors.
         * @param a the first matrix
         * @param b the second matrix
         * @return the outcome matrix
         */
        public static double dotProduct(final Matrix a, final Matrix b){
            if (a.rows * a.cols !=  b.rows * b.cols)
                throw new UnsupportedMatrixOperation(a,b);
            double[] data = new double[a.rows*a.cols];
            Arrays.setAll(data, i->a.data[i] * b.data[i]);
            return DoubleStream.of(data).sum();
        }

        /** This is used for applying the Factory Pattern to identity matrices **/
        private static final HashMap<Integer,Matrix> identityCache = new HashMap<>();

        /** Returns an identity matrix of the given dimension
         * @param size the dimension of the matrix
         * @return an identity Matrix
         */
        public static Matrix identity(final int size){
            //factory the identity matrices so we don't make 1000 2x2 identities
            if (identityCache.containsKey(size)) return identityCache.get(size);
            double[] data = new double[size*size];
            Arrays.setAll(data,i->i%(size+1) == 0 ? 1 : 0);
            Matrix id = new Matrix(data,size,size,size*(size-1),size);
            identityCache.put(size,id);
            return id;
        }

        /** Scales a matrix and returns the outcome
         * @param a the first matrix
         * @param b the scalar
         * @return the outcome matrix
         */
        public static Matrix multiply(final Matrix a, final double b){
            double[] data = new double[a.rows*a.cols];
            Arrays.setAll(data, i->a.data[i] * b);
            return new Matrix(data,a.getRows(),a.getCols(),b != 0 ? a.isZero : 0,a.sum*b);
        }


        /** Composes two matrices and returns the outcome
         * @param a the first matrix
         * @param b the second matrix
         * @return the outcome matrix
         */
        public static Matrix multiply( final Matrix a, final Matrix b ){
            //FIXME: implement Strassen Algorithm for O(n^3)->O(n^2.3)
            //this is very slow
            if (a.cols != b.rows) throw new UnsupportedMatrixOperation(a,b);

            int rowsInA = a.rows;
            int columnsInARowsInB = a.cols;
            int columnsInB = b.cols;

            double[][] c = new double[rowsInA][columnsInB];
            for (int i = 0; i < rowsInA; i++){
                for (int j = 0; j < columnsInB; j++){
                    for (int k = 0; k < columnsInARowsInB; k++){
                        c[i][j] +=
                                a.get(i,k) *
                                        b.get(k,j);
                    }
                }
            }


            //FIXME: theorems about zero/sum?
            return new Matrix(c);
        }


        /** Subtracts two matrices and returns the outcome
         * @param a the first matrix
         * @param b the second matrix
         * @return the outcome matrix
         */
        public static Matrix subtract( final Matrix a, final Matrix b ){
            if (a.rows != b.rows || a.cols != b.cols)
                throw new UnsupportedMatrixOperation(a,b);
            double[] data = new double[a.rows*a.cols];
            Arrays.setAll(data, i->a.data[i] - b.data[i]);
            return new Matrix(data,a.getRows(),a.getCols(),-1,a.sum-b.sum);
        }

        /** Transposes a matrix and returns the transposition
         * @param input the matrix to transpose
         * @return a transposed copy of the matrix
         */
        public static Matrix transpose( final Matrix input ){
            //we're making a new matrix so memory efficiency doesn't matter
            double[] newData = new double[input.rows*input.cols];
            for (int j = 0; j < input.rows * input.cols; j ++){
                int r = j / input.cols;
                int c = j % input.cols;
                int tidx = input.rows * c + r;
                newData[tidx] = input.data[j];
            }
            return new Matrix(newData,input.cols,input.rows,input.isZero,input.sum);
        }

        /** Returns the vector length of the matrix
         * @param input the matrix
         * @return the vector length+
         */
        public static double vectorLength(final Matrix input){
            double count = 0;
            for (double aData : input.data) { count += (aData * aData); }
            return Math.sqrt(count);
        }

        /** Returns a new matrix without the given row, if possible.
         * @param input the matrix from which to delete
         * @param index the index of the row
         */
        public static Matrix deleteRow(Matrix input, int index){
            if (index < 0 || index >= input.rows) {
                String e = "Cannot delete "+index+"th row of " + input.dim() + " matrix";
                throw new UnsupportedMatrixOperation(input, e);
            }

            Matrix output = new Matrix(input.rows-1, input.cols);
            for (int i = 0; i < input.cols; i++){
                for (int j = 0; j < input.rows; j++){
                    if (j < index){
                        output.set(j,i,input.get(j,i));
                    }else if ( j > index)
                        output.set(j-1,i,input.get(j,i));
                }
            }
            return output;
        }

        /** Returns a new matrix without the given column, if possible.
         * @param input the matrix from which to delete
         * @param index the index of the column
         */
        public static Matrix deleteCol(Matrix input, int index){
            if (index < 0 || index >= input.cols) {
                String e = "Cannot delete "+index+"th column of " + input.dim() + " matrix";
                throw new UnsupportedMatrixOperation(input, e);
            }

            Matrix output = new Matrix(input.rows, input.cols-1);
            for (int i = 0; i < input.cols; i++){
                for (int j = 0; j < input.rows; j++){
                    if (i < index){
                        output.set(j,i,input.get(j,i));
                    }else if (i > index){
                        output.set(j,i-1,input.get(j,i));
                    }
                }
            }
            return output;
        }

        /** Copies deeply from the source matrix to the target matrix
         * @param source the source matrix
         * @param target the target matrix
         */
        public static void copy(Matrix source, Matrix target){
            if (target.rows != source.rows || target.cols != source.cols )
                throw new UnsupportedMatrixOperation(source,target);
            Arrays.setAll(target.data,i->source.data[i]);
            target.sum = source.sum;
            target.isZero = source.isZero;
        }

    }

    public static class UnsupportedMatrixOperation extends RuntimeException{
        public UnsupportedMatrixOperation(Matrix a, Matrix b){
            super("Unsupported operation between " +
                    a.getRows() + "x" + a.getCols() + " and " +
                    b.getRows() + "x" + b.getCols()
            );
        }

        public UnsupportedMatrixOperation(Matrix a, String s){
            super("Unsupported unary operation " + s + " on " +
                    a.getRows() + "x" + a.getCols() + " matrix"
            );
        }

        public UnsupportedMatrixOperation(Matrix a, int r, int c){
            super("Unsupported unary operation into entry ("+ r+","+c+") of " +
                    a.getRows() + "x" + a.getCols() + " matrix"
            );
        }
    }

    /** Returns a string representation of the Matrix **/
    @Override public String toString(){
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (int i = 0; i < rows*cols; i++){
            if (i == 0 ) stringBuilder.append("|");
            if (i > 0 && i % cols == 0) stringBuilder.append(" |\n|");
            stringBuilder.append("  ").append(data[i]);
        }
        return stringBuilder.append(" |").toString();
    }

    /** Returns a string representation of the dimensionality of the matrix **/
    public String dim(){return ""+rows+"x"+cols;}

    /** Returns the size of the matrix **/
    public int size(){ return data.length; }

    /** Randomizes the entries to between the given range
     * @param startvalue the lower limit
     * @param endvalue the upper limit
     */
    public void randomize(double startvalue, double endvalue){
        Arrays.setAll(data,i -> startvalue + (Math.random()*
                ((endvalue - startvalue) + 1)));
    }

    /** Returns a Matrix - only for testing purposes **/
    public static Matrix getTestMatrix(double[] newData, int rows, int cols,
                                       int isZero, double sum){
        return new Matrix(newData,rows,cols,isZero,sum);
    }

}

class MatrixInstantiationException extends RuntimeException{

    public MatrixInstantiationException(){
        super("Cannot instantiate undefined 0x0 Matrix");
    }

    public MatrixInstantiationException(int n, int m){
        super("Cannot instantiate misdefined "+n+"x"+m+" Matrix");
    }

    public MatrixInstantiationException(int n, int m, int l){
        super("Cannot instantiate misdefined "+n+"x"+m+" Matrix "+"of actual size "+ l);
    }

}
