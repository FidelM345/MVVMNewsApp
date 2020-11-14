package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article


@Dao
interface ArticleDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long //will update or insert articles in the database


    @Query("SELECT * FROM articles")
    fun getAllArticles():LiveData<List<Article>> //the live data does not work with suspend function is not used
    //the life data updates or notifies all its observers whenever data is changed in the database

    @Delete
    suspend fun deleteArticle(article: Article)

}