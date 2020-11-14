package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.accessibility.AccessibilityEventCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.activities.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragemnt : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                R.id.action_savedNewsFragemnt_to_articlesFragment, bundle
            )
        }





       //call back for handling recycler view swipes

        val  ItemTouchHelperCallBack=object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val  position=viewHolder.adapterPosition//gets position of the item being currently swiped
                val article=newsAdapter.differ.currentList[position]//gets article from the db
                viewModel.deleteArticle(article)//deletes article from the db

                Snackbar.make(view,"Successfully deleted the article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                        //when undo is selected the item will be saved again in the database
                    }

                }.show()


            }
        }

        //attaches the callbacks to the recycler views
        ItemTouchHelper(ItemTouchHelperCallBack).apply {
            attachToRecyclerView(rvSavedNews)
        }





        //observe changes from the room database
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articleList ->

            newsAdapter.differ.submitList(articleList)
        })


    }


    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()

        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }

}