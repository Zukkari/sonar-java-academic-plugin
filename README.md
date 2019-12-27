# sonar-java-academic-plugin

[![Build Status](https://travis-ci.org/Zukkari/sonar-java-academic-plugin.svg?branch=master)](https://travis-ci.org/Zukkari/sonar-java-academic-plugin)
[![codecov](https://codecov.io/gh/Zukkari/sonar-java-academic-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/Zukkari/sonar-java-academic-plugin)

Sonar plugin that can detect academic code smells in Java applications

Currently only Java language is supported.

## Supported code smells

Current list of supported code smells:

| Code smell | Description | 
| :---: | :---: |
| Data class | Description can be found [here](https://refactoring.guru/smells/data-class) |
| Message chain | Description can be found [here](https://refactoring.guru/smells/message-chains). This rule has configurable method chain length. This can be configured with property `sonar.academic.plugin.message.chain.length` |
| Long method | Detects methods that are longer than `X` lines (supports if statements, do while loops, for loops, synchronized blocks, try blocks (catch + finally), while loops). Configuratin property to configure method length is `sonar.academic.plugin.long.method.length`, with default value 8 | 
| Switch statement | Detects usage of switch statements |
| Shotgun surgery | Detect methods that are overused by other classes |
| Lazy class | Detect classes with no methods, low complexity methods or with high coupling with other classes |
| Long parameter list | Detect methods with more than `X` parameters |
| Blob class | |
| Refused Bequest | |
| Comments | |
| Cyclic dependencies | |
| Tradition breaker | |
| Divergent change | |
| Feature envy | |
| Data clumps | |
| Parallel inheritance hierarchies | |
| Speculative generality (interfaces) | |
| Speculative generality (methods) | |

## How does it work?

All of the examples can be found in the test resources directory of the project.

### Data class

By definition, data class is something that only contains data and no other business logic methods.

So, in Java we can define data class as follows:

- A class that has only getters and/or setters
- A class that only has public variables

This narrows down the definition to the following: if class has no custom methods with any other logic that getters/setters, it is a data class code smell.

Plugin analyses a class and nested classes, and looks for getters/setters and then substracts the number of getters + setters from the total method count. If this number is zero, issue is reported to the context of the class.

### Message chains

By definition message chain is a delegation of method calls.

This can be described as a pattern `a -> b -> c -> d` etc.

Plugin analyses method invocations and measures the depth of the calls. 

## Long method

Investigate how many lines does the method have.

Since code follows tree-like structure, we traverse recursively into the tree and count the number of expressions in the tree recursively.

If number of expressions is larger than `X` we report an issue.

## Switch statement

Find all usages of switch statements.

Report all usages of switch statements as an issue.

## Shotgun surgery

Detects methods of a class that are overused by external classes.

## Lazy class

Detects classes that have either:
- 0 methods
- high number of low complexity methods
- tight coupling with external classes and deep inheritance tree

## Long parameter list

Detects all methods with more than `X` parameters

## Blob class
 
## Refused bequest

## Comments

## Cyclic dependencies

## Tradition breaker

## Divergent change

## Feature envy

## Data clumps

## Parallel inheritance hierarchies

## Speculative generality (interfaces)

## Speculative generality (methods)

# To implement

~Distorted hierarchy~

## Primitive obsession

## Middle man

## Inappropriate intimacy

## Alternate classes with different interfaces

## Brain method

## God class

## Intensive coupling

## SAPBreakers

## Distorted Hierarchy

## Unstable dependencies

## Missing template method
