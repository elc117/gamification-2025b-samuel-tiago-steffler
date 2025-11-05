package com.badlogic.drop.entities.gates;

/**
 * Example usage of the LogicGate classes.
 * This demonstrates how to create, configure, and use logic gates.
 */
public class GateExample {
    
    /**
     * Example method showing how to create and use logic gates
     */
    public static void demonstrateGates() {
        // Create an AND gate at position (100, 200)
        ANDGate andGate = new ANDGate(100f, 200f);
        
        // Set the inputs
        andGate.setInput(0, true);   // First input: true
        andGate.setInput(1, false);  // Second input: false
        
        // Update the gate to compute output
        andGate.update();
        
        // Get the output
        boolean result = andGate.getOutput();
        System.out.println("AND Gate output: " + result); // false (true AND false = false)
        
        
        // Example with OR gate
        ORGate orGate = new ORGate(200f, 200f);
        orGate.setInput(0, true);
        orGate.setInput(1, false);
        orGate.update();
        System.out.println("OR Gate output: " + orGate.getOutput()); // true (true OR false = true)
        
        
        // Example with NOT gate
        NOTGate notGate = new NOTGate(300f, 200f);
        notGate.setInput(0, true);
        notGate.update();
        System.out.println("NOT Gate output: " + notGate.getOutput()); // false (NOT true = false)
        
        
        // Example with XOR gate
        XORGate xorGate = new XORGate(400f, 200f);
        xorGate.setInput(0, true);
        xorGate.setInput(1, true);
        xorGate.update();
        System.out.println("XOR Gate output: " + xorGate.getOutput()); // false (true XOR true = false)
        
        
        // Example: Setting a texture (you'll load this from your asset manager)
        // Texture andTexture = new Texture("gates/and_gate.png");
        // andGate.setTexture(andTexture);
        
        
        // Example: Getting gate position for rendering
        float x = andGate.getX();
        float y = andGate.getY();
        float width = andGate.getWidth();
        float height = andGate.getHeight();
        
        // You would use these values to render the gate:
        // batch.draw(andGate.getTexture(), x, y, width, height);
        
        
        // Example: Creating a gate with custom number of inputs
        ANDGate multiInputAnd = new ANDGate(3, 500f, 200f); // 3-input AND gate
        multiInputAnd.setInput(0, true);
        multiInputAnd.setInput(1, true);
        multiInputAnd.setInput(2, false);
        multiInputAnd.update();
        System.out.println("3-input AND Gate output: " + multiInputAnd.getOutput()); // false
        
        
        // Clean up (call when gates are no longer needed)
        // andGate.dispose();
        // orGate.dispose();
        // etc.
    }
    
    /**
     * Example of creating a simple circuit:
     * Two inputs go into an AND gate, output goes to a NOT gate
     */
    public static boolean simpleCircuit(boolean input1, boolean input2) {
        // Create gates
        ANDGate andGate = new ANDGate(100f, 200f);
        NOTGate notGate = new NOTGate(200f, 200f);
        
        // Set initial inputs
        andGate.setInput(0, input1);
        andGate.setInput(1, input2);
        andGate.update();
        
        // Feed AND output to NOT input
        notGate.setInput(0, andGate.getOutput());
        notGate.update();
        
        // Return final output (this is a NAND gate!)
        return notGate.getOutput();
    }
}
