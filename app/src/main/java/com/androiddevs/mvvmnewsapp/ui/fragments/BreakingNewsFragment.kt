package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.GravityCompat.apply
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.activities.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewmodels.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.TAG
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println(" I am the beast becoz i am the best")
        println(" Test me right now")

        viewModel =
            (activity as NewsActivity).viewModel //the activity keyword represents the fragment which is casted to an Activity by the as keyword to access viewModel form NewsActivity

        setUpRecyclerView()

        //implementing click listener for each news item displaying on fragment recycler list
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            //navihation component passes the article object from this fragment to another
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articlesFragment, bundle
            )
        }


        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { responseMe ->

            when (responseMe) {

                is Resource.Success -> {
                    hideProgressBar()

                    Log.e(
                        TAG,
                        "the number of news retrieved is: " + responseMe.data?.let { newsResponse -> newsResponse.articles.size })
                    responseMe.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())//mutable list converted to normal list becoz diffutil cannot work with mutable list properly


                        //handling pagination
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLasPage = viewModel.breakingNewsPageNo == totalPages


                        if (isLasPage) {
                            //it reset the clip padding property in xml. Allows the loading bar to occupy its space
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                        }


                    }
                }


                is Resource.Error -> {
                    hideProgressBar()

                    responseMe.message?.let { message ->
                        /*   Log.e(
                               Constants.TAG,
                               "An Error on Breaking News Fragment has Occured : $message"
                           )*/

                        Toast.makeText(activity, "Am error occured $message", Toast.LENGTH_SHORT)
                            .show()

                    }
                }


                is Resource.Loading -> {
                    showProgressBar()

                }
            }

        })


    }


    private fun hideProgressBar() {

        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false

    }

    private fun showProgressBar() {

        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }


    var isLoading = false
    var isLasPage = false
    var isScrolling = false


    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager =
                recyclerView.layoutManager as LinearLayoutManager //coverted to linearlayout manager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotAtBeggining = firstVisibleItemPosition >= 0


            val isNotLoadingAndNotLastPage = !isLoading && !isLasPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeggining && isTotalMoreThanVisible && isScrolling


            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false

            }

        }


        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                //checks whether we are currently scrolling
                isScrolling = true
            }
        }
    }


    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()

        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

    }
}