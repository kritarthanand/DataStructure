import java.math.BigInteger;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

class FenwickTree<T> {
    /*
     * @author = Kritarth Anand (kritarth.anand@gmail.com)
     * Fenwick Tree (http://en.wikipedia.org/wiki/Fenwick_tree) is a
     * data structure useful for providing efficient method for calculation
     * of prefix sums in log(N) order
     * 
     * [ 2, 3, 353, 35 , 2 , 2, 233 ]
     * 
     * sum(1, 4) = 3 + 353 + 35 + 2 = 393
     * 
     * This is an implementation of Fenwick Tree for generic type.
     * The function is not restricted to sum only, you can define operator
     * using lambda functions. Examples in main class
     */

    /* Binary Index tree array */
    private final T[] fenwickTree;
    /* Orignal data array */
    private final T[] dataArray;
    /*
     * operator that needs to be cumulative,
     * for instance
     * '+' :incase summation of series
     * '*' :incase of multiplication of series
     */
    private final BinaryOperator<T> forwardOperator;
    /*
     * inverse operator, like '-' for '+'
     */
    private final BinaryOperator<T> inverseOperator;
    /* identity element related with the operator */
    private final Supplier<T> identity;

    FenwickTree(int N, BinaryOperator<T> forwardOperator, BinaryOperator<T> inverseOperator, Supplier<T> identity) {
        fenwickTree = (T[]) new Object[N];
        dataArray = (T[]) new Object[N];
        this.forwardOperator = forwardOperator;
        this.inverseOperator = inverseOperator;
        this.identity = identity;
        for (int i = 0; i < fenwickTree.length; i++) {
            fenwickTree[i] = identity.get();
            dataArray[i] = identity.get();
        }
    }

    private void addElementIndex(T value, int index) {
        dataArray[index] = value;
        for (; index < fenwickTree.length; index |= index + 1) {
            fenwickTree[index] = forwardOperator.apply(fenwickTree[index], value);
        }
    }

    private void removeIndex(int index) {
        T value = dataArray[index];
        dataArray[index] = identity.get();
        for (; index < fenwickTree.length; index |= index + 1) {
            fenwickTree[index] = inverseOperator.apply(fenwickTree[index], value);
        }
    }

    /*
     * @params value: value that needs to updated
     * index: index at which value to updated
     * 
     * Change the value at the specified index to value
     */
    public void updateValueAtIndex(T value, int index) {
        removeIndex(index);
        addElementIndex(value, index);
    }

    /*
     * @params index ending index
     * 
     * returns cumulative operation from [0...index]
     */
    public T cumulativeSum(int index) {
        T identityElem = identity.get();
        for (; index >= 0; index = (index & (index + 1)) - 1)
            identityElem = forwardOperator.apply(identityElem, fenwickTree[index]);
        return identityElem;
    }

    /*
     * @params startIndex : starting index
     * endIndex: ending index
     * returns cumulative operation from [startIndex...endIndex]
     */
    public T cumulativeSeries(int startIndex, int endIndex) {
        T endValue = cumulativeSum(endIndex);
        T startValue = cumulativeSum(startIndex - 1);
        return inverseOperator.apply(endValue, startValue);
    }

    public static void main(String argv[]) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        FenwickTree<Integer> sumFTree =
                new FenwickTree<Integer>(
                        arr.length,
                        (p, q) -> (p + q),
                        (p, q) -> (p - q),
                        () -> 0
                );
        FenwickTree<Long> multiplicationFTree =
                new FenwickTree<Long>(
                        arr.length,
                        (p, q) -> (p * q),
                        (p, q) -> (p / q),
                        () -> 1L
                );
        FenwickTree<Integer> sumMod15FTree =
                new FenwickTree<Integer>(
                        arr.length,
                        (p, q) -> (p + q) % 15,
                        (p, q) -> (p - q + 15) % 15,
                        () -> 0
                );
        FenwickTree<BigInteger> bigIntegerFTree =
                new FenwickTree<BigInteger>(
                        arr.length,
                        (p, q) -> (p.add(q)),
                        (p, q) -> (p.subtract(q)),
                        () -> BigInteger.ZERO
                );
        for (int i = 0; i < arr.length; i++) {
            sumFTree.updateValueAtIndex(arr[i], i);
            multiplicationFTree.updateValueAtIndex((long) arr[i], i);
            sumMod15FTree.updateValueAtIndex(arr[i], i);
            bigIntegerFTree.updateValueAtIndex(BigInteger.valueOf(arr[i]), i);
        }
        System.out.println(sumFTree.cumulativeSeries(0, 2));// [ 1 +2 + 3] --- 6
        System.out.println(sumFTree.cumulativeSeries(6, 8));// [ 7+ 8 + 9] --- 24
        System.out.println(multiplicationFTree.cumulativeSeries(0, 4));// [ 1*2*3*4*5] --- 120
        System.out.println(sumMod15FTree.cumulativeSeries(0, 5));// [ 1+2+3+4+5+6] --- 6
        System.out.println(bigIntegerFTree.cumulativeSeries(6, 8));// [ 7+ 8 + 9] --- 24

    }
    }


