package jaw64.ca.app;

import java.awt.Color;
import java.util.Random;
import jaw64.ca.CellGroup;
import jaw64.ca.CellularAutomaton;
import jaw64.ca.DefaultCellularAutomaton;
import jaw64.ca.interpreter.Binary1DImageInterp;
import jaw64.ca.interpreter.musc1280.LisztIntroInterp;
import jaw64.ca.interpreter.musc1280.LisztIntroInterp.Type;
import jaw64.ca.rules.Binary1DRule;
import jaw64.ca.rules.Rule;

/**
 * Cellular automaton-based music composition application.
 * @author jaw64
 */
public class CellAutoApplication {

    /**
     * (constructor) Creates a new CellAutoApplication.
     */
    CellAutoApplication() {}

    /**
     * Runs the application.
     * @return the exit code for the application.
     */
    public int run() {
        final int GROUP_WIDTH = 960;
        final int NUM_GENERATIONS = 540;
        final int SEED = 5513; // ca5513
        CellGroup initialGroup = new CellGroup(GROUP_WIDTH);
        Rule rule = new Binary1DRule(105);
        Random rand = new Random(SEED);
        for (int i = 0; i < GROUP_WIDTH; i++) {
            initialGroup.setValue(Math.abs(rand.nextInt() % 2), i);
        }
        CellularAutomaton ca = new DefaultCellularAutomaton(initialGroup, rule);
        Binary1DImageInterp bin1d = new Binary1DImageInterp("CATest.png", NUM_GENERATIONS);
        LisztIntroInterp liszt = new LisztIntroInterp(128, 256, Type.ASCENDING);
        bin1d.setZeroColor(Color.RED);
        bin1d.setOneColor(Color.BLUE);
        bin1d.interpret(ca);
        liszt.interpret(ca);
        return 0;
    }

    /**
     * Entry point for the CellAutoApplication class.
     * @param argv command line arguments
     */
    public static void main(String[] argv) {
        try {
            System.exit(new CellAutoApplication().run());
        } catch (Exception e) {
            System.err.println("[ERROR]: Exception bubbled up to application "
                    + "level. Exiting with errno 1 & printing stacktrace.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
