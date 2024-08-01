package com.vimal.margh.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vimal.margh.adapter.AdapterRadio
import com.vimal.margh.databinding.ActivityRecyclerBinding
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.response.MainViewModel
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Utils

class ActivityCategory : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var adapter: AdapterRadio
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        binding.toolbar.setTitle(intent.getStringExtra(Constant.EXTRA_KEY_NAME).toString())

        val categoryId = intent.getIntExtra(Constant.EXTRA_KEY_ID, 1)
        viewModel.fetchCategoriesDetail(categoryId)

        adapter = AdapterRadio(this)
        binding.rvRecycler.layoutManager = LinearLayoutManager(this)
        binding.rvRecycler.adapter = adapter


        binding.rvRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.layoutManager as LinearLayoutManager
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    viewModel.categoriesDetailLoadMore()
                }
            }
        })

        adapter.setOnItemClickListener(object : AdapterRadio.OnItemClickListener {
            override fun onItemClick(modelRadio: ArrayList<ModelRadio>, position: Int) {
                val intent = Intent(this@ActivityCategory, ActivityPlay::class.java).apply {
                    putExtra(Constant.EXTRA_KEY_ID, modelRadio)
                }
                startActivity(intent)
            }

            override fun onItemMore(modelRadio: ModelRadio) {
                Utils.setBottomSheet(this@ActivityCategory, modelRadio, false)
            }
        })

        viewModel.isLoading.observe(this) { isLoading ->
            binding.ivInclude.pvProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isNoNetwork.observe(this) { isNoNetwork ->
            binding.ivInclude.llErrorNetwork.visibility =
                if (isNoNetwork) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(this) { isEmpty ->
            binding.ivInclude.llErrorEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        binding.ivInclude.btErrorNetwork.setOnClickListener {
            viewModel.fetchCategoriesDetail(categoryId)
        }

        viewModel.categoriesDetail.observe(this) { categories ->
            categories.let {
                adapter.setData(categories)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAfterTransition()
            }
        })
    }
}