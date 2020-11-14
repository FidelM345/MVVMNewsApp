package com.androiddevs.mvvmnewsapp.ui.viewmodels.providerfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.ui.viewmodels.NewsViewModel


//parameters cannot be passed directly to viewModels hence the use of view models
class NewsViewModelProviderFactory(val app:Application,val newsRepository: NewsRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return NewsViewModel(app,newsRepository) as T
    }


}