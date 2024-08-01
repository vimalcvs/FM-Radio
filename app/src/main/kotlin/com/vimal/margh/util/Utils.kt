@file:Suppress("DEPRECATION")

package com.vimal.margh.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vimal.margh.R
import com.vimal.margh.activity.MainActivity
import com.vimal.margh.databinding.BottomDetailBinding

import com.vimal.margh.db.Repository
import com.vimal.margh.fragment.FragmentHome
import com.vimal.margh.models.ModelRadio
import java.nio.charset.StandardCharsets
import kotlin.math.ln

object Utils {



    fun Context.registerReceiverCompat(receiver: BroadcastReceiver, filter: IntentFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
    }


    inline fun <reified T> Intent.getParcelableExtraCompat(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T?
    }

    inline fun <reified T : Parcelable> Intent.getParcelableArrayListExtraCompat(key: String): ArrayList<T>? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayListExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableArrayListExtra<T>(key)
    }

    fun FragmentHome.mainActivity(): MainActivity {
        return requireActivity() as MainActivity
    }

    fun getErrors(e: Exception?) {
        println("TAG :: " + Log.getStackTraceString(e))
    }


    fun withSuffix(count: Long): String {
        if (count < 1000) return count.toString()
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f%c", count / Math.pow(1000.0, exp.toDouble()), "KMGTPE"[exp - 1])
    }


    fun decrypt(code: String): String {
        return decodeBase64(decodeBase64(code))
    }

    private fun decodeBase64(code: String): String {
        val valueDecoded = Base64.decode(code.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
        return String(valueDecoded)
    }


    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        val shareBody =
            "Check out this awesome app!" + "https://play.google.com/store/apps/details?id=" + context.packageName
        val shareSubject = "Share App"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

    fun contactApp(context: Context) {
        val message = "Hello, this is a direct message from my app!"
        val uri = Uri.parse("smsto:" + "+918882683887")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.setPackage("com.whatsapp")
        context.startActivity(Intent.createChooser(intent, ""))
    }


    fun setBottomSheet(activity: Activity, model: ModelRadio?, boolean: Boolean) {
        val repository: Repository by lazy { Repository.getInstance(activity)!! }
        val bottomSheetDialog = BottomSheetDialog(activity)
        val binding = BottomDetailBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setContentView(binding.getRoot())

        binding.sheetRadioName.text = model!!.radio_name
        binding.sheetCategoryName.text = model.category_name

        Glide.with(activity)
            .load(Config.REST_API_URL + "/upload/" + model.radio_image.replace(" ", "%20"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_thumbnail)
            .into(binding.sheetRadioImage)

        binding.btnFavorite.setOnClickListener {
            if (repository.isFavorite(model.radio_id)) {
                repository.deleteFavorite(model)
                binding.imgFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_normal)
                Toast.makeText(activity, "Removed from Favorite", Toast.LENGTH_SHORT)
                    .show()
            } else {
                repository.insertFavorite(model)
                binding.imgFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_active)
                Toast.makeText(activity, "Added to Favorite", Toast.LENGTH_SHORT).show()
            }
            if (boolean) {
                bottomSheetDialog.dismiss()
            }
        }

        if (repository.isFavorite(model.radio_id)) {
            binding.imgFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_active)
        } else {
            binding.imgFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_normal)
        }
        bottomSheetDialog.show()

    }


}
