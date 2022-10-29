package com.rushi.githubtrending.domain.repository

import com.rushi.githubtrending.common.Resource
import com.rushi.githubtrending.data.remote.GithubReposDto
import com.rushi.githubtrending.domain.model.GithubRepos
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun getRepos(dateParam : String) : Flow<Resource<GithubRepos>>
}