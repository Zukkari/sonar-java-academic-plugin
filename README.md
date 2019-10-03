[![Build Status](https://travis-ci.org/Zukkari/sonar-android-plugin.svg?branch=master)](https://travis-ci.org/Zukkari/sonar-android-plugin)
[![codecov](https://codecov.io/gh/Zukkari/sonar-android-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/Zukkari/sonar-android-plugin)


# sonar-android-plugin
Sonar plugin that can detect code smells in Android applications

Currently only Java language is supported.

## Supported code smells

Current list of supported code smells:

| Code smell | Description | 
| :---: | :---: |
| Data class | Description can be found [here](https://refactoring.guru/smells/data-class) |
| Method chain | Description can be found [here](https://refactoring.guru/smells/message-chains). This rule has configurable method chain lenghth. This can be configured with property `sonar.android.plugin.message.chain.length` |
