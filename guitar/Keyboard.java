import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public class Keyboard {
    // piano key colors
    private static final boolean WHITE_KEY = false;
    private static final boolean BLACK_KEY = true;

    private static final Color BOOK_BLUE = new Color(9, 90, 166);

    // initial font sizes
    private static final int DEFAULT_FONT_SIZE_BLACK_KEY = 16;
    private static final int DEFAULT_FONT_SIZE_WHITE_KEY = 18;
    private static final int DEFAULT_FONT_SIZE_KEYSTROKE = 14;

    // reserve space above and below for keystroke labels
    private static final int TOP_MARGIN = 40;
    private static final int BOTTOM_MARGIN = 40;


    // initial width and height
    private final int initialWidth;
    private final int initialHeight;

    // white and black keys
    private LinkedList<Key> blackKeys = new LinkedList<Key>();
    private LinkedList<Key> whiteKeys = new LinkedList<Key>();

    // for synchronization
    private final Object mouseLock = new Object();
    private final Object keyLock = new Object();

    // queue of typed keys (yet to be processed by client)
    private LinkedList<Character> keysTyped = new LinkedList<Character>();

    // set of key characters currently pressed down
    private TreeSet<Character> keysDown = new TreeSet<Character>();

    // Key that is being clicked (null if no such key)
    private Key mouseKey = null;

    // default 37-key keyboard
    public Keyboard() {
        this("q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ", "A");
    }

    // custom keyboard of arbitrary size
    private Keyboard(String keyboardString, String firstWhiteKey) {

        // determine offset
        String[] whiteKeyNames = { "A", "B", "C", "D", "E", "F", "G" };
        int offset = 0;
        while (!whiteKeyNames[offset].equals(firstWhiteKey)) {
            offset++;
        }

        // create the white and black keys
        for (int i = 0; i < keyboardString.length(); i++) {

            // next key is white
            String whiteKeyName = whiteKeyNames[(offset + whiteKeys.size()) % 7];
            Key whiteKey = new Key(whiteKeys.size(), whiteKeyName, keyboardString.charAt(i),
                                   WHITE_KEY);
            whiteKeys.add(whiteKey);

            // next key is black (black keys occur immediately after A, C, D, F, and G)
            if ("ACDFG".contains(whiteKeyName)) {
                i++;
                if (i >= keyboardString.length()) break;
                String blackKeyName = whiteKeyName + "#";
                Key blackKey = new Key(whiteKeys.size(), blackKeyName, keyboardString.charAt(i),
                                       BLACK_KEY);
                blackKeys.add(blackKey);
            }
        }

        // reasonable values for initial dimensions
        initialWidth = 50 * whiteKeys.size();
        initialHeight = 300;

        // create and show the GUI (in the event-dispatching thread)
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new KeyboardPanel();
            panel.setPreferredSize(new Dimension(initialWidth, initialHeight));

            JFrame frame = new JFrame(keyboardString.length() + "-Key Keyboard");
            frame.setMinimumSize(new Dimension(initialWidth / 4, initialHeight / 4));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);   // center on screen
            frame.setVisible(true);
        });
    }

    /**
     * Returns the next key that was typed by the user (that the client has not already processed).
     * This method should be preceded by a call to {@link #hasNextKeyPlayed()} to ensure that there
     * is a next key to process. This method returns a lowercase character corresponding to the key
     * typed (such as {@code 'a'} or {@code '@'}).
     *
     * @return the next key typed by the user (that your program has not already processed).
     * @throws NoSuchElementException if there is no remaining key
     */
    public char nextKeyPlayed() {
        synchronized (keyLock) {
            if (keysTyped.isEmpty()) {
                throw new NoSuchElementException(
                        "your program has already processed all typed keys");
            }
            return keysTyped.removeLast();
        }
    }

    public boolean hasNextKeyPlayed() {
        synchronized (keyLock) {
            return !keysTyped.isEmpty();
        }
    }

    // the JPanel for drawing the keyboard
    private class KeyboardPanel extends JPanel implements MouseListener, KeyListener {
        public static final long serialVersionUID = 12558137269921L;

        public KeyboardPanel() {
            setBackground(Color.WHITE);
            addMouseListener(this);
            addKeyListener(this);
            setFocusable(true);
        }

        // draw the keyboard
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Dimension size = getSize();
            double width = size.getWidth();
            double height = size.getHeight();

            // first, draw the white keys
            for (Key whiteKey : whiteKeys) {

                // mouse click or key typed
                if ((whiteKey == mouseKey) || keysDown.contains(whiteKey.getKeyStroke())) {
                    whiteKey.draw(g, width, height, BOOK_BLUE, Color.WHITE, true);
                }
                else {
                    whiteKey.draw(g, width, height, Color.WHITE, Color.BLACK, false);
                }
            }

            // then, draw the black keys
            for (Key blackKey : blackKeys) {

                // mouse click or key typed
                if ((blackKey == mouseKey) || keysDown.contains(blackKey.getKeyStroke())) {
                    blackKey.draw(g, width, height, BOOK_BLUE, Color.WHITE, true);
                }

                // draw as usual
                else {
                    blackKey.draw(g, width, height, Color.BLACK, Color.LIGHT_GRAY, false);
                }
            }
        }

        /***************************************************************************
         *  Mouse events.
         ***************************************************************************/

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            synchronized (mouseLock) {
                Dimension size = getSize();
                double width = size.getWidth();
                double height = size.getHeight();
                double mouseX = e.getX() / width * whiteKeys.size();
                double mouseY = e.getY() / height;
                // System.out.printf("mouse = (%s, %s)\n", mouseX, mouseY);

                // check black keys first
                for (Key blackKey : blackKeys) {
                    if (blackKey.contains(mouseX, mouseY)) {
                        mouseKey = blackKey;
                        char c = blackKey.getKeyStroke();
                        keysTyped.addFirst(c);
                        repaint();
                        return;
                    }
                }

                // next, check white keys
                for (Key whiteKey : whiteKeys) {
                    if (whiteKey.contains(mouseX, mouseY)) {
                        mouseKey = whiteKey;
                        char c = whiteKey.getKeyStroke();
                        keysTyped.addFirst(c);
                        repaint();
                        return;
                    }
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            synchronized (mouseLock) {
                mouseKey = null;
                repaint();
            }
        }


        /***************************************************************************
         *  Keyboard events.
         ***************************************************************************/

        public void keyTyped(KeyEvent e) {
            synchronized (keyLock) {
                char c = Character.toLowerCase(e.getKeyChar());
                keysTyped.addFirst(c);
            }
        }

        public void keyPressed(KeyEvent e) {
            synchronized (keyLock) {
                char c = Character.toLowerCase(e.getKeyChar());
                keysDown.add(c);
                repaint();
            }
        }

        public void keyReleased(KeyEvent e) {
            synchronized (keyLock) {
                char c = Character.toLowerCase(e.getKeyChar());
                keysDown.remove(c);
                repaint();
            }
        }
    }


    /***************************************************************************
     *  Helper data type to represent individual white or black keys.
     ***************************************************************************/
    private Font getFont(int defaultFontSize, double width, double height) {
        int size = (int) (width * defaultFontSize / initialWidth);
        if (height < initialHeight / 2.0) size = 0;
        if (width < initialWidth / 2.0) size = 0;
        return new Font("SansSerif", Font.PLAIN, size);
    }

    private class Key {
        private final String name;        // key name (e.g., C)
        private final boolean isBlack;    // is it a black key?
        private final char keyStroke;     // keyboard keystroke that correspond to piano key

        // rectangle for key
        // (coordinate system is scaled so that white keys have width and height 1.0)
        private final double xmin, xmax, ymin, ymax;


        public Key(double x, String name, char keyStroke, boolean isBlack) {
            this.name = name;
            this.keyStroke = keyStroke;
            this.isBlack = isBlack;

            if (!isBlack) {
                xmin = x;
                xmax = x + 1;
                ymin = 0.0;
                ymax = 1.0;
            }
            else {
                xmin = x - 0.3;
                xmax = x + 0.3;
                ymin = 0.0;
                ymax = 0.6;
            }
        }

        // draw the key using the given background and foreground colors
        private void draw(Graphics2D g, double width, double height,
                          Color backgroundColor, Color foregroundColor, boolean pressed) {

            // Compute available area for actual keyboard
            double keyboardHeight = height - TOP_MARGIN - BOTTOM_MARGIN;
            double SCALE_X = width / whiteKeys.size();
            double SCALE_Y = keyboardHeight;

            // Draw rectangle for key, shifted down by TOP_MARGIN
            Rectangle2D.Double rectangle = new Rectangle2D.Double(
                xmin * SCALE_X,
                TOP_MARGIN + ymin * SCALE_Y,
                (xmax - xmin) * SCALE_X,
                (ymax - ymin) * SCALE_Y
            );

            // fill background
            g.setColor(backgroundColor);
            g.fill(rectangle);

            // draw outline
            g.setColor(Color.BLACK);
            g.draw(rectangle);


            // hack to make note name A4 or C4 for special notes
            String noteLabel = name;
            if (noteLabel.equals("A") && keyStroke == 'v') noteLabel = "A4";
            if (noteLabel.equals("C") && keyStroke == 'p') noteLabel = "C4";

            // font setup
            int fontSize;
            if (isBlack) {
                fontSize = DEFAULT_FONT_SIZE_BLACK_KEY;
            }
            else {
                fontSize = DEFAULT_FONT_SIZE_WHITE_KEY;
            }
            g.setFont(getFont(fontSize, width, height));
            FontMetrics metrics = g.getFontMetrics();
            int hs = metrics.getHeight();
            int ws = metrics.stringWidth(noteLabel);

            // draw note name inside the key
            float noteX = (float) ((xmin + xmax) / 2.0 * SCALE_X - ws / 2.0);
            float noteY;
            if (isBlack) {
                noteY = (float) (TOP_MARGIN + 0.4 * SCALE_Y);
            }
            else {
                noteY = (float) (TOP_MARGIN + 0.85 * SCALE_Y);
            }
            g.setColor(foregroundColor);
            g.drawString(noteLabel, noteX, noteY);


            // draw keystroke labels above black keys and below white keys
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(getFont(DEFAULT_FONT_SIZE_KEYSTROKE, width, height));
            String keyLabel = Character.toString(keyStroke);
            ws = metrics.stringWidth(keyLabel);
            float keyX = (float) ((xmin + xmax) / 2.0 * SCALE_X - ws / 2.0);
            if (isBlack) {
                g.drawString(keyLabel, keyX, (float) (TOP_MARGIN * 0.6));
            }
            else {
                g.drawString(keyLabel, keyX, (float) (TOP_MARGIN + SCALE_Y + BOTTOM_MARGIN * 0.6));
            }
        }





        // the computer keyboard keystroke corresponding to this piano key
        private char getKeyStroke() {
            return keyStroke;
        }

        // does the rectangle contain the given (x, y)
        private boolean contains(double x, double y) {
            return x >= xmin && x < xmax && y >= ymin && y < ymax;
        }

        public String toString() {
            return String
                    .format("%-2s: [%4.1f, %4.1f] x [%2.1f, %2.1f]", name, xmin, xmax, ymin, ymax);
        }
    }


    public static void main(String[] args) {
        Keyboard keyboard = new Keyboard();
        // Keyboard keyboard = new Keyboard("q2we4r5ty", "A");

    }

}
