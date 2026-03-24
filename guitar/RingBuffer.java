// The ring buffer models the medium (a string tied down at both ends) in which
// the energy travels back and forth. The length of the ring buffer determines
// the fundamental frequency of the resulting sound. Sonically, the feedback
// mechanism reinforces only the fundamental frequency and its harmonics
// (frequencies at integer multiples of the fundamental). The energy decay
// factor (0.996 in this case) models the slight dissipation in energy as the
// wave makes a round trip through the string.
public class RingBuffer {
    // stores the capacity of the ring buffer
    private int capacity;
    // stores number of items currently in the ring buffer
    private int size;
    // stores the index one beyond the most recently inserted item
    private int last;
    // stores the index of the least recently inserted item
    private int first;
    // stores the buffers
    private double[] buffer;

    // Creates an empty ring buffer with the specified capacity.
    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new double[capacity];
        this.size = 0;
    }

    // Returns the capacity of this ring buffer.
    public int capacity() {
        return capacity;
    }

    // Returns the number of items currently in this ring buffer.
    public int size() {
        return size;
    }

    // Is this ring buffer empty (size equals zero)?
    public boolean isEmpty() {
        return size == 0;
    }

    // Is this ring buffer full (size equals capacity)?
    public boolean isFull() {
        return size == capacity;
    }

    // Adds item x to the end of this ring buffer.
    public void enqueue(double x) {
        // checks if ring buffer is full
        if (size == capacity) {
            throw new IllegalStateException("RingBuffer is full");
        }
        // creates the cyclic wrap-around, switches last to zero if the last =
        // capacity
        if (last == capacity) {
            last = 0;
        }
        size++;
        buffer[last] = x;
        last++;

    }

    // Deletes and returns the item at the front of this ring buffer.
    public double dequeue() {
        // checks if the ring buffer is empty
        if (size == 0) {
            throw new IllegalStateException("RingBuffer is empty");
        }
        double firstOne = buffer[first];
        buffer[first] = 0;
        size--;
        first++;
        if (first == capacity) {
            first = 0;
        }
        return firstOne;
    }

    // Returns the item at the front of this ring buffer, throws an
    // error if the ring buffer is empty.
    public double peek() {
        if (isEmpty()) {
            throw new IllegalStateException("RingBuffer is empty");
        }
        return buffer[first];
    }

    // Tests this class by directly calling all instance methods.
    public static void main(String[] args) {
        // test for the constructor, based on command-line argument
        int n = Integer.parseInt(args[0]); // get the size of n from the command-line
        StdOut.printf("Test #0 - create a RingBuffer object with %d\n", n);
        RingBuffer buffer = new RingBuffer(n);

        // test for capacity()
        StdOut.printf("Test #1 - check capacity - should be %d\n", n);
        StdOut.printf("**** Capacity is %d\n", buffer.capacity());

        // test for size()
        StdOut.printf("Test #2 - check size - should be %d\n", 0);
        StdOut.printf("**** Size is %d\n", buffer.size());

        // test for isFull() and enqueue()
        StdOut.printf("Test #3 - perform %d enqueues: 1.0, 2.0, ...\n", n);
        StdOut.printf("**** isFull() should be false- is %b\n", buffer.isFull());
        for (int i = 1; i <= n; i++) { // enqueue n values:  1.0, 2.0, ...
            buffer.enqueue(i);
            StdOut.printf("Test #3.%d - check size after %d enqueues- should be %d\n",
                          i, i, i);
            StdOut.printf("**** Size is %d\n", buffer.size());
        }
        StdOut.printf("**** isFull() should be true- is %b\n", buffer.isFull());

        // test for isEmpty() and peek()
        StdOut.printf("Test #4 - check peek value == %.1f\n", 1.0);
        StdOut.printf("**** isEmpty() should be false- is %b\n", buffer.isEmpty());
        double val1 = buffer.peek();
        StdOut.printf("**** Value is %.1f\n", val1);

        // test for dequeue()
        double val2 = buffer.dequeue();
        StdOut.printf("Test #5 - dequeue a value, then check value == %.1f and "
                              + "size == %d after a dequeue\n", 1.0, n - 1);
        StdOut.printf("**** Value is %.1f\n", val2);
        StdOut.printf("**** Size is %d\n", buffer.size());
        for (int i = 0; i < n - 1; i++) buffer.dequeue(); // dequeue everything left
        StdOut.printf("**** remove %d items and isEmpty() should be true- is %b\n",
                      n - 1, buffer.isEmpty());

    }
}
