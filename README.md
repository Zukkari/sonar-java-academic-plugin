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

# To implement

## Large/blob class

**Implementable**: Yes

**Difficulty**: Medium

Look for classes that have number of variables higher than `X` and number of methods higher than `Y`.
Then calculate cohesion between methods.
Cohesion be calculated by checking if methods have common variables in use.

## Refused bequest / refused parent bequest

**Implementable**: Yes

**Difficulty**: Very hard

Detect classes that do not use protected methods of its parent.

Link for this definition can be found here [here](https://www.simpleorientedarchitecture.com/how-to-identify-refused-parent-bequest-using-ndepend/).

It is possible to access parent context, implement this as just all other checks.

## Comments

**Implementable**: Yes

**Difficulty**: Easy

Should be pretty easy to implement.
Sonar can already detect comments in some cases.
Check how that is implemented.
Worst case scenario - implement via regex/grepping.
Report issues when number of comments > `X`

## Cyclic dependencies (classes)

**Implementable**: Yes

**Difficulty**: Hard

Detect cycle between classes.
Sounds not that hard, check class fields and see if classes have similar
fields.
On the other hand this is another stateful check.
Look into sensor for this one as well.

## Cyclic dependencies (modules)

**Implementable**: No

**Difficulty**: ???

Module dependencies are not possible in Java.
Maven refuses to build such projects, I assume Gradle too since its not clear
what project to build first.

## Distorted hierarchy

**Implementable**: ???

**Difficulty**: ???

## Tradition breaker

**Implementable**: ???

**Difficulty**: ???

## Divergent change

## Feature envy

## Data clumps

## Primitive obsession

## Parallel inheritance hierarchies

## Speculative generality

## Temporary field

## Middle man

## Inappropriate intimacy

## Alternate classes with different interfaces

## Incomplete library class

## Refused bequest

## Brain method

## God class

## Intensive coupling

## SAPBreakers

## Distorted Hierarchy

## Unstable dependencies
