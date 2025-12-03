# CI-Colors: Modular Color Mixing Optimization Framework

## 🎨 Overview

A clean, extensible optimization framework for color mixing that separates domain data, optimization logic, penalties, and evaluation metrics. Designed to support multiple optimization algorithms, hybrid strategies, and customizable goal functions.

## 🏗️ Architecture

### 1️⃣ **Domain Layer** (Immutable)
Represents static facts about the problem domain.

**Files:**
- `Color.kt` - Immutable pigment representation (ID, title, LAB, RGB, metadata)
- `LabColor.kt` - LAB color space representation and conversions

**Key Principle:** Domain objects represent static facts and never change during optimization.

```kotlin
data class Color(
    val id: Int?,
    val title: String,
    val lab: LabColor,
    // ... other immutable properties
)
```

---

### 2️⃣ **State Layer** (Dynamic)
Represents candidate solutions and transient optimization values.

**Files:**
- `Mixture.kt` - Couples a palette with a weight vector

**Key Principle:** State objects represent solutions and are the only things that change.

```kotlin
data class Mixture(
    val palette: List<Color>,      // immutable reference
    val weights: DoubleArray       // mutable during optimization
)
```

---

### 3️⃣ **Functional Layer** (Pure Functions)
Provides reusable components for evaluating candidate solutions.

#### **Normalizer** - Transforms raw weights into valid proportions
**Interface:** `Normalizer.kt`

**Implementations:**
- `ProportionsNormalizer.kt` - Sum-to-1 normalization (clips negatives)
- `SoftmaxNormalizer.kt` - Exponential normalization

```kotlin
interface Normalizer {
    fun normalize(weights: DoubleArray): DoubleArray
}
```

#### **ColorMixer** - Computes resulting color from weights + palette
**Interface:** `ColorMixer.kt`

**Implementations:**
- `MixboxColorMixer.kt` - Realistic pigment mixing using Mixbox library
- `LabBlendColorMixer.kt` - Simple weighted average in LAB space

```kotlin
interface ColorMixer {
    fun mixColors(weights: DoubleArray, palette: List<Color>): LabColor
}
```

#### **MixingError** - Computes color distance metric
**Interface:** `MixingError.kt`

**Implementations:**
- `DeltaE2000.kt` - CIEDE2000 (most perceptually accurate)
- `DeltaE76.kt` - Simple Euclidean distance in LAB

```kotlin
interface MixingError {
    fun calculate(mixed: LabColor, target: LabColor): Double
}
```

**Key Principle:** These components are pure functions with no side effects.

---

### 4️⃣ **Penalty Layer**
Modifies objective value by adding costs for undesirable solutions.

**Interface:** `Penalty.kt`

**Implementations:**
- `SparsityPenalty.kt` - Penalizes too many active colors
- `SimilarityPenalty.kt` - Penalizes using similar colors together
- `RegularizationPenalties.kt` - L1/L2 regularization

```kotlin
interface Penalty {
    fun calculate(weights: DoubleArray): Double
}
```

**Key Principle:** Penalties operate on numeric weights, never on domain objects.

---

### 5️⃣ **Goal Layer** (Objective Function)
Combines all components into a single evaluation function.

**File:** `Goal.kt`

```kotlin
class Goal(
    private val palette: List<Color>,
    private val target: LabColor,
    private val penalties: List<Penalty>,
    private val mixingError: MixingError,
    private val normalizer: Normalizer,
    private val colorMixer: ColorMixer
) {
    fun evaluate(weights: DoubleArray): Double {
        val normalized = normalizer.normalize(weights)
        val mixed = colorMixer.mixColors(normalized, palette)
        var error = mixingError.calculate(mixed, target)
        penalties.forEach { error += it.calculate(normalized) }
        return error
    }
}
```

**Key Principle:** Goal is the ONLY interface that optimizers interact with.

---

### 6️⃣ **Optimizer Layer** (Algorithms)
Executes optimization according to a chosen algorithm.

**Interface:** `Optimizer.kt`

**Expected Implementations:**
- Local: Nelder-Mead, Powell
- Global: CMA-ES, PSO, GA
- Hybrid: CMA-ES → Nelder-Mead

```kotlin
interface Optimizer {
    fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult
}
```

**Key Principle:** Optimizers operate only on Goals and weight vectors.

---

## 🧱 Core Architectural Principles

✅ **Separation of Concerns** - Domain vs state vs computation vs optimization

✅ **Immutability of Domain** - Colors never change → safe, stable, reusable

✅ **Pure Functional Evaluation** - `Goal.evaluate(weights)` has no side effects

✅ **Modular Composition** - Mixer, normalizer, penalties all plug into the goal

✅ **Optimizer-Agnostic** - Optimization algorithms see only the Goal

✅ **Extensibility** - Add new mixers, penalties, optimizers without refactoring

✅ **Reproducibility** - No mutation of shared domain objects

---

## 📦 Component Summary

| Layer | Interface | Implementations | Purpose |
|-------|-----------|-----------------|---------|
| **Domain** | - | `Color`, `LabColor` | Immutable data |
| **State** | - | `Mixture` | Candidate solutions |
| **Functional** | `Normalizer` | `ProportionsNormalizer`, `SoftmaxNormalizer` | Weight transformation |
| **Functional** | `ColorMixer` | `MixboxColorMixer`, `LabBlendColorMixer` | Color blending |
| **Functional** | `MixingError` | `DeltaE2000`, `DeltaE76` | Color distance |
| **Penalty** | `Penalty` | `SparsityPenalty`, `SimilarityPenalty`, `L1`, `L2` | Constraint costs |
| **Goal** | - | `Goal` | Objective function |
| **Optimizer** | `Optimizer` | TBD (CMA-ES, Nelder-Mead, etc.) | Search algorithms |

