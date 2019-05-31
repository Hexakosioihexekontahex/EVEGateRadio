package com.hex.evegate.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hex.evegate.R
import com.hex.evegate.ui.mvp.model.Comms

class CommsRVAdapter(private val ctx: Context) : RecyclerView.Adapter<CommsRVAdapter.CommsViewHolder>() {
    private var items: MutableList<Comms> = mutableListOf()

    companion object {
        private const val ITEM_TYPE_ROW_COMMS = 3
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommsViewHolder {
        when (viewType) {
            ITEM_TYPE_ROW_COMMS -> {
                val confessionView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.comms_item, viewGroup, false)
                return CommsViewHolder(confessionView)
            }
            else -> throw IllegalArgumentException("Wrong type!")
        }
    }

    override fun onBindViewHolder(itemViewHolder: CommsViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_ROW_COMMS) {
            initRow(itemViewHolder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is Comms) {
            ITEM_TYPE_ROW_COMMS
        } else {
            0
        }
    }

    override fun getItemCount() = items.size

    fun setData(dataList: List<Comms>?) {
        items = dataList as MutableList<Comms>
        notifyDataSetChanged()
    }

    private fun initRow(holder: CommsViewHolder, position: Int) {
        val comms = items[position]

        val layoutParams = holder.ivLogo.layoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.ivLogo.setImageDrawable(ctx.resources.getDrawable(comms.logoRes, null))
        } else { holder.ivLogo.setImageDrawable(ctx.resources.getDrawable(comms.logoRes)) }
        holder.tvCommsName.text = ctx.resources.getString(comms.commsNameRes)
        holder.ivLogo.scaleType = ImageView.ScaleType.FIT_CENTER
        holder.ivLogo.layoutParams = layoutParams


        val clickListener = { _: View ->
            ctx.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(comms.dir)))
        }
        holder.tvCommsName.setOnClickListener(clickListener)
        holder.ivLogo.setOnClickListener(clickListener)
    }

    class CommsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivLogo = itemView.findViewById<ImageView>(R.id.ivLogo)
        var tvCommsName = itemView.findViewById<TextView>(R.id.tvCommsName)
    }
}