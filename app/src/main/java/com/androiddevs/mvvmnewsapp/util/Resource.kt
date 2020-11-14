package com.androiddevs.mvvmnewsapp.util

//class used to handle network request
//used to wrap around our network request and to handle our success, loading and error state
 sealed class Resource<T>(
    val data: T?=null,
    val  message:String?=null

) {

    //The T mean Type type parameters

/*    class Box<T>(t: T) {
        var value = t
    }
    val box: Box<Int> = Box<Int>(1)*/

    //A sealed class is like an abstract class but now we can define who can inherit from that class


    //only these classes can inherit from the Resource class
    class Success<T>(data: T):Resource<T>(data)
    class  Error<T>(message:String,data: T?=null):Resource<T>(data,message)
    class  Loading<T>():Resource<T>()



}