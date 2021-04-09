package com.egemeninceler.donempro.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.egemeninceler.donempro.Model.Cities
import com.egemeninceler.donempro.R

class GridAdapter(
    private var context: Context,
    private var citiesList: List<Cities>

) : BaseAdapter() {
    val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.gridcolumn, parent, false)
        val imageView = rowView.findViewById<ImageView>(R.id.gridColumnImage)
        imageView.setImageResource(citiesList.get(position).imageId)


        return rowView
    }

    override fun getItem(position: Int): Any? {
        return citiesList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return citiesList.size
    }


}