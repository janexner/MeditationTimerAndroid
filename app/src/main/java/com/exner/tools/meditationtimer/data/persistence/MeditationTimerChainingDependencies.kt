package com.exner.tools.meditationtimer.data.persistence

class MeditationTimerChainingDependencies(
    var changed: Boolean,
    var dependentProcessIdsAndNames: List<MeditationTimerDataIdAndName>
)