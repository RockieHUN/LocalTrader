package com.example.localtrader.feed.models

data class FeedBusinessItem(
    var businessId : String ="",
    var ownerUid : String = "",
    var name : String = "",
    var category : String ="",
    var description : String = "",
    var email : String ="",
    var telephone : String = "",
    var longitude : Double = 0.0,
    var latitude : Double = 0.0,
    var facebook_link : String = "",
    var instagram_link : String= "",
    var isPromoted : Boolean = false
) : FeedItem()
