package com.vimal.margh.activity

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vimal.margh.R
import com.vimal.margh.databinding.ActivityMainBinding
import com.vimal.margh.fragment.FragmentHome
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.response.MainViewModel
import com.vimal.margh.service.MusicService
import com.vimal.margh.service.PlayerBroadcast
import com.vimal.margh.util.Config
import com.vimal.margh.util.Constant.ACTION_DATA
import com.vimal.margh.util.Constant.ACTION_PAUSE
import com.vimal.margh.util.Constant.ACTION_PLAY
import com.vimal.margh.util.Constant.ACTION_PLAYER_STATE
import com.vimal.margh.util.Utils.registerReceiverCompat
class MainActivity : AppCompatActivity(), FragmentHome.OnDataPassListener {
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    lateinit var binding: ActivityMainBinding
    private var currentIndex = 0
    private var dataList: ArrayList<ModelRadio>? = null
    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
            updatePlayPauseIcon()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }

    private lateinit var playerBroadcast: PlayerBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        NavigationUI.setupWithNavController(binding.layoutNavigation, navHostFragment.navController)

        setupToolbarClickListener()
        bindMusicService()

        setupPlayerControlClickListeners()
        setupBroadcastReceiver()
    }

    private fun setupToolbarClickListener() {
        binding.clToolbar.setOnClickListener {
            val intent = Intent(this, ActivityPlay::class.java)
            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }
    }

    private fun bindMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setupPlayerControlClickListeners() {
        binding.layoutSmall.imgPlayerPlayStopSmall.setOnClickListener {
            togglePlayback()
        }

        binding.layoutBig.fabPlayBig.setOnClickListener {
            togglePlayback()
        }
    }

    private fun setupBroadcastReceiver() {
        playerBroadcast = PlayerBroadcast { state ->
            updatePlayPauseIcon(state)
        }
        registerReceiverCompat(playerBroadcast, IntentFilter(ACTION_PLAYER_STATE))
    }

    private fun togglePlayback() {
        if (isBound) {
            if (musicService?.isPlaying() == true) {
                sendServiceAction(ACTION_PAUSE)
                updatePlayPauseIcon(PlaybackStateCompat.STATE_PAUSED)
            } else {
                sendServiceAction(ACTION_PLAY)
                updatePlayPauseIcon(PlaybackStateCompat.STATE_PLAYING)
            }
        }
    }

    private fun sendServiceAction(action: String) {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    private fun updatePlayPauseIcon(state: Int = PlaybackStateCompat.STATE_PAUSED) {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING
        binding.layoutSmall.imgPlayerPlayStopSmall.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        binding.layoutBig.fabPlayBig.setIconResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )

        if (isPlaying) {
            (binding.layoutBig.equalizerViewBig.drawable as? Animatable)?.start()
            binding.layoutSmall.flMainSmall.visibility = View.VISIBLE
        } else {
            (binding.layoutBig.equalizerViewBig.drawable as? Animatable)?.stop()
        }
    }

    override fun onDataPass(data: ArrayList<ModelRadio>, position: Int) {
        currentIndex = position
        if (data.isNotEmpty()) {
            dataList = data
            updateUI(currentIndex)
        } else {
            Log.d("MainActivity", "Some error")
        }
        setupNavigationControls(data)
    }

    private fun setupNavigationControls(data: ArrayList<ModelRadio>) {
        binding.layoutSmall.imgPlayerNextSmall.setOnClickListener {
            navigateToNextItem(data)
        }

        binding.layoutSmall.imgPlayerPreviousSmall.setOnClickListener {
            navigateToPreviousItem()
        }

        binding.layoutBig.ibNextBig.setOnClickListener {
            navigateToNextItem(data)
        }

        binding.layoutBig.ibPreviousBig.setOnClickListener {
            navigateToPreviousItem()
        }
    }

    private fun navigateToNextItem(data: ArrayList<ModelRadio>) {
        if (currentIndex < data.size - 1) {
            currentIndex++
            updateUI(currentIndex)
        } else {
            Log.d("MainActivity", "Already at the last item")
        }
        viewModel.radioLoadMore()
    }

    private fun navigateToPreviousItem() {
        if (currentIndex > 0) {
            currentIndex--
            updateUI(currentIndex)

        } else {
            Log.d("MainActivity", "Already at the first item")
        }
        viewModel.radioLoadMore()
    }

    private fun updateUI(index: Int) {
        dataList?.let {
            val item = it[index]
            updateSmallPlayerUI(item)
            updateLargePlayerUI(item)
            playItem(item)
        }
    }

    private fun updateSmallPlayerUI(item: ModelRadio) {
        binding.layoutSmall.flMainSmall.visibility = View.GONE

        Glide.with(this)
            .load("${Config.REST_API_URL}/upload/${item.radio_image.replace(" ", "%20")}")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_thumbnail)
            .into(binding.layoutSmall.imgAlbumArtSmall)

        binding.layoutSmall.txtRadioNameSmall.text = item.radio_name
        binding.layoutSmall.txtMetadataSmall.text = item.category_name

        binding.layoutSmall.flMainSmall.visibility = View.VISIBLE
    }

    private fun updateLargePlayerUI(item: ModelRadio) {
        Glide.with(this)
            .load("${Config.REST_API_URL}/upload/${item.radio_image.replace(" ", "%20")}")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_thumbnail)
            .into(binding.layoutBig.imgRadioLargeBig)

        binding.layoutBig.txtMetadataExpandBig.text = item.category_name
        binding.layoutBig.txtRadioNameExpandBig.text = item.radio_name
    }

    private fun playItem(item: ModelRadio) {
        val intent = Intent(this, MusicService::class.java).apply {
            action = ACTION_PLAY
            putExtra(ACTION_DATA, item)
        }
        startService(intent)
        updatePlayPauseIcon(PlaybackStateCompat.STATE_PLAYING)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        unregisterReceiver(playerBroadcast)
    }
}
