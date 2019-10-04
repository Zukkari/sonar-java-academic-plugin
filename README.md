[![Build Status](https://travis-ci.org/Zukkari/sonar-java-academic-plugin.svg?branch=master)](https://travis-ci.org/Zukkari/sonar-java-academic-plugin)
[![codecov](https://codecov.io/gh/Zukkari/sonar-java-academic-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/Zukkari/sonar-java-academic-plugin)


# sonar-java-academic-plugin
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
This code smell detects direct call delegation, so if your method contains more logic than just delegation this code smell is not reported.
If call depth is greater than configured depth, issue is reported to the context.
