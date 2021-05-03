package com.example.localtrader.feed.models

import kotlin.random.Random

data class FeedAdItem(
    var id : Int = Random.nextInt()
) : FeedItem()