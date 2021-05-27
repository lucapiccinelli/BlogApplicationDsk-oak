package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.utils.RandomServerPort
import com.cgm.experiments.blogapplicationdsl.utils.ServerPort

data class FixedServerPort(private val value: Int): ServerPort {
    constructor(): this(RandomServerPort.value())
    override fun value(): Int = value
}