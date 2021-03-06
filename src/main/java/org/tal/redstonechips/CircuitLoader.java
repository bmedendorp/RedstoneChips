
package org.tal.redstonechips;

import org.tal.redstonechips.circuit.Circuit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Tal Eisenberg
 */
public class CircuitLoader {
    private Map<String,Class<? extends Circuit>> circuitClasses = new HashMap<String,Class<? extends Circuit>>();
    private RedstoneChips rc;

    /**
     * Maximum number of characters for class names to fit into the 1st line of a sign excluding 2 color chars (13).
     */
    public final static int maxClassNameLength = 13;

    public CircuitLoader(RedstoneChips plugin) {
        rc = plugin;
    }

    /**
     * Adds the class to the circuit pool, allowing players to create circuits of this class.
     *
     * @param c The class to add. must extend org.tal.redstonechips.Circuit.
     */
    public void addCircuitClass(Class c) {
        String name = c.getSimpleName();

        if (name.length()>maxClassNameLength) {
            rc.log(Level.WARNING, "While trying to add " + c.getCanonicalName() + " to circuit pool: Class name is longer than " + maxClassNameLength + " characters.");
        } else if (circuitClasses.containsKey(name)) {
            rc.log(Level.WARNING, "While trying to add " + c.getCanonicalName() + " to circuit pool: Another circuit class named " + name + " was found. ");
        } else if (!Circuit.class.isAssignableFrom(c)) {
            rc.log(Level.WARNING, "While trying to add " + c.getCanonicalName() + ": Class does not extend org.tal.redstonechips.circuits.Circuit");
        } else {
            circuitClasses.put(name, c);
        }
    }

    /**
     * Removes a class from the circuit pool
     *
     * @param c Class to remove.
     * @return result of Map.remove() method.
     */
    public Object removeCircuitClass(Class c) {
        return circuitClasses.remove(c.getSimpleName());
    }

    /**
     *
     * @param name Circuit class name.
     * @return A new empty instance of a circuit of the specified class.
     * @throws InstantiationException if thrown by Class.newInstance().
     * @throws IllegalAccessException if thrown by Class.newInstance().
     */
    public Circuit getCircuitInstance(String name) throws InstantiationException, IllegalAccessException {
        Class c = this.circuitClasses.get(name);
        if (c==null) throw new IllegalArgumentException("Unknown circuit type: " + name);
        else return (Circuit) c.newInstance();
    }

    /**
     *
     * @return a Map containing all registered circuit classes. The map's key is set to the circuit class name.
     */
    public Map<String, Class<? extends Circuit>> getCircuitClasses() {
        return circuitClasses;
    }

}
