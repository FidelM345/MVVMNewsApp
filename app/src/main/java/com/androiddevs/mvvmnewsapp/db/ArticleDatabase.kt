package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

//the Room DB class must always be abstract. An absract class can never be instantiated. Therefore theere is no object for abstract classes
//abstract classes contain both regular and abstract methods we mainly use
//child classes must always implement / overrride the abstract methods


@Database(
    entities = [Article::class],
    version =2
)

@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {

        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()


        //the function is called any time initialize The article databse
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            //if null synchronize
            //ensures no other threats instantiates the object when we have already done so

            //if instance is still null do the following
            instance?: createDatabase(context).also { instance=it }
        }


        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "article_db.db"
        )
           // .fallbackToDestructiveMigration()
            .build()
    }
}