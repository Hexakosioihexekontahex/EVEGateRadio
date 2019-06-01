package com.hex.evegate.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hex.evegate.R
import com.hex.evegate.ui.mvp.model.Song

class ItemsRVAdapter(private val ctx: Context) : RecyclerView.Adapter<ItemsRVAdapter.SongViewHolder>() {
    private var items: MutableList<Song> = mutableListOf()

    companion object {
        private const val ITEM_TYPE_ROW_SONG = 2
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SongViewHolder {
        when (viewType) {
            ITEM_TYPE_ROW_SONG -> {
                val confessionView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.song_item, viewGroup, false)
                return SongViewHolder(confessionView)
            }
            else -> throw IllegalArgumentException("Wrong type!")
        }
    }

    override fun onBindViewHolder(aggregatorViewHolder: SongViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_ROW_SONG) {
            initRow(aggregatorViewHolder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is Song) {
            ITEM_TYPE_ROW_SONG
        } else {
            0
        }
    }

    override fun getItemCount() = items.size

    fun setData(dataList: List<Song>?) {
        items = dataList as MutableList<Song>
        notifyDataSetChanged()
    }

    private fun initRow(holder: SongViewHolder, position: Int) {
        val song = items[position]
        holder.tvSongName.text = song.text
        holder.tvPlaylist.text = song.lyrics
        Glide.with(ctx)
                .load(song.art)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.ivSongAlbum)
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSongName = itemView.findViewById<TextView>(R.id.tvSongName)
        var ivSongAlbum = itemView.findViewById<ImageView>(R.id.ivSongAlbum)
        var tvPlaylist = itemView.findViewById<TextView>(R.id.tvPlaylist)
    }
}
