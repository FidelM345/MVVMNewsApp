package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.activities.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticlesFragment: Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    val args: ArticlesFragmentArgs by navArgs() //receives the argurnments from the navigation component

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel //the activity keyword represents the fragment which is casted to an Activity by the as keyword to access viewModel form NewsActivity


        val article=args.article //receives articles from the argurments


        webView.apply {
            webViewClient= WebViewClient(); //ensures content open in the apps internal browser and not an external browser
            loadUrl(article.url)
        }


        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_LONG).show()
        }

    }


}