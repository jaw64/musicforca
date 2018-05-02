package jaw64.ca.interpreter.musc1280;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jaw64.ca.CellGroup;
import jaw64.ca.CellGroupDimensions;
import jaw64.ca.CellularAutomaton;
import jaw64.ca.interpreter.Interpreter;

/**
 * Interprets binary cellular automata and uses it to generate a
 * jumbled mess of Michael Jackson choruses (designed to work
 * specifically with the synced tracks I have created and placed in
 * the resource directory).
 * @author jaw64
 */
public class MikeyJInterp implements Interpreter {

    /**
     * Shows all of the songs that can be sampled.
     * @author jaw64
     */
    public static enum Song {
        BAD(1),
        BEAT_IT(1),
        BILLIE_JEAN(1),
        BLACK_OR_WHITE(1),
        SMOOTH_CRIMINAL(2),
        MAN_IN_THE_MIRROR(1),
        THRILLER(1),
        MAKE_ME_FEEL(1);

        public final int numSamples;

        private Song(int numSamples) {
            this.numSamples = numSamples;
        }
    }

    /**
     * Puts all the song enums in a convenient array for indexing.
     */
    private static final Song[] SONG_MAP = Song.values();

    /**
     * Maps 0 thru 7 to a duration (in beats).
     */
    private static final int[] DURATION_MAP = { 1, 2, 2, 3, 4, 4, 6, 8 };

    /**
     * The number of cells required per iteration to correctly interpret.
     */
    private static final int REQUIRED_SIZE = 13;

    /**
     * The size of all samples in beats (or in this case, in 8th notes).
     */
    private static final int SAMPLE_SIZE = 64;

    /**
     * The length of an individual beat (in seconds).
     */
    private static final double BEAT_DURATION = 0.2503125;

    /**
     * The minimum duration of song to generate (in seconds).
     */
    private final double minDuration;

    /**
     * The number of bits from the leftmost cell of the cell group to begin
     * interpreting bits as music.
     */
    private final int bitOffset;

    /**
     * The path to write the MIDI file to.
     */
    private final String path;

    /**
     * (constructor) Creates a CA interpreter (with 0 bit offset) used for
     * generating MJ sample mashups.
     * @param minDuration the minimum duration of the song to generate (in seconds)
     */
    public MikeyJInterp(double minDuration) {
        this(minDuration, 0, "");
    }

    /**
     * (constructor) Creates a CA interpreter (with 0 bit offset) used for
     * generating MJ sample mashups.
     * @param minDuration the minimum duration of the song to generate (in seconds)
     * @param bitOffset the bit offset
     */
    public MikeyJInterp(double minDuration, int bitOffset) {
        this(minDuration, bitOffset, "");
    }

    /**
     * (constructor) Creates a CA interpreter (with 0 bit offset) used for
     * generating MJ sample mashups.
     * @param minDuration the minimum duration of the song to generate (in seconds)
     * @param bitOffset the bit offset
     * @param path the path to save the output file
     */
    public MikeyJInterp(double minDuration, int bitOffset, String path) {
        this.minDuration = Math.max(0.0, minDuration);
        this.bitOffset = Math.max(0, bitOffset);
        this.path = path;
    }

    /**
     * Helper method to get the required bits for a particular CellGroup.
     * @param cg the cell group to get bits from
     * @return the required bits
     */
    private int[] getRequiredBits(CellGroup cg) {
        final int[] bits = new int[REQUIRED_SIZE];
        for (int j = 0; j < REQUIRED_SIZE; j++) {
            bits[j] = cg.getValue(j + bitOffset) % 2;
        }
        return bits;
    }

    /**
     * Converts an array of bits to an integer (little-endian).
     * @param bits the bits
     * @return the converted integer
     */
    private int bitsToInt(int[] bits) {
        int sum = 0;
        for (int i = 0; i < bits.length; i++) {
            sum += bits[i] << i;
        }
        return sum;
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
            throw new IllegalArgumentException("MikeyJInterp requires 1D cellular automaton.");
        }
        if (dim.getDimensionSize(0) < REQUIRED_SIZE + bitOffset) {
            throw new IllegalArgumentException(String.format(
                    "MikeyJInterp requires CA to be at least %d cells big.", REQUIRED_SIZE));
        }
        // Interpret!
        List<MJEvent> events = new ArrayList<>();
        int currIteration = 0;
        double currDuration = 0.0;
        while (currDuration < minDuration) {
            CellGroup cg = ca.getIteration(currIteration);
            int[] bits = getRequiredBits(cg);
            int[] songBits = Arrays.copyOfRange(bits, 0, 3);
            int[] durationBits = Arrays.copyOfRange(bits, 3, 6);
            int[] startingBits = Arrays.copyOfRange(bits, 6, 10);
            int[] idBits = Arrays.copyOfRange(bits, 10, 13);
            Song song = SONG_MAP[bitsToInt(songBits)];
            int duration = DURATION_MAP[bitsToInt(durationBits)];
            int maxStart = SAMPLE_SIZE - duration;
            int startingBeat = Math.min(4 * bitsToInt(startingBits), maxStart);
            int id = 0;
            if (song.numSamples > 1) {
                id = bitsToInt(idBits) % song.numSamples;
            }
            MJEvent mj = new MJEvent(song, id, startingBeat, duration);
            events.add(mj);
            double durationSeconds = duration * BEAT_DURATION;
            currDuration += durationSeconds;
            currIteration++;
        }
        // Write to output file:
        List<String> lines = new ArrayList<>(events.size());
        for (MJEvent e : events) {
            lines.add(e.toString());
        }
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Class describing a single event for the song (contains the name of the
     * song, the id of the sample, the starting beat, and the sample duration in
     * beats).
     * @author jaw64
     */
    private static class MJEvent {

        /**
         * The name of the song.
         */
        public final Song song;

        /**
         * The sample ID of the song.
         */
        public final int id;

        /**
         * The beat to begin the sample.
         */
        public final int startingBeat;

        /**
         * The duration of the sample (in beats).
         */
        public final int duration;

        /**
         * (constructor) Creates a new MJEvent instance.
         * @param song the name of the song for this event
         * @param id the id of the sample of the song
         * @param startingBeat the beat to begin the sample
         * @param duration the duration of the sample (in beats)
         */
        public MJEvent(Song song, int id, int startingBeat, int duration) {
            this.song = song;
            this.id = id;
            this.startingBeat = startingBeat;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return String.format("%s -- sample %d, starts on beat %d, lasts for %d beats",
                    song.name(), id, startingBeat, duration);
        }
    }
}
