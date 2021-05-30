package com.example.localtrader.feed.models

import kotlin.random.Random

data class FeedLoadItem(
    val id : Int = Random.nextInt()
) : FeedItem()