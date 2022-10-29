package com.rushi.githubtrending.domain.usecase

import com.rushi.githubtrending.common.Resource
import com.rushi.githubtrending.data.remote.toGithubRepos
import com.rushi.githubtrending.domain.model.GithubRepos
import com.rushi.githubtrending.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetReposUseCase @Inject constructor(
    val repository: MainRepository
) {

    suspend operator fun invoke(dateParam : String) : Flow<Resource<GithubRepos>> {
        return repository.getRepos(dateParam = dateParam)
    }
}