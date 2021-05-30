package com.example.localtrader.utils.imageUtils

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

                return Bitmap.createScaledBitmap(image, targetWidth, maxRes, false)

            } else {
                if (image.width <= maxRes) { // if image width already smaller than the required width
                    return image
                }

                val aspectRatio = image.height.toDouble() / image.width.toDouble()
                val targetHeight = (maxRes * aspectRatio).toInt()

                return Bitmap.createScaledBitmap(image, maxRes, targetHeight, false)
            }
        }


        private fun uriToScaledBitmap(activity: Activity, uri : Uri, maxRes : Int) : Bitmap{

            val inputStream = activity.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()

            var bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray!!.size)

            val height = bitmap.height
            val width = bitmap.width

            //if the image is too big, resize
            if (height > maxRes || width >maxRes) bitmap = resizeImage(bitmap, maxRes)

            return bitmap
        }


        private fun bitmapToByteArray(bitmap : Bitmap) : ByteArray{
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            return stream.toByteArray()
        }

        //resize uri and return the bytearray
        fun resizeImageUriTo(activity: Activity, imageUri : Uri, imageSize : ImageSize) : ResizedImage
        {
            val scaledBitmap = uriToScaledBitmap(activity, imageUri, imageSize.value)
            return ResizedImage(imageSize.name,bitmapToByteArray(scaledBitmap))
        }

        //resize bitmap and return the bytearray
        fun resizeImageBitmapTo(imageBitmap : Bitmap, imageSize: ImageSize) : ResizedImage
        {
            val scaledBitmap = resizeImage(imageBitmap, imageSize.value)
            return ResizedImage(imageSize.name, bitmapToByteArray(scaledBitmap))
        }

    }
}