package com.vimal.margh.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vimal.margh.R
import com.vimal.margh.databinding.ItemRadioBinding
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.util.Config
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Utils

@SuppressLint("NotifyDataSetChanged")
class AdapterFavorite(val context: Context, private var list: List<ModelRadio>) :
    RecyclerView.Adapter<AdapterFavorite.ViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRadioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelList = list[position]

        holder.binding.txtRadio.text = modelList.radio_name
        holder.binding.txtCategory.text = modelList.category_name

        if (Constant.ENABLE_RADIO_VIEW_COUNT) {
            holder.binding.txtViewCount.text = Utils.withSuffix(modelList.view_count.toLong())
            holder.binding.lytViewCount.visibility = View.VISIBLE
        } else {
            holder.binding.lytViewCount.visibility = View.GONE
        }


        Glide.with(context)
            .load(Config.REST_API_URL + "/upload/" + modelList.radio_image.replace(" ", "%20"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_thumbnail)
            .into(holder.binding.imgRadio)


        holder.binding.lytParent.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(list.toCollection(ArrayList()))
            }
        }

        holder.binding.imgOverflow.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemMore(modelList)
            }
        }

        if (position == list.size - 1) {
            holder.binding.viewDivider.visibility = ViewGroup.VISIBLE
        } else {
            holder.binding.viewDivider.visibility = ViewGroup.GONE
        }
    }

    fun updateData(productList: List<ModelRadio>) {
        this.list = productList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(modelRadio: ArrayList<ModelRadio>)
        fun onItemMore(modelRadio: ModelRadio)
    }

    class ViewHolder(val binding: ItemRadioBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}