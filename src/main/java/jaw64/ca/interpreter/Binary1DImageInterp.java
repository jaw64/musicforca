package jaw64.ca.interpreter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import jaw64.ca.CellGroup;
import jaw64.ca.CellularAutomaton;

/**
 * Interpreter for Wolfram's "elementary" binary 1D cellular automata
 * that outputs the CA as a black and white image (by default).
 * @author jaw64
 */
public class Binary1DImageInterp implements Interpreter {

    /**
     * The path of the output image generated.
     */
    private final String imgPath;

    /**
     * The number of the generations to generate given a cellular automata.
     */
    private final int numGenerations;

    /**
     * The color used for when there is a zero value in a cell.
     */
    private Color zeroColor;

    /**
     * The color used for when there is a one value in a cell.
     */
    private Color oneColor;

    /**
     * (constructor) Creates a new interpreter for binary cellular automata.
     * @param imgPath the path of the output image
     * @param numGenerations the number of generations
     */
    public Binary1DImageInterp(String imgPath, int numGenerations) {
        this.imgPath = imgPath;
        this.numGenerations = Math.max(1, numGenerations);
        this.zeroColor = Color.WHITE;
        this.oneColor = Color.BLACK;
    }
    
    /**
     * Gets the color being used for when a cell value is zero.
     * @return the zero color
     */
    public Color getZeroColor() {
        return zeroColor;
    }
    
    /**
     * Sets the color to use for when a cell value is zero.
     * @param c the color to set
     */
    public void setZeroColor(Color c) {
        zeroColor = c;
    }

    /**
     * Gets the color being used for when a cell value is one.
     * @return the one color
     */
    public Color getOneColor() {
        return oneColor;
    }
    
    /**
     * Sets the color to use for when a cell value is one.
     * @param c the color to set
     */
    public void setOneColor(Color c) {
        oneColor = c;
    }
    
    /*
     * (non-Javadoc)
     * @see jaw64.ca.interpreter.Interpreter#interpret(jaw64.ca.CellularAutomaton)
     */
    @Override
    public void interpret(CellularAutomaton ca) {
        if (ca.getDimensions().getNumDimensions() != 1) {
            throw new IllegalArgumentException("Cellular automaton must be 1-dimensional.");
        }
        int groupWidth = ca.getDimensions().getDimensionSize(0);
        BufferedImage image;
        if (zeroColor.equals(Color.WHITE) && oneColor.equals(Color.BLACK)) {
            image = new BufferedImage(groupWidth, numGenerations, BufferedImage.TYPE_BYTE_BINARY);
        }
        else {
            image = new BufferedImage(groupWidth, numGenerations, BufferedImage.TYPE_INT_RGB);
        }
        for (int r = 0; r < numGenerations; r++) {
            CellGroup iter = ca.getIteration(r);
            for (int c = 0; c < iter.getNumElements(); c++) {
                int ival = iter.getValue(c) % 2;
                image.setRGB(c, r, ival == 0 ? zeroColor.getRGB() : oneColor.getRGB());
            }
        }
        File output = new File(imgPath);
        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
