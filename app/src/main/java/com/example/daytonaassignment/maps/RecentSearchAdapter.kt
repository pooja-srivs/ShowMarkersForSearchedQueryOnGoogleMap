package com.example.daytonaassignment.maps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.daytonaassignment.R
import kotlinx.android.synthetic.main.recent_place_items.view.*

class RecentSearchAdapter private constructor(
    private val diffUtil: DiffUtil.ItemCallback<TextListItem>,
    private val onItemClick: (Int) -> Unit
) : ListAdapter<TextListItem, TextItemVH>(diffUtil){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TextListItem>() {

            override fun areItemsTheSame(oldItem: TextListItem, newItem: TextListItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: TextListItem, newItem: TextListItem): Boolean =
                oldItem == newItem

        }

        fun newInstance(onItemClick : (Int) -> Unit) = RecentSearchAdapter(
            DIFF_CALLBACK, onItemClick
        )
    }

    fun getItemAt(position: Int): TextListItem? {
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemVH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recent_place_items, parent, false)
        return TextItemVH(view)
    }

    override fun onBindViewHolder(holder: TextItemVH, position: Int) {
       holder.bind(requireNotNull(getItemAt(position)), onItemClick)
    }

}

class TextItemVH(view : View) : RecyclerView.ViewHolder(view){

    val text_movie_item = view.text_recent_place_item

    fun bind(
        textListItem: TextListItem,
        onItemClick: (Int) -> Unit
    ){

        text_movie_item.setText(textListItem.textRecentPlaces)

        itemView.setOnClickListener {
            onItemClick.invoke(adapterPosition)
        }

    }
}

data class TextListItem(val textRecentPlaces: String, val latitude : Double = 0.0, val longitude : Double = 0.0)