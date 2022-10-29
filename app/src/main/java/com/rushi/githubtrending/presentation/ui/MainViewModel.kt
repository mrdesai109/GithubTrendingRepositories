package com.rushi.githubtrending.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rushi.githubtrending.common.Resource
import com.rushi.githubtrending.domain.model.GithubRepo
import com.rushi.githubtrending.domain.model.GithubRepos
import com.rushi.githubtrending.domain.usecase.GetReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(val getReposUseCase: GetReposUseCase) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _repos = MutableStateFlow(GithubRepos(repos = emptyList()))
    val repos = _repos.asStateFlow()

    var searchList = mutableListOf<GithubRepo>()
    val searchQuery = MutableStateFlow("")
    var isSearchOn = false
    val searchListFlow = searchQuery.flatMapLatest {
        if (it.isEmpty()) {
            searchList = mutableListOf()
            isSearchOn = false
            getCurrentRepos()
        } else {
            isSearchOn = true
            filterCurrentRepos(it)
        }
    }

    private val _errorChannel = Channel<String>()
    val errorChannelAsFlow = _errorChannel.receiveAsFlow()

    init {
        getRepos()
    }


    fun getRepos() {
        if(_isLoading.value==false){
            val date = getCalculatedDate("yyyy-MM-dd", -7)
            val dateQuery = "created:>$date"
            viewModelScope.launch {
                getReposUseCase(dateQuery).collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            _isLoading.value = false
                            val gitgubRepos1 = it.data ?: GithubRepos(repos = emptyList())
                            _repos.value = gitgubRepos1
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _errorChannel.send(it.message ?: "Unexpected error occured")
                        }
                        is Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            }
        }else{
            viewModelScope.launch {
                _errorChannel.send("Getting Repositories. Please wait.")
            }
        }
    }

    fun getCalculatedDate(dateFormat: String?, days: Int): String? {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    fun toggleSelectionForItem(
        uid1: Long,
        notifySelection: (Int, List<GithubRepo>) -> Unit
    ) {
        val currentRepo = _repos.value
        val currentRepos = currentRepo.repos
        if (isSearchOn == false) {
            val targetIndex = currentRepos.indexOfFirst {
                it.uid == uid1
            }
            if (targetIndex == -1) {
                return
            }
            currentRepos[targetIndex].isSelected =
                if (currentRepos[targetIndex].isSelected) false else true
            _repos.value = GithubRepos(repos = currentRepos)
            notifySelection(targetIndex, mutableListOf()) //pass index for master list
        }
    }

    fun filterCurrentRepos(query: String): Flow<List<GithubRepo>> = flow {
        val currentRepos = _repos.value.repos
        if (currentRepos.isEmpty()) {
            emit(emptyList())
        } else {
            val searchList1 = mutableListOf<GithubRepo>()
            currentRepos.forEach {
                if (it.name.lowercase().contains(query.lowercase())) {
                    searchList1.add(it)
                }
            }
            searchList = searchList1
            emit(searchList1)
        }
    }

    fun getCurrentRepos(): Flow<List<GithubRepo>> = flow {
        emit(_repos.value.repos)
    }
}