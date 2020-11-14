package com.androiddevs.mvvmnewsapp.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

//we can also inherit the ViewModel but since we want to access the application context that we will use for network checking the android view model is preferred
class NewsViewModel( app:Application ,val newsRepository: NewsRepository) : AndroidViewModel(app) {


    //the fragments will subscribe to the live data as observers
    //we are creating the live data object that will emit data
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPageNo = 1
    var breakingNewsResponse: NewsResponse? = null


    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPageNoNo = 1
    var searchNewsResponse: NewsResponse? = null


    init {
        //makes the newtork call and for it to work network permission must be added to the manifest
        getBreakingNews("us")

    }





    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        //the viewmodelscope will ensure the the coroutine is alive as long as the view model is also alive

       safeBreakingNewsCall(countryCode)
    }


    fun searchNews(searchQuery: String) = viewModelScope.launch {
        //the viewmodelscope will ensure the the coroutine is alive as long as the view model is also alive

        safeSearchNewsCall(searchQuery)

    }




    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                breakingNewsPageNo++ //if page has loaded successfully increase no by one. this variable is used for handling pagination

                if (breakingNewsResponse == null) {
                    //run this section when first page is loaded for the first time
                    breakingNewsResponse = resultResponse
                } else {

                    //run this section for other pages
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }

                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }

        return Resource.Error(response.message())
    }



    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                searchNewsPageNoNo++ //if page has loaded successfully increase no by one. this variable is used for handling pagination

                if (searchNewsResponse == null) {
                    //run this section when first page is loaded for the first time
                    searchNewsResponse = resultResponse
                } else {

                    //run this section for other pages
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }

                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }

        return Resource.Error(response.message())
    }


    //dealing with room database

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upDateInsert(article)

    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)

    }




    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())

        try {

            if (hasInternetConnection()){

                val response = newsRepository.getBreakingNews(
                    countryCode,
                    breakingNewsPageNo
                )//we are making the newtork request
                breakingNews.postValue(handleBreakingNewsResponse(response))//we are posting the success or error of our response

            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))

            }


        }catch (t:Throwable){

            when(t){

                is IOException->  breakingNews.postValue(Resource.Error("Network Failure"))
                else->  breakingNews.postValue(Resource.Error("JSON conversion Error"))


            }

        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())

        try {

            if (hasInternetConnection()){

                val response = newsRepository.searchNews(
                    searchQuery,
                    searchNewsPageNoNo
                )//we are making the newtork request
                searchNews.postValue(handleSearchNewsResponse(response))//we are posting the success or error of our response

            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))

            }


        }catch (t:Throwable){

            when(t){

                is IOException->  breakingNews.postValue(Resource.Error("Network Failure"))
                else->  breakingNews.postValue(Resource.Error("JSON conversion Error"))


            }


        }
    }



    //function for checking internet connection
    private fun hasInternetConnection(): Boolean {
                                //this method is only available in the AndroidViewModel and not the normal view model
        val connectivityManager=getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        if (Build.VERSION.SDK_INT>=26){
            val activeNetwork=connectivityManager.activeNetwork?:return false //if null then return false
            val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false

            return  when{
                capabilities.hasTransport(TRANSPORT_WIFI)->true
                capabilities.hasTransport(TRANSPORT_CELLULAR)->true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                capabilities.hasTransport(TRANSPORT_WIFI)->true

                else ->false
            }

        }else
        {
            connectivityManager.activeNetworkInfo?.run {

                return when(type){

                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_WIFI->true
                    else ->false

                }



            }
        }


        return false
    }


}



