package com.example.localtrader.feed.models

import kotlin.random.Random

data class FeedNoMoreItem(
    val id : Int = Random.nextInt()
) : FeedItem()