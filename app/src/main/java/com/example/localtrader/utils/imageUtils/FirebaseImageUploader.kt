package com.example.localtrader.utils.imageUtils

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*

class FirebaseImageUploader private constructor(
    private val activity : Activity? = null,
    private val lifecycleOwner: LifecycleOwner? = null,
    private val uri : Uri? = null,
    private val bitmap : Bitmap? = null,
    private val path : String? = null,
    private val sizeList: List<ImageSize> = listOf(),
    private val type : Int?
) {
    private val storage = Firebase.storage
    private var resizedImages : MutableLiveData<List<ResizedImage>> = MutableLiveData()
    private var coroutines : MutableList<Deferred<ResizedImage>> = mutableListOf()


    val isCompleted : MutableLiveData<Boolean> = MutableLiveData()

    companion object{

        private val PROFILE_IMAGE_1080 by lazy { ImageSize("PROFILE_IMAGE_1080",1080) }
        private val PROFILE_IMAGE_300 by lazy { ImageSize("PROFILE_IMAGE_300",300) }

        private val BUSINESS_IMAGE_1080 by lazy { ImageSize("BUSINESS_IMAGE_1080",1080) }
        private val BUSINESS_IMAGE_720 by lazy { ImageSize ("BUSINESS_IMAGE_720", 720) }
        private val BUSINESS_IMAGE_400 by lazy { ImageSize("BUSINESS_IMAGE_400",400) }

        private val PRODUCT_IMAGE_1920 by lazy { ImageSize("PRODUCT_IMAGE_1920", 1920) }
        private val PRODUCT_IMAGE_400 by lazy { ImageSize("PRODUCT_IMAGE_400", 400) }

        private val BUG_REPORT_IMAGE_1920 by lazy { ImageSize("BUG_REPORT_IMAGE_1920", 1920) }

        val PROFILE_IMAGE by lazy { listOf(PROFILE_IMAGE_1080, PROFILE_IMAGE_300) }
        val BUSINESS_IMAGE by lazy { listOf(BUSINESS_IMAGE_1080, BUSINESS_IMAGE_720, BUSINESS_IMAGE_400) }
        val PRODUCT_IMAGE by lazy { listOf(PRODUCT_IMAGE_1920, PRODUCT_IMAGE_400) }
        val BUG_REPORT_IMAGE by lazy { listOf(BUG_REPORT_IMAGE_1920) }
    }

    private suspend fun convert() = coroutineScope {

        if (activity == null  || type == null){
            return@coroutineScope
        }

        if (type == 1){
            for (element in sizeList) {
                coroutines.add(
                    async (Dispatchers.IO){
                        ImageUtils.resizeImageUriTo(activity, uri!!, element)
                    }
                )
            }
        }
        else{
            for (element in sizeList){
                coroutines.add(
                    async(Dispatchers.IO){
                        ImageUtils.resizeImageBitmapTo(bitmap!!, element)
                    }
                )
            }
        }

        resizedImages.value = coroutines.awaitAll()
    }

    suspend fun uploadAll() {
        if (lifecycleOwner == null) return

        resizedImages.observe(lifecycleOwner, object : Observer<List<ResizedImage>>{
            override fun onChanged(t: List<ResizedImage>?) {

                CoroutineScope(Dispatchers.IO).launch {
                    uploadOneByOne(0)
                }

                resizedImages.removeObserver(this)
            }
        })
        convert()
    }

    private fun uploadOneByOne(current : Int){
        if (resizedImages.value == null || current > resizedImages.value!!.size - 1) {

            if (current == 99){
                isCompleted.postValue(false)
            }else{
                isCompleted.postValue(true)
            }
            return
        }

        val pictureName = resizedImages.value!![current].name
        storage.reference.child("${this.path}/${pictureName}")
            .putBytes(resizedImages.value!![current].value)
            .addOnSuccessListener {
                uploadOneByOne(current + 1)
            }
            .addOnFailureListener{
                uploadOneByOne(99)
            }
    }

    class Builder{
        private var activity : Activity? = null
        private var uri : Uri? = null
        private var bitmap : Bitmap? = null
        private var sizeList: List<ImageSize> = listOf()
        private var lifecycleOwner : LifecycleOwner? = null
        private var path : String? = null
        private var type : Int? = null


        fun withActivity(activity : Activity) = apply{ this.activity = activity }
        fun withLifecycle(lifecycleOwner: LifecycleOwner) = apply{ this.lifecycleOwner = lifecycleOwner }

        fun imageUri(uri : Uri) = apply{
            this.uri = uri
            this.type = 1
        }

        fun imageBitmap (bitmap : Bitmap) = apply{
            this.bitmap = bitmap
            this.type = 2
        }

        fun toPath (path : String) = apply { this.path = path }

        fun imageType(imageType : List<ImageSize>) = apply{ this.sizeList = imageType }
        fun build() = FirebaseImageUploader(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            uri = uri,
            bitmap = bitmap,
            path = path,
            sizeList = sizeList,
            type = type
        )
    }


}