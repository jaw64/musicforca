package jaw64.ca.interpreter.musc1280;

import static jm.constants.Durations.SIXTEENTH_NOTE;
import static jm.constants.Pitches.AS4;
import static jm.constants.Pitches.AS5;
import static jm.constants.Pitches.B4;
import static jm.constants.Pitches.B5;
import static jm.constants.Pitches.CS4;
import static jm.constants.Pitches.CS5;
import static jm.constants.Pitches.CS6;
import static jm.constants.Pitches.DS5;
import static jm.constants.Pitches.DS6;
import static jm.constants.Pitches.ES4;
import static jm.constants.Pitches.ES5;
import static jm.constants.Pitches.ES6;
import static jm.constants.Pitches.FS4;
import static jm.constants.Pitches.FS5;
import static jm.constants.Pitches.FS6;
import static jm.constants.Pitches.GS4;
import static jm.constants.Pitches.GS5;
import jaw64.ca.CellGroup;
import jaw64.ca.CellGroupDimensions;
import jaw64.ca.CellularAutomaton;
import jaw64.ca.interpreter.Interpreter;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Write;

/**
 * Interprets binary cellular automata and uses it to generate a melody
 * based on the arpeggios in the introduction of Frank Liszt's <a
 * href="https://www.youtube.com/watch?v=CWN18ZoqzGs">Les jeux
 * d'eaux à la Villa d'Este</a>.
 * @author jaw64
 */
public class LisztIntroInterp implements Interpreter {

    /**
     * Defines the way that this interpreter will create music.
     * @author jaw64
     */
    public static enum Type {
        RANDOM, ASCENDING, DESCENDING
    }

    /**
     * The key signature used in the original song composition.
     */
    private static final int KEY_SIGNATURE = 6; // 6 sharps, F# major

    /**
     * First arpeggio ascending.
     */
    private static final int[] NOTES_A = { CS4, ES4, GS4, B4, CS5, ES5, GS5, B5, DS6 };

    /**
     * Second arpeggio ascending.
     */
    private static final int[] NOTES_B = { CS4, FS4, AS4, CS5, DS5, FS5, AS5, CS6, ES6 };

    /**
     * Third arpeggio ascending.
     */
    private static final int[] NOTES_C = { CS4, GS4, B4, DS5, ES5, GS5, B5, DS6, FS6 };

    /**
     * The duration of all notes to be generated.
     */
    private static final double NOTE_DURATION = SIXTEENTH_NOTE;

    /**
     * The number of cells required per iteration to correctly interpret using the
     * {@link Type#RANDOM} method.
     */
    private static final int REQUIRED_RANDOM_SIZE = 6;

    /**
     * The number of cells required per iteration to correctly interpret using the
     * {@link Type#ASCENDING} or {@link Type#DESCENDING} method.
     */
    private static final int REQUIRED_ASC_DESC_SIZE = 5;

    /**
     * The number of generations to generate.
     */
    private final int numGenerations;

    /**
     * The number of bits from the leftmost cell of the cell group to begin
     * interpreting bits as music.
     */
    private final int bitOffset;

    /**
     * The type of Liszt interpretation.
     */
    private final Type type;

    /**
     * The path to write the MIDI file to.
     */
    private final String path;

    /**
     * (constructor) Creates a CA interpreter (with 0 bit offset) used for
     * generating a very specific type of melody (see class definition for
     * more specifics).
     * @param numGenerations the number of generations for which to interpret a
     * melody
     */
    public LisztIntroInterp(int numGenerations) {
        this(numGenerations, 0, Type.RANDOM, "");
    }

    /**
     * (constructor) Creates a CA interpreter used for
     * generating a very specific type of melody (see class definition for
     * more specifics).
     * @param numGenerations the number of generations for which to interpret a
     * melody
     * @param bitOffset tells how far off from the leftmost cell of the CA to
     * look when interpreting cell groups
     */
    public LisztIntroInterp(int numGenerations, int bitOffset) {
        this(numGenerations, bitOffset, Type.RANDOM, "");
    }

