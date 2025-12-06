# Running CMA-ES with Specific Parameters (No Hyperparameter Optimization)

## Quick Start

The simplest way to run CMA-ES with default parameters:

```kotlin
fun main() {
    runCMAESWithParameters()
}
```

This will:
- Use default CMA-ES parameters
- Run on the default training dataset
- Generate 50 optimization samples
- Save results to `results/cmaes-results.csv`

## Custom Parameters

### Example 1: Custom CMA-ES Parameters

```kotlin
fun main() {
    runCMAESWithParameters(
        parameters = mapOf(
            "populationMultiplier" to 15,    // Larger population
            "sigma" to 0.5,                  // Larger initial step
            "diagonalOnly" to 5,             // Fewer diagonal generations
            "checkFeasibleCount" to 15,      // More feasibility checks
            "stopFitness" to 0.0001          // Stricter convergence
        )
    )
}
```

### Example 2: Custom Dataset and Output

```kotlin
fun main() {
    runCMAESWithParameters(
        trainingDataPath = "C:\\data\\my-training-data.csv",
        outputPath = "C:\\results\\my-cmaes-results.csv",
        numSamples = 100,  // More samples for better statistics
        parameters = mapOf(
            "populationMultiplier" to 10,
            "sigma" to 0.3,
            "diagonalOnly" to 10,
            "checkFeasibleCount" to 10,
            "stopFitness" to 0.001
        )
    )
}
```

### Example 3: Quick Test Run

```kotlin
fun main() {
    // Fast test with fewer samples
    runCMAESWithParameters(
        numSamples = 5,
        parameters = mapOf(
            "populationMultiplier" to 5,     // Smaller for speed
            "sigma" to 0.3,
            "diagonalOnly" to 10,
            "checkFeasibleCount" to 10,
            "stopFitness" to 0.01            // Relaxed for speed
        )
    )
}
```

## Parameters Explained

### CMA-ES Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `populationMultiplier` | Int | 10 | Population size multiplier (pop = multiplier × dim) |
| `sigma` | Double | 0.3 | Initial step size (exploration range) |
| `diagonalOnly` | Int | 10 | Number of generations before using full covariance |
| `checkFeasibleCount` | Int | 10 | Frequency of feasibility checks |
| `stopFitness` | Double | 0.001 | Target fitness value for stopping |

### Function Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `trainingDataPath` | String | `training--3-colors-random-step.csv` | Path to training data |
| `outputPath` | String | `results/cmaes-results.csv` | Where to save results |
| `numSamples` | Int | 50 | Number of optimization runs |
| `parameters` | Map | See above | CMA-ES parameters |

## Parameter Tuning Guide

### For Speed (Fast but less accurate)
```kotlin
parameters = mapOf(
    "populationMultiplier" to 5,     // Small population
    "sigma" to 0.3,
    "diagonalOnly" to 5,             // Use full covariance earlier
    "checkFeasibleCount" to 5,       // Fewer checks
    "stopFitness" to 0.01            // Relaxed stopping criterion
)
```

### For Quality (Slow but more accurate)
```kotlin
parameters = mapOf(
    "populationMultiplier" to 20,    // Large population
    "sigma" to 0.2,                  // Smaller steps
    "diagonalOnly" to 20,            // More diagonal generations
    "checkFeasibleCount" to 20,      // More feasibility checks
    "stopFitness" to 0.0001          // Strict stopping criterion
)
```

### Balanced (Good trade-off)
```kotlin
parameters = mapOf(
    "populationMultiplier" to 10,    // Default
    "sigma" to 0.3,                  // Default
    "diagonalOnly" to 10,            // Default
    "checkFeasibleCount" to 10,      // Default
    "stopFitness" to 0.001           // Default
)
```

## Output

### Console Output
```
================================================================================
Running CMA-ES with specified parameters
Training data: C:\...\training--3-colors-random-step.csv
Output: C:\...\results\cmaes-results.csv
Number of samples: 50
Parameters: {populationMultiplier=10, sigma=0.3, ...}
================================================================================

[CMA-ES optimization runs...]

================================================================================
✓ CMA-ES optimization completed successfully!
Results saved to: C:\...\results\cmaes-results.csv
================================================================================
```

### Output CSV File
The results CSV contains:
- `targetLab` - Target LAB color
- `resultLab` - Resulting LAB color  
- `error` - DeltaE2000 error
- `weights` - Optimization weights
- `optimizerName` - "CMA-ES"
- `populationMultiplier`, `sigma`, etc. - Parameter values used

## Running Multiple Configurations

### Compare Different Parameters

```kotlin
fun main() {
    // Configuration 1: Conservative
    runCMAESWithParameters(
        outputPath = "results/cmaes-conservative.csv",
        parameters = mapOf(
            "populationMultiplier" to 15,
            "sigma" to 0.2,
            "diagonalOnly" to 15,
            "checkFeasibleCount" to 15,
            "stopFitness" to 0.0001
        )
    )
    
    // Configuration 2: Aggressive
    runCMAESWithParameters(
        outputPath = "results/cmaes-aggressive.csv",
        parameters = mapOf(
            "populationMultiplier" to 5,
            "sigma" to 0.5,
            "diagonalOnly" to 5,
            "checkFeasibleCount" to 5,
            "stopFitness" to 0.01
        )
    )
    
    // Configuration 3: Balanced
    runCMAESWithParameters(
        outputPath = "results/cmaes-balanced.csv",
        parameters = mapOf(
            "populationMultiplier" to 10,
            "sigma" to 0.3,
            "diagonalOnly" to 10,
            "checkFeasibleCount" to 10,
            "stopFitness" to 0.001
        )
    )
}
```

## Comparison with Hyperparameter Optimization

| Approach | Use Case | Speed | Flexibility |
|----------|----------|-------|-------------|
| **`runCMAESWithParameters()`** | Known good parameters | Fast | Manual control |
| **`runCMAESHyperoptimizationFast()`** | Finding best parameters | Slow | Automatic |
| **`runAllAlgorithms()`** | Comparing all algorithms | Medium | Compare all |

## Typical Workflow

```kotlin
fun main() {
    // Step 1: Quick test with default parameters
    runCMAESWithParameters(numSamples = 10)
    
    // Step 2: Try different parameter configurations
    // runCMAESWithParameters(parameters = config1)
    // runCMAESWithParameters(parameters = config2)
    
    // Step 3: Use best configuration for full run
    // runCMAESWithParameters(numSamples = 100, parameters = bestConfig)
}
```

## Tips

1. **Start with defaults** - They work well for most cases
2. **Adjust sigma** - Controls exploration vs exploitation
3. **Scale population** - Larger for complex problems
4. **Monitor output** - Check convergence in results
5. **Use multiple samples** - More samples = better statistics

## Related Functions

- `runAllAlgorithms()` - Run all optimizers with defaults
- `runCMAESHyperoptimizationFast()` - Optimize CMA-ES parameters
- `runCMAESExperiment()` - Run parameter sweep experiment

## Summary

`runCMAESWithParameters()` is perfect for:
- ✅ Running CMA-ES with known parameters
- ✅ Testing specific configurations
- ✅ Production runs with tuned settings
- ✅ Quick experiments without hyperparameter optimization

**Simple, direct, and gives you full control!**

