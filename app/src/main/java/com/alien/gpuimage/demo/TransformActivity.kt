package com.alien.gpuimage.demo

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Matrix
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import com.alien.gpuimage.filter.TransformFilter
import com.alien.gpuimage.outputs.SurfaceView
import com.alien.gpuimage.sources.Picture
import java.io.File

class TransformActivity : AppCompatActivity() {

    companion object {
        private const val KEY_PATH = "key_path"

        fun startActivity(context: Context, path: String?) {
            val intent = Intent(context, TransformActivity::class.java)
            intent.putExtra(KEY_PATH, path)
            context.startActivity(intent)
        }
    }

    private var surfaceView: SurfaceView? = null
    private var picture: Picture? = null
    private var transformFilter: TransformFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transform)

        surfaceView = findViewById(R.id.surface_view)
        val path = intent.getStringExtra(KEY_PATH)
        if (!File(path ?: "").exists()) return

        val option = BitmapFactory.Options().apply { this.inSampleSize = 2 }
        val bitmap = BitmapFactory.decodeFile(path, option)

        picture = Picture(bitmap, true)
        transformFilter = TransformFilter()
        transformFilter?.setIgnoreAspectRatio(true)
        picture?.addTarget(transformFilter)
        transformFilter?.addTarget(surfaceView)

        findViewById<AppCompatSeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val transform = FloatArray(16)
                Matrix.setIdentityM(transform, 0);
//                Matrix.setRotateM(transform, 0, 0f, 0f, 0f, 1.0f)
                Matrix.scaleM(transform, 0, p1 / 100f, p1 / 100f, 1f)
//                Matrix.translateM(transform, 0, p1 / 100f, 0f, 0f)
                transformFilter?.setTransform3D(transform)
                picture?.processPicture()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        picture?.release()
        transformFilter?.release()
    }
}