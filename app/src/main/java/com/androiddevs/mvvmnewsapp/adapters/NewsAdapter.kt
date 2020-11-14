package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticlesViewHolder>() {


    //the class is tasked with calculating the list difference and it is asynchronus it runs in a background thread
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {

            // return oldItem.id==newItem.id// the perfect option but the id is coming from api and some of the data do not have the id
            return oldItem.url == newItem.url// using url becoz its also unique and all data have url

        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {

            return oldItem == newItem
        }
    }

    //the tool that takes the two list and compares them
    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {

        //used to create each of the viewholder list items in the recyclerview


        //this line of code inflates our custom xml layout file
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article_preview, parent, false)

        //the method returns the custom view holders
        return ArticlesViewHolder(view)

    }

    override fun getItemCount(): Int {

        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {

        val article = differ.currentList[position]

        holder.itemView.apply {

            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source!!.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt


            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }

        }
    }

    //click listener methods handled outside the adapter
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }


    inner class ArticlesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

}