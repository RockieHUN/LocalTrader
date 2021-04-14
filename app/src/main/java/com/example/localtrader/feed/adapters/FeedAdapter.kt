package com.example.localtrader.feed.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.example.localtrader.feed.models.FeedAdItem
import com.example.localtrader.feed.models.FeedBusinessItem
import com.example.localtrader.feed.models.FeedItem
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.logging.Handler

class FeedAdapter(
    private val activity : Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<FeedItem>()
    private var storage = Firebase.storage

    //ViewHolders
    inner class BusinessViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val logoView = itemView.findViewById<ImageView>(R.id.business_logo)
        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val businessDescriptionView = itemView.findViewById<TextView>(R.id.business_description)

        fun bind(position: Int){
            val currentItem = items[position] as FeedBusinessItem

            businessDescriptionView.text = currentItem.description
            businessNameView.text = currentItem.name

            storage.reference.child("businesses/${currentItem.businessId}/logo")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(activity)
                        .load(uri)
                        .centerCrop()
                        .into(logoView)
                }
        }
    }

    inner class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(position: Int){
            val currentItem = items[position]
        }
    }

    inner class AdViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(position: Int){
            val currentItem = items[position]

            //TODO: Clear ads from memory


            val adAppIconView = itemView.findViewById<ImageView>(R.id.ad_app_icon)
            val adHeaderView = itemView.findViewById<TextView>(R.id.ad_headline)
            val adAdvertiserNameView = itemView.findViewById<TextView>(R.id.ad_advertiser_name)
            //val mediaView = itemView.findViewById<MediaView>(R.id.media_view)

            val adloader = AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd { ad : NativeAd ->


                    if (ad.icon!= null)  adAppIconView.setImageDrawable(ad.icon!!.drawable)

                    adHeaderView.text = ad.headline
                    adAdvertiserNameView.text = ad.advertiser


                    itemView as NativeAdView
                    itemView.setNativeAd(ad)
                }
                .withAdListener(object : AdListener(){
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.d("MYAD", "failed to load ad ${error}")
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()

            adloader.loadAd(AdRequest.Builder().build())

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FeedBusinessItem -> 1
            is FeedAdItem -> 3
            else -> 0
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1 -> BusinessViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_business_item, parent, false)
            )
            3 -> AdViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_ad_item, parent, false)
            )
            else -> BusinessViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_business_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (items[position]){
            is FeedBusinessItem -> (holder as BusinessViewHolder).bind(position)
            is FeedAdItem -> (holder as AdViewHolder).bind(position)
        }
    }

    fun updateData(newItems : List<FeedItem>){
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}