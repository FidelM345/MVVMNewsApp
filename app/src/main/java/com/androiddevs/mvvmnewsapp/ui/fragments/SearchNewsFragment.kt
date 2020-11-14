package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel=(activity as NewsActivity).viewModel //the activity keyword represents the fragment which is casted to an Activity by the as keyword to access viewModel form NewsActivity

        setUpRecyclerView()


        //implementing click listener for each news item displaying on fragment recycler list
        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }

            //navihation component passes the article object from this fragment to another
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articlesFragment,bundle
            )
        }



        //implementing click listener for each news item displaying on fragment recycler list
        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }

            //navihation component passes the article object from this fragment to another
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articlesFragment,bundle
            )
        }




        //deals with the search view component
        var job: Job?=null

        etSearch.addTextChangedListener{editable->
            job?.cancel()//current job is cancelled whenever a new text is added

            //Mainscope- id current job has not been canceled execute the code below
            job= MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {

                    if (editable.toString().isNotEmpty()){

                        Log.e(Constants.TAG, "Value on search view is ${editable} " )
                        //search for the news only if the edit text is not empty
                        viewModel.searchNews(editable.toString())
                    }
                }
            }

        }



        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
                responseMe ->

            when(responseMe){

                is Resource.Success->{
                    hideProgressBar()

                    responseMe.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //handling pagination
                        val totalPages=newsResponse.totalResults/Constants.QUERY_PAGE_SIZE+2
                        isLasPage=viewModel.searchNewsPageNoNo==totalPages

                        if (isLasPage){
                            //it reset the clip padding property in xml. Allows the loading bar to occupy its space
                            rvSearchNews.setPadding(0,0,0,0)
                        }


                    }
                }


                is Resource.Error->{
                    hideProgressBar()

                    responseMe.message?.let {message ->
                         Log.e(Constants.TAG, "An Error on Search News Fragment has Occured : ${message.toString()}" )
                        Toast.makeText(activity, "Am error occured $message", Toast.LENGTH_SHORT).show()
                    }
                }


                is Resource.Loading->{
                    showProgressBar()

                }
            }

        })


    }



    private fun hideProgressBar() {

        paginationProgressBar.visibility=View.INVISIBLE
        isLoading=false

    }

    private fun showProgressBar() {

        paginationProgressBar.visibility=View.VISIBLE
        isLoading=true

    }



    var isLoading=false
    var isLasPage=false
    var isScrolling=false



    val scrollListener=object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager=recyclerView.layoutManager as LinearLayoutManager //coverted to linearlayout manager
            val firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount
            val isNotAtBeggining=firstVisibleItemPosition>=0


            val isNotLoadingAndNotLastPage=!isLoading&&!isLasPage
            val isAtLastItem=firstVisibleItemPosition+visibleItemCount>=totalItemCount
            val isTotalMoreThanVisible=totalItemCount>=Constants.QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotLastPage&&isAtLastItem&&isNotAtBeggining&&isTotalMoreThanVisible&&isScrolling


            if (shouldPaginate){
                viewModel.getBreakingNews(etSearch.text.toString())
                isScrolling=false
            }


        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                //checks whether we are currently scrolling

                isScrolling=true

            }
        }
    }



    private  fun  setUpRecyclerView(){
        newsAdapter= NewsAdapter()

        rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }


}