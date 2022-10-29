package com.rushi.githubtrending.data.repository

import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.rushi.githubtrending.data.remote.GithubAPI
import com.rushi.githubtrending.data.remote.GithubReposDto
import com.rushi.githubtrending.domain.model.GithubRepo
import com.rushi.githubtrending.domain.model.GithubRepos
import com.rushi.githubtrending.domain.repository.MainRepository
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class MainRepositoryImplTest {


    lateinit var repository: MainRepository
    lateinit var mockWebServer: MockWebServer
    lateinit var githubAPI: GithubAPI

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        githubAPI = Retrofit.Builder().baseUrl(mockWebServer.url("/").toString())
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GithubAPI::class.java)
        repository = MainRepositoryImpl(githubAPI = githubAPI)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getRepos() = runTest{
        val repos = mutableListOf<GithubRepo>()
        repos.add(GithubRepo("abc","https://www.google.com",3,4,3,"https://www.google.com",false,234))
        val repo = GithubRepos(repos = repos)
        val expectedResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(Gson().toJson(repos))
        mockWebServer.enqueue(expectedResponse)
        val actualResponse = repository.getRepos("q=created:>2022-10-22").asLiveData().value
        val repo1 = actualResponse?.data
        assert(repo == repo1)
    }
}