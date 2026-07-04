#  Phase-Plane-Plotter (Java)

Built as a core technical application showcase for mathematical foundations, research, and professional developement.

A high-performance computational Java utility engineered to simulate, analyze, and map vector flow fields for autonomous systems of first-order ordinary differential equations (ODEs). The project provides explicit trajectory modeling to track the dynamic state-space evolution of a particle given explicit initial condition matrices.

---

##  Key Engineering Highlights

* **Mathematical Simulation Engine:** Leverages precise calculus-based vector calculations to capture and plot directional fields for both linear and non-linear mathematical configurations.
* **Numerical Calculus Solvers:** Integrates algorithmic step approximation mechanics (such as Euler's Method or Runge-Kutta 4th Order) to steadily compute high-precision continuous motion sweeps.
* **Object-Oriented Integrity:** Strictly follows core Java OOP principles (encapsulation, abstraction, clear interface boundaries) to decouple math solvers from visual/state engines.
* **Performance Scoping:** Processes trajectory arrays efficiently with optimized loops to maintain zero memory leakage during recursive calculation frames.

---

##  Tech Stack & Language Metric

* **Language:** Java (JDK 17 or higher recommended)
* **Design Pattern:** Object-Oriented Domain Architecture (OOP)
* **Application Focus:** Differential Calculus Approximation & Computational Matrix Tracking

---

##  System File Architecture

```text
 Phase-Plane-Plotter
   ├──  src/                  # Main Java Source Code Files
   │   ├──  Main_phaseplane.java        # Entry point (Initializes UI/Logic frames)
   │   ├──  Matrix.java                 # Equations definition, matrix and parameter state settings
   │   ├──  PhasePlane.java             # Core system vector processing algorithms
   |   └──  ExpressionEvaluator.java    # External class to solve expressions to be plotted
   ├──  .gitignore            # Keeps repo clean from heavy binary out/ or bin/ junk files
   └──  README.md             # This structural engineering documentation page
```

---

##  Sample Core Logic

This abstract snippet illustrates the design pattern utilized to dynamically resolve state approximations across incremental time adjustments (`dt`):

```java
public class PhasePlaneSolver {
    // Calculates trajectory movements by computing directional slope metrics
    public double[] computeNextState(double currentX, double currentY, double dt) {
        double dX = evaluateXDerivative(currentX, currentY);
        double dY = evaluateYDerivative(currentX, currentY);
        
        // Iterative step approximation matrix update
        double nextX = currentX + (dX * dt);
        double nextY = currentY + (dY * dt);
        
        return new double[] { nextX, nextY };
    }
}
```

---

##  How to Run Locally

### 1. Prerequisites
Ensure you have the Java Development Kit (**JDK 17+**) installed globally on your processing machine.

### 2. Local Initialization
Open your terminal framework and input these execution pathways to download and run the solver locally:

```bash
# Clone the clean repository layout
git clone https://github.com/ThiccRicc/Phase-Plane-Plotter.git

# Enter the root trajectory directory
cd Phase-Plane-Plotter

# Compile the Java system classes via standard CLI (or drop the folder into IntelliJ/VS Code)
javac src/*.java

# Boot the primary tracking application main entry point
java src.Main
```
