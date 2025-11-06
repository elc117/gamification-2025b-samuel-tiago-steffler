# Circuit Evaluation System - Documentation

## Overview
The circuit evaluation system uses **topological sorting** to ensure logic gates are evaluated in the correct dependency order, from inputs to outputs.

## Architecture

```
┌─────────────┐
│ InputBits   │ ← User controllable inputs (toggleable bits)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ LogicGate   │ ← Gates evaluate based on their inputs
└──────┬──────┘   (AND, OR, NOT, XOR, etc.)
       │
       ▼
┌─────────────┐
│    Wire     │ ← Connects gates and propagates signals
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ OutputBits  │ ← Displays final results
└─────────────┘
```

## Evaluation Flow

### 1. Building the Circuit
```java
// Create inputs
InputBits[] inputs = {
    new InputBits("A", 50f, 300f),
    new InputBits("B", 50f, 200f)
};

// Create gates
LogicGate[] gates = {
    new ANDGate(200f, 250f),
    new NOTGate(350f, 250f)
};

// Create wires (AND → NOT)
Wire[] wires = {
    new Wire(gates[0], gates[1])
};

// Create outputs
OutputBits[] outputs = {
    new OutputBits("Result", gates[1], 500f, 250f)
};

// Assemble circuit
Circuit circuit = new Circuit(inputs, gates, wires, outputs, expectedOutput);
```

### 2. Dependency Graph Construction
The `Circuit` automatically builds a dependency graph:

```
Input A ──┐
          ├──> AND Gate ──> NOT Gate ──> Output
Input B ──┘

Dependency chain:
- AND has no gate dependencies (only inputs)
- NOT depends on AND
- Evaluation order: [AND, NOT]
```

### 3. Topological Sort (Kahn's Algorithm)
```
1. Find all gates with no dependencies → Add to queue
2. Process queue:
   - Add gate to evaluation order
   - Remove it from dependents' dependency list
   - If dependent now has 0 dependencies → Add to queue
3. Repeat until all gates processed
```

### 4. Circuit Evaluation
```java
// Set input values
circuit.setInputValues(new boolean[]{true, false}); // A=1, B=0

// Evaluate in correct order
circuit.evaluate();
// → AND evaluates first: true AND false = false
// → NOT evaluates second: NOT false = true

// Check results
boolean[] outputs = circuit.getActualOutputs(); // [true]
boolean correct = circuit.isCorrect(); // Compare with expected
```

## Example: Complex Circuit (Full Adder)

```
         A ──┬──> XOR1 ──┬──> XOR2 ──> Sum
             │           │
         B ──┼──> AND1   └──> AND2
             │      │           │
         Cin ───────┼───────────┘
                    │
                    └────> OR ──> Carry Out
```

**Evaluation Order (topological sort result):**
1. XOR1 (depends only on inputs A, B)
2. AND1 (depends only on inputs A, B)
3. XOR2 (depends on XOR1 and input Cin)
4. AND2 (depends on XOR1 and input Cin)
5. OR (depends on AND1 and AND2)

## Key Features

### ✅ Automatic Dependency Resolution
- No manual ordering needed
- Handles complex multi-layer circuits
- Detects cycles (throws error)

### ✅ Separation of Concerns
- **InputBits**: User-controllable sources
- **LogicGate**: Pure logic computation
- **Wire**: Visual and logical connections
- **OutputBits**: Result terminals
- **Circuit**: Orchestration and evaluation

### ✅ Efficient Evaluation
- Each gate evaluated exactly once per update
- O(V + E) complexity (vertices + edges)
- Bottom-up evaluation (inputs → outputs)

## Usage in Game Loop

```java
public class GameScreen {
    private Circuit circuit;
    
    public void create() {
        circuit = CircuitExample.createHalfAdder();
    }
    
    public void handleInput() {
        // User clicks on InputBit
        if (inputBitClicked) {
            circuit.getInputs()[0].toggle();
        }
    }
    
    public void update() {
        // Re-evaluate circuit when inputs change
        circuit.evaluate();
        
        // Check if player solved it
        if (circuit.isCorrect()) {
            // Level complete!
        }
    }
    
    public void render() {
        // Render inputs, gates, wires, outputs
        // Use wire colors based on circuit.getWires()[i].getState()
    }
}
```

## Benefits Over Manual Evaluation

| Manual Approach | Topological Sort Approach |
|----------------|---------------------------|
| Hard-coded evaluation order | Automatic ordering |
| Breaks with circuit changes | Adapts to any structure |
| Error-prone | Cycle detection |
| Difficult for complex circuits | Scales to any complexity |
| Tight coupling | Loose coupling |

## Debugging

```java
// Print evaluation order
circuit.printEvaluationOrder();
// Output:
// 1. AND gate
// 2. XOR gate
// 3. OR gate

// Check actual vs expected
boolean[] actual = circuit.getActualOutputs();
boolean[] expected = circuit.getExpectedOutput();
```

## Future Enhancements

Possible improvements:
- [ ] Support for multi-output gates (MUX, DEMUX)
- [ ] Visualization of signal propagation (animation)
- [ ] Save/Load circuit from JSON
- [ ] Circuit simulation step-by-step (debug mode)
- [ ] Performance optimization for very large circuits
