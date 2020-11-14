package com.androiddevs.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.models.Source

class Converters {

    //the conveeters are used because room only accepts primitive data types and Source is a class type data structure
    @TypeConverter
    fun  fromSource(source: Source):String{

        return  source.name
    }


    @TypeConverter
    fun  toSource(name:String): Source {

        return Source(name, name)
    }

}