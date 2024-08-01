package com.vimal.margh.rest

import com.vimal.margh.response.ResponseCategory
import com.vimal.margh.response.ResponseRadio
import com.vimal.margh.response.ResponseSettings
import com.vimal.margh.util.Config
import com.vimal.margh.util.Constant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ApiInterface {

    @Headers(CACHE, AGENT)
    @GET("api.php?radios&api_key=$API_KEY")
    suspend fun getRadios(
        @Query("count") count: Int,
        @Query("page") page: Int,
    ): Response<ResponseRadio>


    @Headers(CACHE, AGENT)
    @GET("api.php?categories&api_key=$API_KEY")
    suspend fun getCategories(): Response<ResponseCategory>


    @Headers(CACHE, AGENT)
    @GET("api.php?search&api_key=$API_KEY")
    suspend fun getSearch(
        @Query("search") search: String?,
        @Query("count") count: Int,
        @Query("page") page: Int,
    ): Response<ResponseRadio>


    @Headers(CACHE, AGENT)
    @GET("api.php?setting&api_key=$API_KEY&package_name=$PACKAGE_NAME")
    suspend fun getSettings(): Response<ResponseSettings>


    @Headers(CACHE, AGENT)
    @GET("api.php?category_detail&api_key=$API_KEY")
    suspend fun getCategoryDetail(
        @Query("id") id: Int,
        @Query("count") count: Int,
        @Query("page") page: Int,
    ): Response<ResponseRadio>


    companion object {
        const val CACHE: String = "Cache-Control: max-age=0"
        const val AGENT: String = "Data-Agent: Your ModelRadio ModelApp"
        const val API_KEY = Config.REST_API_KEY
        const val PACKAGE_NAME = Constant.APPLICATION_ID
    }
}
