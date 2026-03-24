public class GuitarHeroLite {

    //  Plays two guitar strings (middle C and concert A) when the user types
    //  the corresponding keys ('p' and 'v') in an interactive window.
    public static void main(String[] args) {

        // create two guitar strings, for concert A = A4 (440 Hz)
        // and middle C = C4 (about 261.63 Hz)
        double CONCERT_A = 440.0;
        double MIDDLE_C = CONCERT_A * Math.pow(2, -9.0/12.0);
        GuitarString concertA = new GuitarString(CONCERT_A);
        GuitarString middleC = new GuitarString(MIDDLE_C);

        // the main input loop
        Keyboard keyboard = new Keyboard();
        while (true) {

            // check if the user has played a key; if so, process it
            if (keyboard.hasNextKeyPlayed()) {

                // the key the user played
                char key = keyboard.nextKeyPlayed();

                // pluck the corresponding string
                if (key == 'p') { middleC.pluck(); }
                if (key == 'v') { concertA.pluck(); }
            }

            // compute the superposition of the samples
            double sample = middleC.sample() + concertA.sample();

            // play the sample on standard audio
            StdAudio.play(sample);

            // advance the simulation of each guitar string by one step
            middleC.tic();
            concertA.tic();
        }
    }

}
