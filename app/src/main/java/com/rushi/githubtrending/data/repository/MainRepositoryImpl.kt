package com.rushi.githubtrending.data.repository

import com.rushi.githubtrending.common.Resource
import com.rushi.githubtrending.data.remote.GithubAPI
import com.rushi.githubtrending.data.remote.GithubReposDto
import com.rushi.githubtrending.data.remote.toGithubRepos
import com.rushi.githubtrending.domain.model.GithubRepos
import com.rushi.githubtrending.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(val githubAPI: GithubAPI) : MainRepository {

    override suspend fun getRepos(dateParam: String): Flow<Resource<GithubRepos>> = flow {
        try {
            emit(Resource.Loading())
            val githubReposDto = githubAPI.getRepos(q = dateParam)
            val githubRepos = githubReposDto.toGithubRepos()
            emit(Resource.Success(data = githubRepos))
            println("Rushi : Response API : ${githubRepos.toString()}")
        }catch (ex : Exception){
            println("Rushi : Error API : ${ex.localizedMessage}")
            emit(Resource.Error(message = ex.localizedMessage ?: "Unexpected error occured"))
        }
    }
}