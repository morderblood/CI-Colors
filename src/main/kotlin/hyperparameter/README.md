# Flexible Hyperparameter Optimization

Эта система позволяет оптимизировать гиперпараметры для любого оптимизатора с помощью настраиваемых параметров.

## Основные компоненты

### 1. `HyperparameterConfig`
Конфигурация для одного гиперпараметра:
- `name`: Имя параметра
- `initialValue`: Начальное значение для оптимизации
- `sigma`: Стандартное отклонение для CMA-ES
- `lowerBound`: Нижняя граница
- `upperBound`: Верхняя граница
- `transform`: Функция преобразования непрерывного значения в реальный параметр

### 2. `HyperparameterOptimizer`
Оптимизатор гиперпараметров (использует CMA-ES как внешний оптимизатор):
- `trainingDataPath`: Путь к обучающему набору данных
- `numSamples`: Количество образцов для оценки каждой конфигурации
- `tempOutputDir`: Директория для временных файлов
- `innerOptimizerName`: Имя оптимизатора для оптимизации гиперпараметров
- `hyperparameters`: Список конфигураций гиперпараметров

### 3. `HyperparameterSample`
Результат оптимизации гиперпараметров:
- `optimizerName`: Имя внутреннего оптимизатора
- `parameters`: Map имен параметров к их оптимизированным значениям
- `meanError`: Средняя ошибка, достигнутая с этими параметрами

## Примеры использования

### Оптимизация CMA-ES

```kotlin
val cmaesHyperparams = listOf(
    HyperparameterConfig(
        name = "populationMultiplier",
        initialValue = 10.0,
        sigma = 5.0,
        lowerBound = 3.0,
        upperBound = 40.0,
        transform = { it.toInt() }  // Преобразование в целое число
    ),
    HyperparameterConfig(
        name = "sigma",
        initialValue = 0.3,
        sigma = 0.1,
        lowerBound = 0.01,
        upperBound = 1.0,
        transform = { it }  // Оставить как Double
    ),
    HyperparameterConfig(
        name = "diagonalOnly",
        initialValue = 10.0,
        sigma = 5.0,
        lowerBound = 0.0,
        upperBound = 20.0,
        transform = { it.toInt() }
    ),
    HyperparameterConfig(
        name = "checkFeasibleCount",
        initialValue = 10.0,
        sigma = 5.0,
        lowerBound = 0.0,
        upperBound = 20.0,
        transform = { it.toInt() }
    ),
    HyperparameterConfig(
        name = "stopFitness",
        initialValue = 1e-3,
        sigma = 1e-3,
        lowerBound = 1e-6,
        upperBound = 1e-2,
        transform = { it }
    )
)

val optimizer = HyperparameterOptimizer(
    trainingDataPath = "path/to/training.csv",
    numSamples = 20,
    tempOutputDir = "path/to/temp",
    innerOptimizerName = "CMA-ES",
    hyperparameters = cmaesHyperparams
)

val best = optimizer.optimize()
println("Best parameters: ${best.parameters}, error: ${best.meanError}")
```

### Оптимизация NSGAII

```kotlin
val nsgaiiHyperparams = listOf(
    HyperparameterConfig(
        name = "populationSize",
        initialValue = 100.0,
        sigma = 50.0,
        lowerBound = 50.0,
        upperBound = 500.0,
        transform = { it.toInt() }
    ),
    HyperparameterConfig(
        name = "maxGenerations",
        initialValue = 1000.0,
        sigma = 500.0,
        lowerBound = 100.0,
        upperBound = 5000.0,
        transform = { it.toInt() }
    ),
    HyperparameterConfig(
        name = "crossoverProbability",
        initialValue = 0.9,
        sigma = 0.1,
        lowerBound = 0.5,
        upperBound = 1.0,
        transform = { it }
    ),
    HyperparameterConfig(
        name = "mutationProbability",
        initialValue = 0.1,
        sigma = 0.05,
        lowerBound = 0.01,
        upperBound = 0.5,
        transform = { it }
    )
)

val optimizer = HyperparameterOptimizer(
    trainingDataPath = "path/to/training.csv",
    numSamples = 20,
    tempOutputDir = "path/to/temp",
    innerOptimizerName = "NSGAII",
    hyperparameters = nsgaiiHyperparams
)

val best = optimizer.optimize()
```

### Оптимизация Powell

```kotlin
val powellHyperparams = listOf(
    HyperparameterConfig(
        name = "maxEvaluations",
        initialValue = 1000.0,
        sigma = 500.0,
        lowerBound = 100.0,
        upperBound = 10000.0,
        transform = { it.toInt() }
    ),
    HyperparameterConfig(
        name = "relativeTolerance",
        initialValue = 1e-6,
        sigma = 1e-6,
        lowerBound = 1e-10,
        upperBound = 1e-3,
        transform = { it }
    ),
    HyperparameterConfig(
        name = "absoluteTolerance",
        initialValue = 1e-8,
        sigma = 1e-8,
        lowerBound = 1e-12,
        upperBound = 1e-4,
        transform = { it }
    )
)

val optimizer = HyperparameterOptimizer(
    trainingDataPath = "path/to/training.csv",
    numSamples = 20,
    tempOutputDir = "path/to/temp",
    innerOptimizerName = "Powell",
    hyperparameters = powellHyperparams
)

val best = optimizer.optimize()
```

## Преимущества новой системы

1. **Гибкость**: Легко добавлять новые оптимизаторы и параметры
2. **Масштабируемость**: Можно оптимизировать любое количество параметров
3. **Типобезопасность**: Функции преобразования обеспечивают правильные типы
4. **Переиспользование**: Один оптимизатор для всех внутренних оптимизаторов
5. **Читаемость**: Декларативная конфигурация параметров

## Расширение системы

Чтобы добавить новый оптимизатор:

1. Определите его гиперпараметры с помощью `HyperparameterConfig`
2. Убедитесь, что `OptimizerFactory` поддерживает его
3. Создайте экземпляр `HyperparameterOptimizer` с новыми параметрами
4. Запустите оптимизацию!