    /**
     * (constructor) Creates a CA interpreter used for generating a specific
     * type of melody (see class definition for more specifics).
     * @param numGenerations the number of generations
     * @param bitOffset tells how far off from the leftmost cell of the CA to
     * look when interpreting cell groups
     * @param type the way to generate the melody / interpret the data
     */
    public LisztIntroInterp(int numGenerations, int bitOffset, Type type) {
        this(numGenerations, bitOffset, type, "");
    }

    /**
     * (constructor) Creates a CA interpreter used for generating a very specific
     * type of melody (see class definition for more specifics).
     * @param numGenerations the number of generations for which to interpret a
     * melody
     * @param bitOffset tells how far off from the leftmost cell of the CA to
     * look when interpreting cell groups
     * @param type the way to generate the melody / interpret the data
     * @param path the path to write the resulting MIDI file to
     */
    public LisztIntroInterp(int numGenerations, int bitOffset, Type type, String path) {
        this.numGenerations = Math.max(0, numGenerations);
        this.bitOffset = Math.max(0, bitOffset);
        this.type = type;
        this.path = path;
    }

    /**
     * Interprets the cellular automaton in a semi-random way.<br>
     * <br>
     * <strong> First two bits: </strong>
     * <ul>
     *   <li> Bits 1 and 2 - interpreted as a binary number (little endian) mapping to arpeggio type. </li>
     *   <ul>
     *     <li> values 0 and 1 represent NOTES_A </li>
     *     <li> value 2 represents NOTES_B </li>
     *     <li> value 3 represents NOTES_C </li>
     *   </ul>
     * </ul>
     * <strong>Last four bits:</strong>
     * <ul>
     *   <li> Bits 3 thru 6 - interpreted as binary number (little endian) mapping to arpeggio index. </li>
     *   <li> mappings are as follows </li>
     *   <ul>
     *     <li><pre> { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 } </pre></li>
     *     <li><pre> { |  |  |  |  |  |  |  |  |  |   |   |   |   |   |   | } </pre></li>
     *     <li><pre> { 0, 1, 2, 3, 4, 5, 6, 7, 8, 0,  1,  2,  3,  4,  5,  6 } </pre></li>
     *   </ul>
     * </ul>
     * 
     * @param ca the cellular automaton from which to interpret
     * @return the generated score
     */
    private Score randomInterp(CellularAutomaton ca) {
        CellGroupDimensions dim = ca.getDimensions();
        if (dim.getDimensionSize(0) < REQUIRED_RANDOM_SIZE + bitOffset) {
            throw new IllegalArgumentException(
                    String.format("LisztIntroInterp requires CA to be at least %d cells big.",
                            REQUIRED_RANDOM_SIZE));
        }
        // Beginning interpretation!
        final int[][] ARP_TYPE_MAP = { NOTES_A, NOTES_A, NOTES_B, NOTES_C };
        final int[] ARP_NOTE_MAP = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 1, 2, 3, 4, 5, 6 };
        final Note[] FINAL_NOTES = new Note[numGenerations];
        for (int i = 0; i < numGenerations; i++) {
            CellGroup cg = ca.getIteration(i);
            final int[] bits = new int[REQUIRED_RANDOM_SIZE];
            for (int j = 0; j < REQUIRED_RANDOM_SIZE; j++) {
                bits[j] = cg.getValue(j + bitOffset) % 2;
            }
            int arpTypeIndex = bits[0] + (bits[1] << 1);
            int arpNoteIndex = bits[2] + (bits[3] << 1) + (bits[4] << 2) + (bits[5] << 3);
            int[] arpType = ARP_TYPE_MAP[arpTypeIndex];
            int arpNote = arpType[ARP_NOTE_MAP[arpNoteIndex]];
            FINAL_NOTES[i] = new Note(arpNote, NOTE_DURATION);
        }
        return new Score(new Part(new Phrase(FINAL_NOTES)));
    }

    /**
     * Interprets the cellular automaton such that the notes either ascend or descend.<br>
     * <br>
     * <em> Before interpreting anything, first 3 bits are used to represent the
     * starting note from NOTES_A. </em>
     * <strong> First two bits: </strong>
     * <ul>
     *   <li> Bits 1 and 2 - interpreted as a binary number (little endian) mapping to arpeggio type. </li>
     *   <ul>
     *     <li> values 0 and 1 represent NOTES_A </li>
     *     <li> value 2 represents NOTES_B </li>
     *     <li> value 3 represents NOTES_C </li>
     *   </ul>
     * </ul>
     * <strong>Last two bits:</strong>
     * <ul>
     *   <li> Bits 3 thru 5 - interpreted as binary number (little endian) mapping to how much to
     *        skip to the next note. </li>
     *   <li> mappings are as follows </li>
     *   <ul>
     *     <li><pre> { 0, 1, 2, 3, 4, 5, 6, 7 } </pre></li>
     *     <li><pre> { |  |  |  |  |  |  |  | } </pre></li>
     *     <li><pre> { 1, 2, 3, 4, 1, 2, 2, 3 } </pre></li>
     *   </ul>
     * </ul>
     * 
     * @param ca the cellular automaton from which to interpret
     * @return the generated score
     */
    private Score ascDescInterp(CellularAutomaton ca) {
        CellGroupDimensions dim = ca.getDimensions();
        if (dim.getDimensionSize(0) < REQUIRED_ASC_DESC_SIZE + bitOffset) {
            throw new IllegalArgumentException(
                    String.format("LisztIntroInterp requires CA to be at least %d cells big.",
                            REQUIRED_ASC_DESC_SIZE));
        }
        // Beginning interpretation!
        final int[][] ARP_TYPE_MAP = { NOTES_A, NOTES_A, NOTES_B, NOTES_C };
        final int[] ARP_SKIP_MAP = { 1, 2, 3, 1, 1, 1, 2, 2 };
        final Note[] FINAL_NOTES = new Note[numGenerations];
        CellGroup cg = ca.getIteration(0);
        int[] ibits = { cg.getValue(bitOffset), cg.getValue(bitOffset + 1),
                cg.getValue(bitOffset + 2) }; // initial bits
        int currNoteIndex = (ibits[0] + (ibits[1] << 1) + (ibits[2] << 2)) % 9;
        for (int i = 0; i < numGenerations; i++) {
            cg = ca.getIteration(i);
            final int[] bits = new int[REQUIRED_ASC_DESC_SIZE];
            for (int j = 0; j < REQUIRED_ASC_DESC_SIZE; j++) {
                bits[j] = cg.getValue(j + bitOffset) % 2;
            }
            int arpTypeIndex = bits[0] + (bits[1] << 1);
            int arpSkipAmt = ARP_SKIP_MAP[bits[2] + (bits[3] << 1) + (bits[4] << 2)];
            int nextNoteIndex = type == Type.ASCENDING ? currNoteIndex + arpSkipAmt
                    : currNoteIndex - arpSkipAmt;
            nextNoteIndex = nextNoteIndex < 0 ? nextNoteIndex + 9 : nextNoteIndex % 9;
            int[] arpType = ARP_TYPE_MAP[arpTypeIndex];
            int arpNote = arpType[nextNoteIndex];
            FINAL_NOTES[i] = new Note(arpNote, NOTE_DURATION);
            currNoteIndex = nextNoteIndex;
        }
        return new Score(new Part(new Phrase(FINAL_NOTES)));
    }

    /*
     * (non-Javadoc)
     * @see jaw64.ca.interpreter.Interpreter#interpret(jaw64.ca.CellularAutomaton)
     */
    @Override
    public void interpret(CellularAutomaton ca) {
        // Error-checking.
        CellGroupDimensions dim = ca.getDimensions();
        if (dim.getNumDimensions() != 1) {
            throw new IllegalArgumentException("LisztIntroInterp requires 1D cellular automaton.");
        }
        Score myScore = null;
        switch (type) {
        case RANDOM: {
            myScore = randomInterp(ca);
            break;
        }
        case ASCENDING:
        case DESCENDING: {
            myScore = ascDescInterp(ca);
            break;
        }
        default:
            throw new IllegalArgumentException("LisztIntroInterp received invalid type.");
        }
        myScore.setKeySignature(KEY_SIGNATURE);
        myScore.setTimeSignature(4, 4);
        myScore.setTempo(120);
        // View.notation(myScore);
        if (path == null || path.isEmpty()) {
            Write.midi(myScore);
        }
        else {
            Write.midi(myScore, path);
        }
    }
}
