package com.vimal.margh.adapter

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

class AdapterRadio(
    private val context: Context
) : RecyclerView.Adapter<AdapterRadio.ViewHolder>() {
    private var list = listOf<ModelRadio>()
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
            mOnItemClickListener?.onItemClick(list.toCollection(ArrayList()), position)
        }

        holder.binding.imgOverflow.setOnClickListener {
            mOnItemClickListener?.onItemMore(modelList)
        }

        if (position == list.size - 1) {
            holder.binding.viewDivider.visibility = ViewGroup.GONE
        } else {
            holder.binding.viewDivider.visibility = ViewGroup.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(radioList: List<ModelRadio>) {
        this.list = radioList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(modelRadio: ArrayList<ModelRadio>, position: Int)
        fun onItemMore(modelRadio: ModelRadio)
    }

    class ViewHolder(val binding: ItemRadioBinding) : RecyclerView.ViewHolder(binding.root)
}