---

## 🚀 Usage Example

```kotlin
// 1. Define palette (domain)
val palette = listOf(
    Color(1, "Red", "r.jpg", "#FF0000", lab = LabColor.fromHex("#FF0000")),
    Color(2, "Blue", "b.jpg", "#0000FF", lab = LabColor.fromHex("#0000FF")),
    Color(3, "Yellow", "y.jpg", "#FFFF00", lab = LabColor.fromHex("#FFFF00"))
)

// 2. Define target
val target = LabColor.fromHex("#8B4513") // Brown

// 3. Configure components
val goal = Goal(
    palette = palette,
    target = target,
    penalties = listOf(
        SparsityPenalty(threshold = 0.01, penaltyPerColor = 1.0),
        SimilarityPenalty(similarityPairs = listOf(), threshold = 0.1, penaltyPerPair = 3.0)
    ),
    mixingError = DeltaE2000(),
    normalizer = ProportionsNormalizer(),
    colorMixer = MixboxColorMixer()
)

// 4. Optimize
val initialWeights = DoubleArray(palette.size) { 1.0 / palette.size }
// val result = optimizer.optimize(goal, initialWeights)

// 5. Evaluate
val error = goal.evaluate(initialWeights)
println("Error: $error")
```

See `UsageExamples.kt` for more detailed examples.

---

## 🔧 Extending the Framework

### Add a New Penalty
```kotlin
class CustomPenalty(private val strength: Double) : Penalty {
    override fun calculate(weights: DoubleArray): Double {
        // Your logic here
        return strength * someCalculation(weights)
    }
}
```

### Add a New Color Mixer
```kotlin
class CustomMixer : ColorMixer {
    override fun mixColors(weights: DoubleArray, palette: List<Color>): LabColor {
        // Your mixing logic here
    }
}
```

### Add a New Optimizer
```kotlin
class MyOptimizer : Optimizer {
    override fun optimize(goal: Goal, initialWeights: DoubleArray, bounds: Pair<DoubleArray, DoubleArray>?): OptimizationResult {
        // Your optimization algorithm here
    }
}
```

---

## 📚 Files Overview

### Core Architecture
- ✅ `Color.kt` - Domain: Immutable color representation
- ✅ `LabColor.kt` - Domain: LAB color space with conversions
- ✅ `Mixture.kt` - State: Palette + weights
- ✅ `Normalizer.kt` - Functional: Weight normalization interface
- ✅ `ColorMixer.kt` - Functional: Color mixing interface
- ✅ `MixingError.kt` - Functional: Color distance interface
- ✅ `Penalty.kt` - Penalty: Cost function interface
- ✅ `Goal.kt` - Goal: Objective function
- ✅ `Optimizer.kt` - Optimizer: Algorithm interface

### Implementations
- ✅ `ProportionsNormalizer.kt` - Sum-to-1 normalization
- ✅ `SoftmaxNormalizer.kt` - Softmax normalization
- ✅ `MixboxColorMixer.kt` - Realistic pigment mixing
- ✅ `LabBlendColorMixer.kt` - Simple LAB blending
- ✅ `DeltaE2000.kt` - CIEDE2000 color difference
- ✅ `DeltaE76.kt` - Simple Euclidean distance
- ✅ `SparsityPenalty.kt` - Penalize many active colors
- ✅ `SimilarityPenalty.kt` - Penalize similar color pairs
- ✅ `RegularizationPenalties.kt` - L1/L2 regularization

### Legacy/To Be Updated
- ⚠️ `MixingOptimizer.kt` - Old implementation (needs refactoring)
- ⚠️ `ColorMixObjective.kt` - Old implementation (superseded by Goal)
- ⚠️ `Penalties.kt` - Old implementation (superseded by Penalty interface)
- ⚠️ `DeltaE.kt` - Check if still needed

### Documentation
- ✅ `UsageExamples.kt` - Comprehensive usage examples
- ✅ `ARCHITECTURE.md` - This file

---

## 🎯 Benefits of This Architecture

1. **Testability** - Each component can be tested in isolation
2. **Reusability** - Components work independently and can be combined freely
3. **Maintainability** - Clear responsibilities, easy to understand
4. **Extensibility** - Add new implementations without touching existing code
5. **Type Safety** - Strong typing prevents many runtime errors
6. **No Side Effects** - Pure functions make reasoning about code easier
7. **Performance** - Can swap implementations (e.g., fast DeltaE76 for prototyping)

---

## 🔮 Future Enhancements

- [ ] Implement CMA-ES optimizer
- [ ] Implement Nelder-Mead optimizer
- [ ] Implement hybrid optimization strategies
- [ ] Add benchmarking framework
- [ ] Add experiment runner for comparing algorithms
- [ ] Add visualization tools
- [ ] Add caching for expensive color conversions
- [ ] Add parallel evaluation support
- [ ] Add gradient-based optimizers
- [ ] Add constraint handling (min/max weights per color)

---

## 📄 License

[Your license here]

## 👥 Contributors

[Your name/team here]

