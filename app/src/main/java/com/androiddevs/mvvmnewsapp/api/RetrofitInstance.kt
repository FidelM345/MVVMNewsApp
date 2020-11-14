package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {


          //this variable contains the retrofit builder
        private val retrofit by lazy {
            //the lazy keyword initiallizes its contents only once

            //this dependency is used for logging retrofit responses
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)//will show the response body

            //using the logging interceptor for creating the network client
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            //creating The retrofit instance
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//converts the JSON response to kotlin data class objects
                .client(client)
                .build()


        }

        //creating an api instance from the retrofit builder
        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }

    }
}