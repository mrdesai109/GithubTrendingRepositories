package com.rushi.githubtrending.domain.model

data class GithubRepos(
    val repos : List<GithubRepo>
)

data class GithubRepo(
    val name: String,
    val avatarURL: String,
    val forks: Int,
    val openIssues: Int,
    val watchers: Int,
    val htmlURL: String,
    var isSelected: Boolean = false,
    val uid: Long = (0..1000000000).random().toLong()
)
