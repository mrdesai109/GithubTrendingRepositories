package com.rushi.githubtrending.presentation.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rushi.githubtrending.common.Utils
import com.rushi.githubtrending.databinding.ActivityMainBinding
import com.rushi.githubtrending.presentation.adapter.MainRVAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    var isOrientationLand = false

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var applicationScope: CoroutineScope

    lateinit var mainRVAdapter: MainRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        collectFlows()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun init() {
        supportActionBar?.setTitle(
            Html.fromHtml(
                "<font color='#ffffff'>" + "Github Trending" + "</font>",
                Html.FROM_HTML_MODE_LEGACY
            )
        )
        isOrientationLand =
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainRVAdapter = MainRVAdapter()
        binding.apply {
            mainRV.apply {
                adapter = mainRVAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            fab.setOnClickListener {
                mainViewModel.getRepos()
            }
        }
        mainRVAdapter.onItemClick = { repo, pos, uid ->
            mainViewModel.toggleSelectionForItem(uid) { pos1, list ->
                println("Rushi : Index Check : $pos1")
                if (mainViewModel.isSearchOn == false) {
                    mainRVAdapter.notifyItemChanged(pos1)
                }
            }
        }

    }

    fun collectFlows() {
        applicationScope.launch {
            mainViewModel.isLoading.collectLatest { isLoading ->
                withContext(Dispatchers.Main) {
                    binding.pb.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
        applicationScope.launch {
            mainViewModel.repos.collectLatest {
                if(mainViewModel.isSearchOn){
                    return@collectLatest
                }
                mainRVAdapter.submitList(it.repos)
            }
        }
        applicationScope.launch {
            mainViewModel.errorChannelAsFlow.collectLatest {
                withContext(Dispatchers.Main) {
                    Utils.showSnackBar(binding.mainRV.rootView, it)
                }
            }
        }
        applicationScope.launch {
            mainViewModel.searchListFlow.collectLatest {
                println("Rushi : SearchListFlow Collect")
                it.forEach {
                    println("Rushi : SearchListFlow Collect : ${it.isSelected} ${it.name}")
                }
                mainRVAdapter.submitList(it)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.rushi.githubtrending.R.menu.main_menu, menu)
        val searchItem = menu?.findItem(com.rushi.githubtrending.R.id.search_item)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = Html.fromHtml(
            "<font color = #50d3d3d3>" + "Search Repository Name.." + "</font>",
            Html.FROM_HTML_MODE_LEGACY
        )
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(ContextCompat.getColor(this, com.rushi.githubtrending.R.color.white))
        val searchClose: ImageView =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.setImageResource(com.rushi.githubtrending.R.drawable.ic_baseline_clear_24)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.searchQuery.value = newText.orEmpty()
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isOrientationLand =
            newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        println("Rushi : IsOrientation_Landscape : ${isOrientationLand}")
    }
}