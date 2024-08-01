package com.vimal.margh.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vimal.margh.adapter.AdapterRadio
import com.vimal.margh.databinding.FragmentRecyclerBinding
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.response.MainViewModel
import com.vimal.margh.util.Utils

class FragmentHome : Fragment() {
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    interface OnDataPassListener {
        fun onDataPass(data: ArrayList<ModelRadio>, position: Int)
    }

    private var dataPassListener: OnDataPassListener? = null

    private lateinit var adapter: AdapterRadio
    private var _binding: FragmentRecyclerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterRadio(requireActivity())
        binding.rvRecycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRecycler.adapter = adapter


        adapter.setOnItemClickListener(object : AdapterRadio.OnItemClickListener {
            override fun onItemClick(modelRadio: ArrayList<ModelRadio>, position: Int) {
                sendDataToActivity(modelRadio, position)
            }

            override fun onItemMore(modelRadio: ModelRadio) {
                Utils.setBottomSheet(requireActivity(), modelRadio, false)
            }
        })

        binding.rvRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.layoutManager as LinearLayoutManager
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    viewModel.radioLoadMore()
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.ivInclude.pvProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isNoNetwork.observe(viewLifecycleOwner) { isNoNetwork ->
            binding.ivInclude.llErrorNetwork.visibility =
                if (isNoNetwork) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.ivInclude.llErrorEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        binding.ivInclude.btErrorNetwork.setOnClickListener {
            viewModel.fetchRadio()
        }

        viewModel.radio.observe(viewLifecycleOwner) { radio ->
            radio.let {
                adapter.setData(radio)
            }
        }
    }

    private fun sendDataToActivity(data: ArrayList<ModelRadio>, position: Int) {
        if (dataPassListener != null) {
            dataPassListener!!.onDataPass(data, position)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataPassListener = context as OnDataPassListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnDataPassListener")
        }
    }

}

