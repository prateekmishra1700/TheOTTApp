package com.example.ottapp2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val context: Context, private val dataList: List<Result>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val overview: TextView = itemView.findViewById(R.id.overview)
        val backdropImage: ImageView = itemView.findViewById(R.id.backdropImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_items, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        holder.title.text = data.title
        holder.overview.text = data.overview

        val imageUrl = "https://image.tmdb.org/t/p/w500" + data.backdrop_path
        Glide.with(context).load(imageUrl).into(holder.backdropImage)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
