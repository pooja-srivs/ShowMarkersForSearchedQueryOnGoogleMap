package com.example.daytonaassignment.maps.di

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.daytonaassignment.R
import kotlinx.android.synthetic.main.recent_search_place_items.view.*

class UserPlaceAdapter private constructor(
    private val diffUtil: DiffUtil.ItemCallback<RecentSearchListItem>,
    private val onItemClick: (Int) -> Unit,
    private val onFavItemClick: (Int, Boolean) -> Unit
) : ListAdapter<RecentSearchListItem, TextItemVH>(diffUtil){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecentSearchListItem>() {

            override fun areItemsTheSame(oldItem: RecentSearchListItem, newItem: RecentSearchListItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: RecentSearchListItem, newItem: RecentSearchListItem): Boolean =
                oldItem == newItem

        }

        fun newInstance(onItemClick : (Int) -> Unit, onFavItemClick: (Int, Boolean) -> Unit) = UserPlaceAdapter(
            DIFF_CALLBACK, onItemClick, onFavItemClick
        )
    }

    fun getItemAt(position: Int): RecentSearchListItem? {
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemVH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recent_search_place_items, parent, false)
        return TextItemVH(view)
    }

    override fun onBindViewHolder(holder: TextItemVH, position: Int) {
       holder.bind(requireNotNull(getItemAt(position)), onItemClick, onFavItemClick)
    }

}

class TextItemVH(view : View) : RecyclerView.ViewHolder(view){

    val text_name = view.text_name
    val text_address = view.text_address
    val iv_fav = view.iv_fav
    val iv_fav_loc = view.iv_fav_loc

    fun bind(
        recentSearchListItem: RecentSearchListItem,
        onItemClick: (Int) -> Unit,
        onFavItemClick: (Int, Boolean) -> Unit
    ){

        text_name.setText(recentSearchListItem.textname)
        text_address.setText(recentSearchListItem.textAddress)

        if (recentSearchListItem.isFav) {
            iv_fav_loc.visibility = VISIBLE
            iv_fav.visibility = GONE
        }else{
            iv_fav.visibility = VISIBLE
            iv_fav_loc.visibility = GONE
        }

        iv_fav.setOnClickListener {
            recentSearchListItem.isFav = true
            onFavItemClick.invoke(adapterPosition, recentSearchListItem.isFav)
        }

        itemView.setOnClickListener {
            onItemClick.invoke(adapterPosition)
        }
    }
}

data class RecentSearchListItem(val textname: String,
                                val textAddress: String,
                                var isFav: Boolean,
                                val latitude: Double,
                                val longitude: Double,
                                val rating : Double,
                                val currentlyOpen : Boolean,
                                val distance : Int)