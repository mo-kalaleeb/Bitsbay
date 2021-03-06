package de.sixbits.bitspay.main.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.sixbits.bitspay.main.repository.MainRepository
import de.sixbits.bitspay.network.model.ImageListItemModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

private const val TAG = "TrashViewModel"

@HiltViewModel
class TrashViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    val loadingLiveData = MutableLiveData<Boolean>()
    val imagesLiveData = MutableLiveData<List<ImageListItemModel>>()
    val errorLiveData = MutableLiveData<String>()

    fun getItems() {
        loadingLiveData.postValue(true)
        mainRepository.getTrashed()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    imagesLiveData.postValue(it)
                    loadingLiveData.postValue(false)
                },
                {
                    errorLiveData.postValue(it.message)
                }
            )
    }

    fun deleteItem(image: ImageListItemModel) {
        Log.d(TAG, "deleteItem: ")
        loadingLiveData.postValue(true)
        mainRepository.delete(image)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "deleteItem: Item Deleted")
                    loadingLiveData.postValue(false)
                    getItems()
                },
                {
                    errorLiveData.postValue(it.message)
                }
            )
    }
}