# Hyperparameter Optimization Functions Reference

This document provides a quick reference for all available hyperparameter optimization functions.

## Available Optimization Functions

### 1. Nelder-Mead (`runNelderMeadHyperoptimization`)
**Optimizer Type:** Derivative-free simplex method

**Parameters:**
- `maxEvaluations` (Int): Maximum number of function evaluations
  - Default: 2000, Range: [200, 20000]
- `relativeThreshold` (Double): Relative convergence threshold (log scale)
  - Default: 1e-4, Range: [1e-8, 1e-1]
- `absoluteThreshold` (Double): Absolute convergence threshold (log scale)
  - Default: 1e-5, Range: [1e-10, 1e-2]
- `stepSize` (Double): Initial simplex step size (log scale)
  - Default: 0.01, Range: [0.0001, 1.0]

**Use Case:** Simple, robust optimization for smooth functions

---

### 2. CMA-ES (`runCMAEHyperoptimization`)
**Optimizer Type:** Evolutionary strategy with covariance matrix adaptation

**Parameters:**
- `populationMultiplier` (Int): Population size multiplier
  - Default: 10, Range: [3, 40]
- `sigma` (Double): Initial step size
  - Default: 0.3, Range: [0.01, 1.0]
- `diagonalOnly` (Int): Generations before full covariance
  - Default: 10, Range: [0, 20]
- `checkFeasibleCount` (Int): Feasibility check frequency
  - Default: 10, Range: [0, 20]
- `stopFitness` (Double): Target fitness value
  - Default: 1e-3, Range: [1e-6, 1e-2]

**Use Case:** Complex, high-dimensional optimization problems

---

### 3. NSGAII (`runNSGAIIHyperoptimization`)
**Optimizer Type:** Multi-objective genetic algorithm

**Parameters:**
- `populationSize` (Int): Population size
  - Default: 175, Range: [50, 500]
- `sbx.rate` (Double): Simulated binary crossover rate
  - Default: 1.0, Range: [0.0, 1.0]
- `sbx.distributionIndex` (Double): SBX distribution index
  - Default: 15.0, Range: [0.0, 30.0]
- `pm.rate` (Double): Polynomial mutation rate
  - Default: 0.1, Range: [0.0, 0.2]
- `pm.distributionIndex` (Double): PM distribution index
  - Default: 20.0, Range: [0.0, 40.0]

**Use Case:** Multi-objective optimization, balanced solutions

---

### 4. BOBYQA (`runBOBYQAHyperoptimization`)
**Optimizer Type:** Bound-constrained optimization by quadratic approximation

**Parameters:**
- `maxEvaluations` (Int): Maximum number of function evaluations
  - Default: 10000, Range: [1000, 50000]
- `numberOfInterpolationPoints` (Int): Number of interpolation points
  - Default: 20, Range: [13, 50]
  - Note: Must be at least n+2 where n is the problem dimension

**Use Case:** Smooth, bound-constrained problems with expensive evaluations

---

### 5. Powell (`runPowellHyperoptimization`)
**Optimizer Type:** Direction-set method without derivatives

**Parameters:**
- `maxEvaluations` (Int): Maximum number of function evaluations
  - Default: 50000, Range: [5000, 200000]
- **Note:** Tolerance values (1e-6) are hardcoded in implementation

**Use Case:** Smooth optimization without derivative information

---

### 6. SMSEMOA (`runSMSEMOAHyperoptimization`)
**Optimizer Type:** S-metric selection evolutionary multi-objective algorithm

**Parameters:**
- `populationSize` (Int): Population size
  - Default: 100, Range: [50, 500]
- `sbxRate` (Double): Simulated binary crossover rate
  - Default: 1.0, Range: [0.5, 1.0]
- `sbxDistributionIndex` (Double): SBX distribution index
  - Default: 15.0, Range: [5.0, 30.0]
- `pmDistributionIndex` (Double): Polynomial mutation distribution index
  - Default: 20.0, Range: [10.0, 40.0]

**Use Case:** Multi-objective optimization with hypervolume optimization

---

### 7. SPEA2 (`runSPEA2Hyperoptimization`)
**Optimizer Type:** Strength Pareto evolutionary algorithm 2

**Parameters:**
- `populationSize` (Int): Population size
  - Default: 100, Range: [50, 500]
- `archiveSize` (Int): Archive size for elite solutions
  - Default: 100, Range: [50, 500]
- `sbxRate` (Double): Simulated binary crossover rate
  - Default: 1.0, Range: [0.5, 1.0]
- `sbxDistributionIndex` (Double): SBX distribution index
  - Default: 15.0, Range: [5.0, 30.0]
- `pmDistributionIndex` (Double): Polynomial mutation distribution index
  - Default: 20.0, Range: [10.0, 40.0]

**Use Case:** Multi-objective optimization with archive maintenance

---

## Usage Example

To optimize hyperparameters for any algorithm:

```kotlin
fun main() {
    // Choose one:
    runNelderMeadHyperoptimization()
    runCMAEHyperoptimization()
    runNSGAIIHyperoptimization()
    runBOBYQAHyperoptimization()
    runPowellHyperoptimization()
    runSMSEMOAHyperoptimization()
    runSPEA2Hyperoptimization()
}
```

## Results

Each function will:
1. Run the outer CMA-ES optimizer to find best hyperparameters
2. Evaluate each configuration on `numSamples` (default: 20) optimization runs
3. Print the best hyperparameters found and the mean error achieved
4. Return a Map containing the optimized parameter values

## Customization

To modify training data or output directory, edit the paths in each function:
```kotlin
val trainingDataPath = "your/path/to/training.csv"
val tempOutputDir = "your/path/to/temp/output"
```

To adjust the number of samples per configuration:
```kotlin
numSamples = 20  // Increase for more reliable results
```

## Algorithm Selection Guide

| Problem Type | Recommended Algorithms |
|-------------|----------------------|
| Smooth, unimodal | Powell, BOBYQA |
| Smooth, multimodal | CMA-ES, Nelder-Mead |
| Non-smooth | CMA-ES |
| Multi-objective | NSGAII, SMSEMOA, SPEA2 |
| Expensive evaluations | BOBYQA, CMA-ES |
| Simple problems | Nelder-Mead, Powell |
| Complex problems | CMA-ES, NSGAII |

