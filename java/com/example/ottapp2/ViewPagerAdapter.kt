package com.example.ottapp2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter


class ViewPagerAdapter(private val context: Context) : PagerAdapter() {

    private var images: List<ImageEntity> = emptyList()

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.pager_item, container, false)

        val imageView = itemView.findViewById<ImageView>(R.id.imageView)

        // Load image using Glide, Picasso, or any other image loading library here
        imageView.setImageResource(images[position].imageResId)

        container.addView(itemView)

        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun setImages(images: List<ImageEntity>) {
        this.images = images
        notifyDataSetChanged()
    }
}
