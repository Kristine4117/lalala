// Creates a data type that models a vibrating guitar string. It is a
// program to simulate plucking a guitar string using the Karplus–Strong
// algorithm. This algorithm played a seminal role in the emergence of
// physically modeled sound synthesis (where a physical description of a
// musical instrument is used to synthesize sound electronically).
// When a guitar string is plucked, the string vibrates and creates sound.
// The length of the string determines its fundamental frequency of vibration.
// We model a guitar string by sampling its displacement
// (a real number between –½ and +½) at
//  n equally spaced points in time. The integer n
//  equals the sampling rate (44,100 Hz) divided by the desired fundamental
//  frequency, rounded up to the nearest integer.
public class GuitarString {
    // instance variables
    // number provided by assignment 7 specification for sampling rate
    private static final int SAMPLING_RATE = 44100;
    // number provided by assignment 7 specification for the decay factor
    private static final double DECAY_FACTOR = 0.996;
    // number of samples for ring buffer
    private int n;
    // stores the ring buffer
    private RingBuffer ringBuffer;


    // Creates a guitar string of the specified frequency,
    // using a sampling rate of 44,100, where all samples are
    // initially set to 0.0.
    public GuitarString(double frequency) {
        // desired sample size for ring buffer formula from
        // assignment 7 specification
        this.n = (int) Math.ceil(SAMPLING_RATE / frequency);
        this.ringBuffer = new RingBuffer(n);
        for (int i = 0; i < n; i++) {
            ringBuffer.enqueue(0.0);
        }
    }

    // Creates a guitar string whose length and initial values
    // are given by the specified array.
    public GuitarString(double[] init) {
        this.n = init.length;
        this.ringBuffer = new RingBuffer(n);
        for (int i = 0; i < n; i++) {
            ringBuffer.enqueue(init[i]);
        }
    }

    // Returns the number of samples in the ring buffer.
    public int length() {
        return ringBuffer.size();
    }

    // Returns the current sample.
    public double sample() {
        return ringBuffer.peek();
    }


    // Plucks this guitar string by replacing the ring buffer with white noise.
    public void pluck() {
        for (int i = 0; i < n; i++) {
            ringBuffer.dequeue();
        }
        for (int i = 0; i < n; i++) {
            ringBuffer.enqueue(StdRandom.uniformDouble(-0.5, 0.5));
        }
    }

    // Advances the Karplus-Strong simulation one time step.
    public void tic() {
        double firstOne = ringBuffer.dequeue();
        // the averaging operation provided by assignment 7 specification
        double decayed = DECAY_FACTOR * ((firstOne + ringBuffer.peek()) * 0.5);
        ringBuffer.enqueue(decayed);

    }

    // Tests this class by directly calling both constructors
    // and all instance methods.
    public static void main(String[] args) {
        // get frequency from command-line
        double freq = Double.parseDouble(args[0]);


        // create a GuitarString object with given frequency
        StdOut.printf("Test #0 - create GuitarString obj with frequency %f\n", freq);
        GuitarString gs1 = new GuitarString(freq);

        // check length (sampling rate/frequency)
        StdOut.printf("Test #1 - check length based on frequency %f\n", freq);
        StdOut.printf("**** Length is %d\n", gs1.length());

        // get one sample
        StdOut.printf("Test #2 - check sample is %.1f\n", 0.0);
        StdOut.printf("**** Sample is %.1f\n", gs1.sample());

        // create a simple GuitarString with 4 samples
        double[] samples = { -0.7, +0.8, -0.9, +0.6 };
        GuitarString gs2 = new GuitarString(samples);
        int len = samples.length;
        StdOut.printf("Test #3 - check length based on given samples == %d\n",
                      len);
        StdOut.printf("**** Length is %d\n", gs2.length());

        // get a sample
        StdOut.printf("Test #4 - check sample is %.2f\n", samples[0]);
        StdOut.printf("**** Sample is %.2f\n", gs2.sample());

        // now pluck and check length
        gs2.pluck();
        StdOut.printf("Test #5 - check length after pluck is still == %d\n",
                      len);
        StdOut.printf("**** Length is %d\n", gs2.length());

        // check that a random sample is  [-0.5, +0.5)
        StdOut.printf("Test #6 - check sample is range [-0.5, +0.5)\n");
        StdOut.printf("**** Sample is %.2f\n", gs2.sample());

        // test tic and sample
        GuitarString gs3 = new GuitarString(samples);
        StdOut.printf("Test #7 - check sample is %.2f\n", samples[0]);
        StdOut.printf("**** Sample is %.1f\n", gs3.sample());
        gs3.tic();
        StdOut.printf("Test #8 - check sample is %.2f\n", samples[1]);
        StdOut.printf("**** Sample is %.2f\n", gs3.sample());

        // test 25 tics
        int m = 25; // number of tics
        double[] moreSamples =
                { 0.2, 0.4, 0.5, 0.3, -0.2, 0.4, 0.3, 0.0, -0.1, -0.3 };
        StdOut.printf("Test #9 - test %d tics\n", m);
        GuitarString gs4 = new GuitarString(moreSamples);
        for (int i = 0; i < m; i++) {
            double sample = gs4.sample();
            StdOut.printf("%6d %8.4f\n", i, sample);
            gs4.tic();
        }
    }
}
