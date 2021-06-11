package com.example.localtrader.feed.models

data class FeedConfig(
    val newItemsPerLoad : Int,
    val adPerLoad : Int,
    val businessPerLoad : Long,
    val productPerLoad : Long
)