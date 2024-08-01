package com.vimal.margh.activity


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vimal.margh.adapter.AdapterRadio
import com.vimal.margh.databinding.ActivitySearchBinding
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.response.MainViewModel
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Utils

class ActivitySearch : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private lateinit var adapter: AdapterRadio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        adapter = AdapterRadio(this)
        binding.rvRecycler.layoutManager = LinearLayoutManager(this)
        binding.rvRecycler.adapter = adapter

        binding.rvRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.layoutManager as LinearLayoutManager
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    viewModel.searchLoadMore()
                }
            }
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {
                c.let {
                    viewModel.fetchSearch(c.toString())
                }
            }

            override fun beforeTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {
                c.let {
                    viewModel.fetchSearch(c.toString())
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        viewModel.fetchSearch(binding.etSearch.text.toString())

        adapter.setOnItemClickListener(object : AdapterRadio.OnItemClickListener {
            override fun onItemClick(modelRadio: ArrayList<ModelRadio>, position: Int) {
                val intent = Intent(this@ActivitySearch, ActivityPlay::class.java)
                intent.putExtra(Constant.EXTRA_KEY_ID, modelRadio)
                startActivity(intent)
            }

            override fun onItemMore(modelRadio: ModelRadio) {
                Utils.setBottomSheet(this@ActivitySearch, modelRadio, false)
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
            binding.rvRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        binding.ivInclude.btErrorNetwork.setOnClickListener {
            viewModel.fetchSearch(binding.etSearch.text.toString())
        }

        viewModel.search.observe(this) { list ->
            list.let {
                adapter.setData(list)
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
