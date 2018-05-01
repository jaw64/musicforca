package jaw64.ca.interpreter;

import jaw64.ca.CellularAutomaton;

/**
 * Interface defining how to interpret cellular automata.
 * @author jaw64
 */
public interface Interpreter {
    
    /**
     * Defines how to interpret a cellular automata.
     * @param ca the cellular automata.
     */
    public void interpret(CellularAutomaton ca);

}
