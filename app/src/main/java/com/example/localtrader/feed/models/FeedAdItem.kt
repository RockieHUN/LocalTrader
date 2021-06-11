package com.example.localtrader.feed.models

import com.google.android.gms.ads.nativead.NativeAd
import kotlin.random.Random

data class FeedAdItem(
    var id : Int = Random.nextInt(),
    var ad : NativeAd? = null
) : FeedItem()