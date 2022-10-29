package com.rushi.githubtrending.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GithubAPI {

    @GET("search/repositories")
    suspend fun getRepos(@Query("q") q: String) : GithubReposDto
}