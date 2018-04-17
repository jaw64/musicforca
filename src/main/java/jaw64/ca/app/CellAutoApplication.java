package jaw64.ca.app;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import jaw64.ca.CellGroup;
import jaw64.ca.CellularAutomaton;
import jaw64.ca.DefaultCellularAutomaton;
import jaw64.ca.rules.Binary1DRule;
import jaw64.ca.rules.Rule;

/**
 * Cellular automaton-based music composition application.
 * @author jaw64
 */
public class CellAutoApplication {

    /**
     * Array of command line arguments.
     */
    private String[] argv;

    /**
     * (constructor) Creates a new CellAutoApplication.
     * @param argv command line arguments
     */
    CellAutoApplication(String[] argv) {
        this.argv = argv;
    }

    /**
     * Runs the application.
     * @return the exit code for the application.
     */
    public int run() {
        final int GROUP_WIDTH = 960;
        final int NUM_GENERATIONS = 540;
        CellGroup initialGroup = new CellGroup(GROUP_WIDTH);
        Rule rule = new Binary1DRule(105);
        Random rand = new Random(1234);
        for (int i = 0; i < GROUP_WIDTH; i++) {
            initialGroup.setValue(Math.abs(rand.nextInt() % 256), i);
        }
        CellularAutomaton ca = new DefaultCellularAutomaton(initialGroup, rule);
        BufferedImage image = new BufferedImage(GROUP_WIDTH * 2, NUM_GENERATIONS * 2,
                BufferedImage.TYPE_BYTE_BINARY);
        for (int r = 0; r < NUM_GENERATIONS; r++) {
            CellGroup iter = ca.getIteration(r);
            for (int c = 0; c < iter.getNumElements(); c++) {
                int rr = r * 2;
                int rc = c * 2;
                int ival = (iter.getValue(c) + 1) % 2;
                int color = ival == 0 ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
                image.setRGB(rc, rr, color);
                image.setRGB(rc, rr + 1, color);
                image.setRGB(rc + 1, rr, color);
                image.setRGB(rc + 1, rr + 1, color);
            }
        }
        File output = new File("CATest.png");
        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Entry point for the CellAutoApplication class.
     * @param argv command line arguments
     */
    public static void main(String[] argv) {
        try {
            System.exit(new CellAutoApplication(argv).run());
        } catch (Exception e) {
            System.err.println("[ERROR]: Exception bubbled up to application "
                    + "level. Exiting with errno 1 & printing stacktrace.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
