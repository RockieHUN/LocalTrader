package com.example.localtrader.feed.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.feed.models.*
import com.example.localtrader.utils.diffUtils.FeedDiffUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedAdapter(
    private val activity : Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val listener : OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<FeedItem>()
    private var storage = Firebase.storage
    private val loadedAd : MutableLiveData<NativeAd> = MutableLiveData()

    private val adRequest by lazy { AdRequest.Builder().build() }
    private lateinit var adLoader : AdLoader

    val adList : MutableList<FeedAdItem> = mutableListOf()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            adLoader = AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd { ad : NativeAd ->
                    loadedAd.postValue(ad)
                }
                .withAdListener(object : AdListener(){
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.d("MYAD", "failed to load ad $error")
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()
        }
    }


    interface OnItemClickListener{
        fun onBusinessClick(id : String)
    }

    //ViewHolders
    inner class BusinessViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val logoView: ImageView = itemView.findViewById(R.id.business_logo)
        val businessNameView: TextView = itemView.findViewById(R.id.business_name)
        val businessDescriptionView : TextView = itemView.findViewById(R.id.business_description)
        val item : CardView = itemView.findViewById(R.id.item)

        fun bind(position: Int){
            val currentItem = items[position] as FeedBusinessItem

            businessDescriptionView.text = currentItem.description
            businessNameView.text = currentItem.name

            item.setOnClickListener {
                listener.onBusinessClick(currentItem.businessId)
            }

            logoView.setImageResource(android.R.color.transparent)

            storage.reference.child("businesses/${currentItem.businessId}/BUSINESS_IMAGE_400")
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

    inner class LoadingViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){ }
    }

    inner class NoMoreItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){ }
    }

    inner class AdViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        lateinit var adAppIconView  : ImageView
        lateinit var adHeaderView : TextView
        lateinit var adAdvertiserNameView : TextView
        lateinit var adTextHolderView : CardView

        fun bind(position: Int){
            val currentItem = items[position]

            adAppIconView = itemView.findViewById(R.id.ad_app_icon)
            adHeaderView = itemView.findViewById(R.id.ad_headline)
            adAdvertiserNameView = itemView.findViewById(R.id.ad_advertiser_name)
            adTextHolderView = itemView.findViewById(R.id.ad_text_holder)

            if ((currentItem as FeedAdItem).ad == null){
                loadedAd.observe(lifecycleOwner, object : Observer<NativeAd>{
                    override fun onChanged(ad: NativeAd?) {
                        if (ad == null){
                            loadedAd.removeObserver(this)
                            return
                        }

                        (items[position] as FeedAdItem).ad = ad
                        adList.add(items[position] as FeedAdItem)
                        setAd(ad)

                        loadedAd.removeObserver(this)
                    }
                })

                GlobalScope.launch(Dispatchers.IO) {
                    adLoader.loadAd(adRequest)
                }
            }
            else{
                setAd(currentItem.ad!!)
            }
        }

        private fun setAd(ad : NativeAd){
            if (ad.icon!= null)  adAppIconView.setImageDrawable(ad.icon!!.drawable)

            if (ad.headline != null || ad.advertiser!= null){
                adTextHolderView.visibility = View.VISIBLE
            }
            else{
                adTextHolderView.visibility = View.GONE
            }

            adHeaderView.text = ad.headline
            adAdvertiserNameView.text = ad.advertiser

            itemView as NativeAdView
            itemView.setNativeAd(ad)
        }

    }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FeedBusinessItem -> 1
            is FeedAdItem -> 3
            is FeedLoadItem -> 4
            is FeedNoMoreItem -> 5
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
            4 -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_load_item, parent, false)
            )
            5 -> NoMoreItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_no_more_item, parent, false)
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
            is FeedLoadItem -> (holder as LoadingViewHolder).bind(position)
            is FeedNoMoreItem -> (holder as NoMoreItemViewHolder).bind(position)
        }
    }

    fun updateData(newItems : List<FeedItem>){
        val diffUtil = FeedDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = items.size
}