package com.vimal.margh.response

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vimal.margh.models.ModelCategory
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.models.ModelSettings
import com.vimal.margh.rest.ApiInterface
import com.vimal.margh.rest.RestAdapter
import com.vimal.margh.util.Constant.LIST_PER_PAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiInterface by lazy { RestAdapter.createAPI(application) }

    private val _categoriesDetail = MutableLiveData<List<ModelRadio>>()
    val categoriesDetail: LiveData<List<ModelRadio>> = _categoriesDetail

    private val _categories = MutableLiveData<List<ModelCategory>>()
    val categories: LiveData<List<ModelCategory>> = _categories

    private val _radio = MutableLiveData<List<ModelRadio>>()
    val radio: LiveData<List<ModelRadio>> = _radio

    private val _search = MutableLiveData<List<ModelRadio>>()
    val search: LiveData<List<ModelRadio>> = _search

    private val _settings = MutableLiveData<ModelSettings>()
    val settings: LiveData<ModelSettings> = _settings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isNoNetwork = MutableLiveData<Boolean>()
    val isNoNetwork: LiveData<Boolean> = _isNoNetwork

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty


    /////////////////////////////////////////////////////////////////////////
    ///Start Radio

    private var currentPage = 1
    private var isLastPage = false

    init {
        fetchRadio()
    }

    fun fetchRadio(loadMore: Boolean = false) {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        _isNoNetwork.value = false
        if (!loadMore) _isEmpty.value = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getRadios(LIST_PER_PAGE, currentPage)
                if (response.isSuccessful) {
                    val radios = response.body()?.posts
                    if (radios.isNullOrEmpty()) {
                        if (currentPage == 1) {
                            _isEmpty.postValue(true)
                        } else {
                            isLastPage = true
                        }
                    } else {
                        if (loadMore) {
                            val currentList = _radio.value.orEmpty().toMutableList()
                            currentList.addAll(radios)
                            _radio.postValue(currentList)
                        } else {
                            _radio.postValue(response.body()?.posts)
                        }
                        currentPage++
                    }
                } else {
                    _isNoNetwork.postValue(true)
                }
            } catch (e: Exception) {
                _isNoNetwork.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun radioLoadMore() {
        fetchRadio(loadMore = true)
    }

    ///End Radio
    /////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////
    ///Start Categories Detail

    private var currentCategory = -1

    fun fetchCategoriesDetail(category: Int, isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            currentPage = 1
            currentCategory = category
            _categoriesDetail.value = emptyList()
        }
        _isLoading.value = true
        _isNoNetwork.value = false
        _isEmpty.value = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCategoryDetail(category, LIST_PER_PAGE, currentPage)
                if (response.isSuccessful) {
                    val currentData = _categoriesDetail.value ?: emptyList()
                    val newData = response.body()?.posts ?: emptyList()
                    if (newData.isEmpty()) {
                        if (currentPage == 1) {
                            _isEmpty.postValue(true)
                        }
                    } else {
                        _categoriesDetail.postValue(currentData + newData)
                        currentPage++
                    }
                } else {
                    _isNoNetwork.postValue(true)
                }
            } catch (e: Exception) {
                _isNoNetwork.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun categoriesDetailLoadMore() {
        if (currentCategory != -1) {
            fetchCategoriesDetail(currentCategory, true)
        }
    }

    ///End Categories Detail
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    ///Start Categories

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        _isLoading.value = true
        _isNoNetwork.value = false
        _isEmpty.value = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful) {
                    if (response.body()?.categories.isNullOrEmpty()) {
                        _isEmpty.postValue(true)
                    } else {
                        _categories.postValue(response.body()?.categories)
                    }
                } else {
                    _isNoNetwork.postValue(true)
                }
            } catch (e: Exception) {
                _isNoNetwork.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


    ///End Categories
    /////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////
    ///Start Search


    private var currentSearchTerm: String? = null
    fun fetchSearch(searchTerm: String, isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            currentPage = 1
            currentSearchTerm = searchTerm
            _search.value = emptyList()
        }
        _isLoading.value = true
        _isNoNetwork.value = false
        _isEmpty.value = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getSearch(searchTerm, LIST_PER_PAGE, currentPage)
                if (response.isSuccessful) {
                    val currentData = _search.value ?: emptyList()
                    val newData = response.body()?.posts ?: emptyList()
                    if (newData.isEmpty()) {
                        if (currentPage == 1) {
                            _isEmpty.postValue(true)
                        }
                    } else {
                        _search.postValue(currentData + newData)
                        currentPage++
                    }
                } else {
                    _isNoNetwork.postValue(true)
                }
            } catch (e: Exception) {
                _isNoNetwork.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun searchLoadMore() {
        currentSearchTerm?.let {
            fetchSearch(it, true)
        }
    }


    /////////////////////////////////////////////////////////////////////////
    ///Start Settings


    fun fetchSettings() {
        _isLoading.value = true
        _isNoNetwork.value = false
        _isEmpty.value = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getSettings()
                if (response.isSuccessful) {
                    if (response.body()?.status.isNullOrEmpty()) {
                        _isEmpty.postValue(true)
                    } else {
                        _settings.postValue(response.body()?.settings)
                    }
                } else {
                    _isNoNetwork.postValue(true)
                }
            } catch (e: Exception) {
                _isNoNetwork.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    ///End Settings
    /////////////////////////////////////////////////////////////////////////
}