package com.example.localtrader.services.notifications.retrofit

import com.example.localtrader.services.notifications.models.PushNotification
import com.example.localtrader.utils.constants.RetrofitConstants.Companion.CONTENT_TYPE
import com.example.localtrader.utils.constants.RetrofitConstants.Companion.SERVER_KEY
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key = $SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification : PushNotification
    ) : Response<ResponseBody>
}