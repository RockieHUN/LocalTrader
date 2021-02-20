package com.example.localtrader.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

class ImageUtils {

    companion object {

        private fun resizeImage(image : Bitmap, maxRes : Int) : Bitmap {
            if (image.height >= image.width) {
                if (image.height <= maxRes) { // if image height already smaller than the required height
                    return image
                }

                val aspectRatio = image.width.toDouble() / image.height.toDouble()
                val targetWidth = (maxRes * aspectRatio).toInt()

                val result = Bitmap.createScaledBitmap(image, targetWidth, maxRes, false)
                return result

            } else {
                if (image.width <= maxRes) { // if image width already smaller than the required width
                    return image
                }

                val aspectRatio = image.height.toDouble() / image.width.toDouble()
                val targetHeight = (maxRes * aspectRatio).toInt()

                val result = Bitmap.createScaledBitmap(image, maxRes, targetHeight, false)
                return result
            }
        }


        suspend fun uriToScaledBitmap(activity: Activity, uri : Uri, maxRes : Int) : Bitmap{

            val inputStream = activity.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()

            var bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray!!.size)

            val height = bitmap.height
            val width = bitmap.width

            //if the image is too big, resize
            if (height > maxRes || width >maxRes) bitmap = resizeImage(bitmap, maxRes)

            return bitmap
        }


        suspend fun bitmapToByteArray(bitmap : Bitmap) : ByteArray{
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            return stream.toByteArray()
        }

        //resize uri and return the bytearray
        suspend fun resizeImageUriTo(activity: Activity, imageUri : Uri, maxRes : Int) : ByteArray
        {
            val scaledBitmap = uriToScaledBitmap(activity, imageUri, maxRes)
            return bitmapToByteArray(scaledBitmap)
        }

        //resize bitmap and return the bytearray
        suspend fun resizeImageBitmapTo(activity: Activity, imageBitmap : Bitmap, maxRes : Int) : ByteArray
        {
            val scaledBitmap = resizeImage(imageBitmap, maxRes)
            return bitmapToByteArray(scaledBitmap)
        }

    }
}