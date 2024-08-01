package com.vimal.margh.response

import com.vimal.margh.models.ModelSettings

data class ResponseSettings(
    val settings: ModelSettings,
    val status: String
)
