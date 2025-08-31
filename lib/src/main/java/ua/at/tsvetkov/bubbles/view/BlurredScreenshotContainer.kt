package ua.at.tsvetkov.bubbles.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Вспомогательная функция для захвата и размытия конкретного View
// (Эта функция остается такой же, как в предыдущем вашем варианте)
private suspend fun captureAndBlurActualView(context: Context, view: View, blurRadius: Float): Bitmap? {
    return withContext(Dispatchers.Default) {
        try {
            val screenshot = createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1))
            val canvas = Canvas(screenshot)
            // Важно: для захвата всего окна, включая диалоги и всплывающие окна поверх Activity,
            // может потребоваться более сложный подход или дополнительные разрешения (например, MediaProjection API).
            // view.draw(canvas) захватит содержимое этого конкретного View (в данном случае, rootView Activity).
            view.draw(canvas)

            val outputBitmap = Bitmap.createBitmap(screenshot)
            val rs = RenderScript.create(context)
            val anIn = Allocation.createFromBitmap(rs, screenshot)
            val anOut = Allocation.createFromBitmap(rs, outputBitmap)
            val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            val clampedRadius = blurRadius.coerceIn(0.1f, 25.0f)

            scriptIntrinsicBlur.setRadius(clampedRadius)
            scriptIntrinsicBlur.setInput(anIn)
            scriptIntrinsicBlur.forEach(anOut)
            anOut.copyTo(outputBitmap)

            anIn.destroy()
            anOut.destroy()
            scriptIntrinsicBlur.destroy()
            rs.destroy()
            if (!screenshot.isRecycled) {
                screenshot.recycle()
            }
            outputBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun captureAndBlurScreen(
    window: Window,
    context: Context,
    viewToCapture: View,
    blurRadius: Float,
    callback: (ImageView?) -> Unit,
) {
    // Шаг 1: Захват скриншота с помощью PixelCopy
    val bitmap = createBitmap(viewToCapture.width, viewToCapture.height)
    val location = IntArray(2)
    viewToCapture.getLocationInWindow(location)

    PixelCopy.request(
        window,
        Rect(location[0], location[1], location[0] + viewToCapture.width, location[1] + viewToCapture.height),
        bitmap,
        { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                // Шаг 2: Создание ImageView и добавление его в иерархию View
                val blurredImageView = ImageView(context)
                blurredImageView.setImageBitmap(bitmap)
                blurredImageView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Добавление ImageView как верхнего слоя
                val rootView = viewToCapture.rootView as ViewGroup
                rootView.addView(blurredImageView)

                // Шаг 3: Применение размытия (только для Android 12+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val blurEffect = RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                    blurredImageView.setRenderEffect(blurEffect)
                }

                callback(blurredImageView)
            } else {
                callback(null)
            }
        },
        Handler(Looper.getMainLooper())
    )
}

