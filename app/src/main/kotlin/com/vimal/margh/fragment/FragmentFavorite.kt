package com.vimal.margh.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vimal.margh.activity.ActivityCategory
import com.vimal.margh.adapter.AdapterFavorite
import com.vimal.margh.databinding.FragmentRecyclerBinding
import com.vimal.margh.db.Repository
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Utils


class FragmentFavorite : Fragment(), AdapterFavorite.OnItemClickListener {

    private val repository: Repository by lazy { Repository.getInstance(requireActivity())!! }
    private var binding: FragmentRecyclerBinding? = null
    private var adapter: AdapterFavorite? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        val root: View = binding!!.root


        adapter = AdapterFavorite(requireActivity(), ArrayList())
        repository.allFavorite().observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                adapter!!.updateData(products)
                binding!!.ivInclude.llEmptyFavorite.visibility = View.GONE
            } else {
                adapter!!.updateData(ArrayList())
                binding!!.ivInclude.llEmptyFavorite.visibility = View.VISIBLE
            }
        }
        binding!!.rvRecycler.layoutManager = LinearLayoutManager(requireActivity())
        binding!!.rvRecycler.adapter = adapter
        adapter!!.setOnItemClickListener(this)

        return root
    }


    override fun onItemClick(modelRadio: ArrayList<ModelRadio>) {
        val intent = Intent(requireActivity(), ActivityCategory::class.java).apply {
            putExtra(Constant.EXTRA_KEY_ID, arrayListOf(modelRadio))
        }
        startActivity(intent)
    }

    override fun onItemMore(modelRadio: ModelRadio) {
        Utils.setBottomSheet(requireActivity(), modelRadio, false)
    }
}