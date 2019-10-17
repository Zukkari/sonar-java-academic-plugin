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
| Method chain | Description can be found [here](https://refactoring.guru/smells/message-chains). This rule has configurable method chain lenghth. This can be configured with property `sonar.android.plugin.message.chain.length` |

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

# To implement

## Long method

**Implementable**: Yes

**Difficulty**: Easy

Look for methods in classes that have more than `X` statements/expressions.
`X` can be defined by the user from the UI.

Can be easily implemented since we can visit all method declarations in a single file.

## Large/blob class

**Implementable**: Yes

**Difficulty**: Medium

Look for classes that have number of variables higher than `X` and number of methods higher than `Y`.
Then calculate cohesion between methods.
Cohesion be calculated by checking if methods have common variables in use.

## Shotgun surgery

**Implementable**: Yes

**Difficulty**: Very hard

Count the callers of method.
If callers > `X` then it is a code smell.

Why is this hard? Because we have context of a single file.
Solution: for every class we analyze its methods and method invocations that are performed
inside those methods.
This allows us to traverse classes only once and fill the gaps later as we perform the check.
Perhaps maybe even investigate scanner for this.

Stateful check.

## Switch statement

**Implementable**: Yes

**Difficulty**: Easy

Detect usage of switch statements and report a problem if collected count is higher than
`X`. 
Check if statement is instance of `SwitchExpressionTree`

## Lazy class

**Implementable**: Yes

**Difficulty**: Hard

Need to check following items for class:

```
    n of methods == 0
    OR
    (n of instructions > A
        AND
    n of weighted methods / n of methods < B)
    OR
    (coupling between object classes < C
        AND
    depth of inheritance > D)
```

where:
- `A` medium number of instructions
- `B` low complexity method ratio
- `C` medium coupling between objects
- `D` depth of inheritance tree

Sonar has already built in complexity calculation so I can look into that.
Depth of inheritance should be trivial to calculate.
Number of statements/expressions is not that hard to calculate also.
Coupling calculation can be found in the source.

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
