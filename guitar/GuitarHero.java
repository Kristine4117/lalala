// GuitarHero is an interactive guitar player. GuitarHero.java is a GuitarString
// client that plays the guitar in real time. It relies on a helper class
// Keyboard.java that provides a graphical user interface (GUI) to play notes
// using the keyboard. When a user types one of the keys
// "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ", the program plucks the guitar string
// corresponding to a note. Since the combined result of several sound waves is
// the superposition of the individual sound waves, it plays the sum of the
// string samples.
public class GuitarHero {
    //  Plays guitar strings when the user types the corresponding keys
    //  ("q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ") in an interactive window.
    public static void main(String[] args) {
        String keyboardString = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        int length = keyboardString.length();
        // variable for concert A = A4 (440 Hz)
        double CONCERT_A = 440.0;
        // creates an array of guitarStrings
        GuitarString[] guitarString = new GuitarString[length];
        for (int i = 0; i < length; i++) {
            // formula for calculating the frequency provided by
            // assignment 7 specification
            double frequency = CONCERT_A * Math.pow(2, (i - 24) / 12.0);
            guitarString[i] = new GuitarString(frequency);
        }

        // the main input loop
        Keyboard keyboard = new Keyboard();
        while (true) {
            // check if the user has played a key; if so, process it
            if (keyboard.hasNextKeyPlayed()) {

                // the key the user played
                char key = keyboard.nextKeyPlayed();

                // pluck the corresponding string
                int index = keyboardString.indexOf(key);
                if (index != -1) {
                    guitarString[index].pluck();
                }
            }

            // compute the superposition of the samples
            double sample = 0.0;
            for (int i = 0; i < length; i++) {
                sample += guitarString[i].sample();
            }

            // play the sample on standard audio
            StdAudio.play(sample);

            // advance the simulation of each guitar string by one step
            for (int i = 0; i < length; i++) {
                guitarString[i].tic();
            }
        }
    }
}


