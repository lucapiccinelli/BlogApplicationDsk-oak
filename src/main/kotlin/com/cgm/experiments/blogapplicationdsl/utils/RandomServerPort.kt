package com.cgm.experiments.blogapplicationdsl.utils

object RandomServerPort : ServerPort {

    override fun value(): Int = (10000..10500).random()

}