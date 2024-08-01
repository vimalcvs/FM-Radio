package com.vimal.margh.response

import com.vimal.margh.models.ModelCategory

data class ResponseCategory(
    val status: String,
    val count: Int,
    val categories: List<ModelCategory>
)