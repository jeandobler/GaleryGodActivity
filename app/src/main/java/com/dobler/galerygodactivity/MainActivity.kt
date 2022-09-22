package com.dobler.galerygodactivity

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.Locale
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    var medias: List<Media>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_medias)
        medias = getFilePaths("Download")
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = GridLayoutManager(this, 2)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = MediasAdapter(medias)
        mRecyclerView.adapter = mAdapter
    }

    class MediasAdapter(private val mMedias: List<Media>?) :
        RecyclerView.Adapter<MediasAdapter.MediaViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MediaViewHolder {
            val view = LayoutInflater.from(
                viewGroup.context
            ).inflate(R.layout.item_view_gallery, viewGroup, false)
            return MediaViewHolder(view)
        }

        override fun onBindViewHolder(mediaViewHolder: MediaViewHolder, i: Int) {
            val media = mMedias!![i]
            mediaViewHolder.imageViewThumbnail.setImageURI(Uri.fromFile(File(media.thumbnailLocalPath)))
        }

        override fun getItemCount(): Int {
            return mMedias?.size ?: 0
        }

        class MediaViewHolder(viewRoot: View) : RecyclerView.ViewHolder(viewRoot) {
            var imageViewThumbnail: ImageView = itemView.findViewById(R.id.image_media_thumbnail)

        }
    }

    inner class Media(val thumbnailLocalPath: String)

    fun getFilePaths(dir: String): List<Media> {
        val medias: MutableList<Media> = ArrayList()
        val directory =
            File(Environment.getExternalStorageDirectory().toString() + File.separator.toString() + dir)
        if (directory.isDirectory) {
            for (file in directory.listFiles()) {
                val filePath: String = file.absolutePath
                if (isSupportedFile(filePath)) {
                    medias.add(Media(filePath))
                }
            }
        }
        return medias
    }

    private fun isSupportedFile(filePath: String): Boolean {
        val ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length)
        return FILE_EXTENSIONS.contains(ext.lowercase(Locale.getDefault()))
    }

    companion object {
        val FILE_EXTENSIONS: List<String> = listOf("jpg", "jpeg", "png")
    }
}
