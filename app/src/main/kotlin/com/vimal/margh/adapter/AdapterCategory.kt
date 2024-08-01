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
import com.vimal.margh.databinding.ItemCategoryBinding
import com.vimal.margh.models.ModelCategory
import com.vimal.margh.util.Config
import com.vimal.margh.util.Constant

@SuppressLint("NotifyDataSetChanged")

class AdapterCategory(private val context: Context) : RecyclerView.Adapter<AdapterCategory.ViewHolder>() {
    private var list = listOf<ModelCategory>()
    private var mOnItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.binding.txtCategoryName.text = model.category_name

        val text =
            model.radio_count.toString() + " " + context.resources.getString(R.string.station)
        holder.binding.txtRadioCount.text = text

        if (Constant.DISPLAY_RADIO_COUNT_ON_CATEGORY_LIST) {
            holder.binding.txtRadioCount.visibility = View.VISIBLE
        } else {
            holder.binding.txtRadioCount.visibility = View.GONE
        }

        Glide.with(context)
            .load(
                Config.REST_API_URL + "/upload/category/" + model.category_image.replace(
                    " ",
                    "%20"
                )
            )
            .placeholder(R.drawable.ic_thumbnail)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.imgCategory)

        holder.binding.cardView.setOnClickListener { mOnItemClickListener?.onItemClick(model) }

        if (position == list.size - 1) {
            holder.binding.viewDivider.visibility = ViewGroup.VISIBLE
        } else {
            holder.binding.viewDivider.visibility = ViewGroup.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(modelPosts: List<ModelCategory>) {
        this.list = modelPosts
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(modelCategory: ModelCategory)
    }

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}
