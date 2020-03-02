package io.github.zukkari.config

case class ConfigurationProperty(
    key: String,
    description: String,
    name: String,
    defaultValue: String,
    array: Boolean = false
)

object ConfigurationProperties {
  val ALTERNATIVE_CLASSES_MIN_PARAM_COUNT: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.alternative.classes.min.parameter.count",
      "Include only member with minimum number of parameters in the analysis",
      "Alternative classes with different interfaces: minimum parameter count",
      "2"
    )

  val ALTERNATIVE_CLASSES_MIN_COMMON_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.alternative.classes.min.common.methods",
      "Minimum number of common methods when to report an issue",
      "Alternative classes with different interfaces: minimum common method count",
      "2"
    )

  val BLOB_CLASS_NUM_OF_VARIABLES: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.blob.class.variable.count",
      "Number of attributes to consider as high number of attributes",
      "Blob class: high number of variables",
      "13"
    )

  val BLOB_CLASS_NUM_OF_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.blob.class.method.count",
      "Number of methods to consider as high number of methods",
      "Blob class: high number of methods",
      "22")

  val BLOB_CLASS_LACK_OF_COHESION: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.blob.class.lack.of.cohesion",
      "Cohesion number that will be considered as low cohesion value",
      "Blob class: lack of cohesion",
      "40"
    )

  val BRAIN_METHOD_HIGH_NUMBER_OF_LOC: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.brain.method.high.loc",
      "Number of lines of code that will be considered as high number of LOC",
      "Brain method: high number of LOC",
      "130"
    )

  val BRAIN_METHOD_HIGH_CYCLOMATIC_COMPLEXITY: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.brain.method.high.complexity",
      "Number that will be considered as high complexity for methods",
      "Brain method: high complexity number",
      "31"
    )

  val BRAIN_METHOD_HIGH_NESTING_DEPTH: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.brain.method.high.nesting.depth",
      "Number that will be considered as high nesting depth for methods",
      "Brain method: high nesting depth",
      "3"
    )

  val BRAIN_METHOD_MANY_ACCESSED_VARIABLES: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.brain.method.many.accessed.variables",
      "Number that will be considered as high number accessed variables",
      "Brain method: many accessed variables",
      "7"
    )

  val DATA_CLUMP_COMMON_VARIABLES: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.data.clump.common.variables",
      "Number of common variables to consider when reporting an issue",
      "Data clump: number of common variables",
      "3"
    )

  val DIVERGENT_CHANGE_METHOD_CALLS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.method.calls",
      "Number of methods calls to consider when report an issue",
      "Divergent change: high number of method calls",
      "20")

  val FEATURE_ENVY_LOCALITY_THRESHOLD: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.locality.threshold",
      "Locality threshold to consider when reporting an issue",
      "Feature envy: locality threshold",
      "0.33")

  val FEATURE_ENVY_ACCESS_TO_FOREIGN_VARIABLES: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.access.foreign.variables",
      "Number of foreign variable accesses to consider when reporting an issue",
      "Feature envy: high number of foreign variable access",
      "2"
    )

  val FEATURE_ENVY_ACCESS_TO_FOREIGN_CLASSES: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.access.foreign.classes",
      "Number of foreign class accesses to consider when reporting an issue",
      "Feature envy: high number of foreign class access",
      "2"
    )

  val GOD_CLASS_ACCESS_TO_FOREIGN_DATA: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.access.foreign.data",
      "Number of accesses to foreign data to consider when reporting an issue",
      "God class: high accesses to foreign data",
      "5"
    )

  val GOD_CLASS_TIGHT_COHESION: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.tight.cohesion",
      "High class cohesion to consider when reporting an issue",
      "God class: tight cohesion",
      "0.33")

  val GOD_CLASS_CLASS_COMPLEXITY: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.class.complexity",
      "High class complexity to consider when reporting an issue",
      "God class: high class complexity",
      "47")

  val INAPPROPRIATE_INTIMACY_NUMBER_OF_CALLS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.inappropriate.intimacy.number.of.calls",
      "Number of method calls to consider when reporting an issue",
      "Inappropriate intimacy: high number of method calls",
      "4"
    )

  val INTENSIVE_COUPLING_CALLED_METHOD_COUNT: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.intensive.coupling.method.count",
      "Method count to consider when reporting an issue",
      "Intensive coupling: high method count",
      "7"
    )

  val INTENSIVE_COUPLING_HALF_COUPLING_DISPERSION: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.intensive.coupling.half.coupling.dispersion",
      "Half dispersion to consider when reporting an issue",
      "Intensive coupling: half coupling dispersion",
      "0.5"
    )

  val INTENSIVE_COUPLING_QUARTER_COUPLING_DISPERSION: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.intensive.coupling.quarter.coupling.dispersion",
      "Quarter dispersion to consider when reporting an issue",
      "Intensive coupling: quarter coupling dispersion",
      "0.25"
    )

  val INTENSIVE_COUPLING_COUPLING_INTENSITY: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.intensive.coupling.coupling.intensity",
      "Coupling intensity to consider when reporting an issue",
      "Intensive coupling: coupling intensity",
      "2"
    )

  val INTENSIVE_COUPLING_NESTING_DEPTH: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.intensive.coupling.nesting.depth",
      "Method nesting depth to consider when reporting an issue",
      "Intensive coupling: nesting depth",
      "1"
    )

  val LAZY_CLASS_MIN_NUMBER_OF_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.lazy.class.number.of.methods",
      "Minimum number of methods to consider when reporting an issue",
      "Lazy class: minimum number of methods",
      "0"
    )

  val LAZY_CLASS_MEDIUM_NUMBER_OF_INSTRUCTIONS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.lazy.class.number.of.instructions",
      "Medium number of instruction to consider when reporting an issue",
      "Lazy class: medium number of instructions",
      "50"
    )

  val LAZY_CLASS_LOW_COMPLEXITY_METHOD_RATIO: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.lazy.class.low.complexity.method.ratio",
      "Low complexity method ratio to consider when reporting an issue",
      "Lazy class: low complexity method ratio",
      "2.0"
    )

  val LAZY_CLASS_DEPTH_OF_INHERITANCE: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.lazy.class.depth.inheritance",
      "Depth of inheritance to consider when reporting an issue",
      "Lazy class: depth of inheritance",
      "2"
    )

  val LAZY_CLASS_COUPLING_BETWEEN_OBJECTS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.lazy.class.coupling.between.classes",
      "Coupling to consider when reporting an issue",
      "Lazy class: coupling between objects ratio",
      "3"
    )

  val LONG_METHOD_METHOD_LENGTH: ConfigurationProperty =
    ConfigurationProperty("sonar.academic.plugin.long.method.length",
                          "Number of lines to consider when reporting an issue",
                          "Long method: number of lines",
                          "26")

  val LONG_PARAMETER_LIST_PARAMETER_COUNT: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.long.parameter.list.parameter.count",
      "High number of method parameters to consider when reporting an issue",
      "Long parameter list: high number of method parameters",
      "9"
    )

  val MESSAGE_CHAIN_LENGTH: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.message.chain.length",
      "Message chain length to consider when reporting an issue",
      "Message chain: message chain length",
      "3")

  val MIDDLE_MAN_DELEGATE_RATIO: ConfigurationProperty =
    ConfigurationProperty("sonar.academic.plugin.middle.man.delegate.ratio",
                          "Delegate ratio to consider when reporting an issue",
                          "Middle man: delegate ratio",
                          "0.5")

  val PARALLEL_INHERITANCE_HIERARCHIES_PREFIX_LENGTH: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.parallel.inheritance.hierarchies.prefix.length",
      "Class name prefix to consider when reporting an issue",
      "Parallel inheritance hierarchies: class name prefix",
      "1"
    )

  val PARALLEL_INHERITANCE_HIERARCHIES_HIERARCHY_DEPTH: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.parallel.inheritance.hierarchies.hierarchy.depth",
      "Hierarchy depth to consider when reporting an issue",
      "Parallel inheritance hierarchies: hierarchy depth",
      "5"
    )

  val PRIMITIVE_OBSESSION_PRIMITIVE_TIMES_USED: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.primitive.obsession.primitive.times.used",
      "Primitive usage count to consider when reporting an issue",
      "Primitive obsession: primitive usage count",
      "3"
    )

  val PRIMITIVE_OBSESSION_IGNORED_PACKAGES: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.primitive.obsession.ignored.packages",
      "Packages to ingore when performing primitive obsession check",
      "Primitive obsession: packages to ignore",
      "java.",
      array = true
    )

  val REFUSED_BEQUEST_NUMBER_OF_PROTECTED_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.refused.bequest.number.protected.methods",
      "Number of protected methods to consider when reporting an issue",
      "Refused bequest: number of protected methods",
      "3"
    )

  val REFUSED_BEQUEST_BASE_CLASS_USAGE_RATIO: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.refused.bequest.base.class.usage.ratio",
      "Base class usage ratio to consider when reporting an issue",
      "Refused bequest: base class usage ratio",
      "0.33"
    )

  val REFUSED_BEQUEST_BASE_CLASS_OVERRIDE_RATIO: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.refused.bequest.base.class.override.ratio",
      "Base class override ratio to consider when reporting an issue",
      "Refused bequest: base class override ratio",
      "0.33"
    )

  val REFUSED_BEQUEST_AVERAGE_METHOD_WEIGHT: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.refused.bequest.average.method.weight",
      "Average method weight to consider when reporting an issue",
      "Refused bequest: average method weight",
      "2"
    )

  val REFUSED_BEQUEST_WEIGHTED_METHOD_COUNT: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.refused.bequest.weighted.method.count",
      "Weighted method count to consider when reporting an issue",
      "Refused bequest: weighted method count",
      "14"
    )

  val REFUSED_BEQUEST_NUMBER_OF_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.refused.bequest.number.of.methods",
      "Number of methods to consider when reporting an issue",
      "Refused bequest: number of methods",
      "7"
    )

  val SHOTGUN_SURGERY_INVOCATION_COUNT: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.shotgun.surgery.invocation.count",
      "Number of invocations to consider when reporting an issue",
      "Shotgun surgery: number of invocations",
      "3"
    )

  val SWISS_ARMY_KNIFE_HIGH_NUMBER_OF_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.swiss.army.knife.high.number.of.methods",
      "Number of methods to consider when reporting an issue",
      "Swiss army knife: number of methods",
      "13"
    )

  val TRADITION_BREAKER_HIGH_NUMBER_OF_MEMBERS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.tradition.breaker.high.number.of.members",
      "High number of members to consider when reporting an issue",
      "Tradition breaker: high number of members",
      "20"
    )

  val TRADITION_BREAKER_LOW_NUMBER_OF_MEMBERS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.tradition.breaker.low.number.of.members",
      "Low number of members to consider when reporting an issue",
      "Tradition breaker: Low number of members",
      "5"
    )

  val MISSING_TEMPLATE_METHOD_COMMON_MEMBERS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.missing.template.method.common.members",
      "Number of common variables and methods to consider when reporting an issue",
      "Missing template method: number of common variables and method invocations",
      "5"
    )

  val MISSING_TEMPLATE_METHOD_COMMON_METHODS: ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.missing.template.method.common.methods",
      "Number of methods that should have common variable and method invocations",
      "Missing template method: number of methods",
      "2"
    )

  val STABLE_ABSTRACTION_BREAKER_ALLOWED_DISTANCE_FROM_MAIN
    : ConfigurationProperty =
    ConfigurationProperty(
      "sonar.academic.plugin.stable.abstraction.breaker.allowed.distance",
      "Distance allowed in calculation for stable abstraction breaker",
      "Stable abstraction breaker: distance",
      "0.5"
    )

  val properties = List(
    ALTERNATIVE_CLASSES_MIN_PARAM_COUNT,
    ALTERNATIVE_CLASSES_MIN_COMMON_METHODS,
    BLOB_CLASS_NUM_OF_VARIABLES,
    BLOB_CLASS_NUM_OF_METHODS,
    BLOB_CLASS_LACK_OF_COHESION,
    BRAIN_METHOD_HIGH_NUMBER_OF_LOC,
    BRAIN_METHOD_HIGH_CYCLOMATIC_COMPLEXITY,
    BRAIN_METHOD_HIGH_NESTING_DEPTH,
    BRAIN_METHOD_MANY_ACCESSED_VARIABLES,
    DATA_CLUMP_COMMON_VARIABLES,
    DIVERGENT_CHANGE_METHOD_CALLS,
    FEATURE_ENVY_LOCALITY_THRESHOLD,
    FEATURE_ENVY_ACCESS_TO_FOREIGN_VARIABLES,
    FEATURE_ENVY_ACCESS_TO_FOREIGN_CLASSES,
    GOD_CLASS_ACCESS_TO_FOREIGN_DATA,
    GOD_CLASS_TIGHT_COHESION,
    GOD_CLASS_CLASS_COMPLEXITY,
    INAPPROPRIATE_INTIMACY_NUMBER_OF_CALLS,
    INTENSIVE_COUPLING_CALLED_METHOD_COUNT,
    INTENSIVE_COUPLING_HALF_COUPLING_DISPERSION,
    INTENSIVE_COUPLING_QUARTER_COUPLING_DISPERSION,
    INTENSIVE_COUPLING_COUPLING_INTENSITY,
    INTENSIVE_COUPLING_NESTING_DEPTH,
    LAZY_CLASS_MIN_NUMBER_OF_METHODS,
    LAZY_CLASS_MEDIUM_NUMBER_OF_INSTRUCTIONS,
    LAZY_CLASS_LOW_COMPLEXITY_METHOD_RATIO,
    LAZY_CLASS_DEPTH_OF_INHERITANCE,
    LAZY_CLASS_COUPLING_BETWEEN_OBJECTS,
    LONG_METHOD_METHOD_LENGTH,
    LONG_PARAMETER_LIST_PARAMETER_COUNT,
    MESSAGE_CHAIN_LENGTH,
    MIDDLE_MAN_DELEGATE_RATIO,
    PARALLEL_INHERITANCE_HIERARCHIES_PREFIX_LENGTH,
    PARALLEL_INHERITANCE_HIERARCHIES_HIERARCHY_DEPTH,
    PRIMITIVE_OBSESSION_PRIMITIVE_TIMES_USED,
    PRIMITIVE_OBSESSION_IGNORED_PACKAGES,
    REFUSED_BEQUEST_NUMBER_OF_PROTECTED_METHODS,
    REFUSED_BEQUEST_BASE_CLASS_USAGE_RATIO,
    REFUSED_BEQUEST_BASE_CLASS_OVERRIDE_RATIO,
    REFUSED_BEQUEST_AVERAGE_METHOD_WEIGHT,
    REFUSED_BEQUEST_WEIGHTED_METHOD_COUNT,
    REFUSED_BEQUEST_NUMBER_OF_METHODS,
    SHOTGUN_SURGERY_INVOCATION_COUNT,
    SWISS_ARMY_KNIFE_HIGH_NUMBER_OF_METHODS,
    TRADITION_BREAKER_HIGH_NUMBER_OF_MEMBERS,
    TRADITION_BREAKER_LOW_NUMBER_OF_MEMBERS,
    MISSING_TEMPLATE_METHOD_COMMON_MEMBERS,
    MISSING_TEMPLATE_METHOD_COMMON_METHODS
  )
}
