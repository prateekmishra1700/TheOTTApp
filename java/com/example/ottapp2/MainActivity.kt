

package com.example.ottapp2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ottapp2.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var viewPager: ViewPager
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<ImageView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var coroutineScope: CoroutineScope

    private var imageList = intArrayOf(R.drawable.image1, R.drawable.image2, R.drawable.image3) // Replace with your image resources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvRecycleView
        recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)

        // Initialize ViewPager with adapter
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        // Add indicator dots
        val dotsCount = imageList.size
        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_unselected))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dotsLayout.addView(dots[i], params)
        }

        dots[0]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_selected))

        // ViewPager page change listener to update dots
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                updateDots(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        // Initialize coroutine scope
        coroutineScope = CoroutineScope(Dispatchers.Main)

        // Insert images into Room Database
        coroutineScope.launch(Dispatchers.IO) {
            val imageDao = MyApp.database.imageDao()
            for (imageResId in imageList) {
                val imageEntity = ImageEntity(imageResId = imageResId)
                imageDao.insertImage(imageEntity)
            }
        }

        // Fetch images from Room Database and update ViewPager
        coroutineScope.launch(Dispatchers.IO) {
            val imageDao = MyApp.database.imageDao()
            val images = imageDao.getAllImages()
            withContext(Dispatchers.Main) {
                viewPagerAdapter.setImages(images)
            }
        }

        // Fetch data from API
        getMyData()
    }

    private fun getMyData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.getTrendingMovies("fd03126398e5e4267ebe197e36777a0b")

        call.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.results ?: emptyList()
                    Log.d("MainActivity", "Data fetched: $responseBody")
                    myAdapter = MyAdapter(this@MainActivity, responseBody)
                    recyclerView.adapter = myAdapter
                } else {
                    Log.e("MainActivity", "Failed to get data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                Log.e("MainActivity", "onFailure: ${t.message}")
            }
        })
    }

    private fun updateDots(position: Int) {
        for (i in dots.indices) {
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_unselected))
        }
        if (position >= 0 && position < dots.size) {
            dots[position]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_selected))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel the coroutine scope on activity destroy
    }
}


//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var myAdapter: MyAdapter
//    private lateinit var linearLayoutManager: LinearLayoutManager
//    private lateinit var viewPager: ViewPager
//    private lateinit var dotsLayout: LinearLayout
//    private lateinit var dots: Array<ImageView?>
//    private lateinit var viewPagerAdapter: ViewPagerAdapter
//    private lateinit var coroutineScope: CoroutineScope
//
//    private var imageList = intArrayOf(R.drawable.image1, R.drawable.image2, R.drawable.image3) // Replace with your image resources
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        recyclerView = binding.rvRecycleView
//        recyclerView.setHasFixedSize(true)
//        linearLayoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = linearLayoutManager
//
//        viewPager = findViewById(R.id.viewPager)
//        dotsLayout = findViewById(R.id.dotsLayout)
//
//        // Initialize ViewPager with adapter
//        viewPagerAdapter = ViewPagerAdapter(this)
//        viewPager.adapter = viewPagerAdapter
//
//        // Add indicator dots
//        val dotsCount = imageList.size
//        dots = arrayOfNulls(dotsCount)
//
//        for (i in 0 until dotsCount) {
//            dots[i] = ImageView(this)
//            dots[i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_unselected))
//            val params = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            params.setMargins(8, 0, 8, 0)
//            dotsLayout.addView(dots[i], params)
//        }
//
//        dots[0]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_selected))
//
//        // ViewPager page change listener to update dots
//        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//
//            override fun onPageSelected(position: Int) {
//                for (i in 0 until dotsCount) {
//                    dots[i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_unselected))
//                }
//                dots[position]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_dot_selected))
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {}
//        })
//
//        // Initialize coroutine scope
//        coroutineScope = CoroutineScope(Dispatchers.Main)
//
//        // Insert images into Room Database
//        coroutineScope.launch(Dispatchers.IO) {
//            val imageDao = MyApp.database.imageDao()
//            for (imageResId in imageList) {
//                val imageEntity = ImageEntity(imageResId = imageResId)
//                imageDao.insertImage(imageEntity)
//            }
//        }
//
//        // Fetch images from Room Database and update ViewPager
//        coroutineScope.launch(Dispatchers.IO) {
//            val imageDao = MyApp.database.imageDao()
//            val images = imageDao.getAllImages()
//            withContext(Dispatchers.Main) {
//                viewPagerAdapter.setImages(images)
//            }
//        }
//
//        // Fetch data from API
//        getMyData()
//    }
//
//    private fun getMyData() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiInterface = retrofit.create(ApiInterface::class.java)
//        val call = apiInterface.getTrendingMovies("fd03126398e5e4267ebe197e36777a0b")
//
//        call.enqueue(object : Callback<MyData> {
//            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()?.results ?: emptyList()
//                    Log.d("MainActivity", "Data fetched: $responseBody")
//                    myAdapter = MyAdapter(this@MainActivity, responseBody)
//                    recyclerView.adapter = myAdapter
//                } else {
//                    Log.e("MainActivity", "Failed to get data: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<MyData>, t: Throwable) {
//                Log.e("MainActivity", "onFailure: ${t.message}")
//            }
//        })
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        coroutineScope.cancel() // Cancel the coroutine scope on activity destroy
//    }
//}
