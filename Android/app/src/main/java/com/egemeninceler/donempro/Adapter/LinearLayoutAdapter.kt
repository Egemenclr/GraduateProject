package com.egemeninceler.donempro.Adapter

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.egemeninceler.donempro.Model.Healtcare

import com.egemeninceler.donempro.Model.Prices
import com.egemeninceler.donempro.R
import kotlinx.android.synthetic.main.linearlayoutcolumn.view.*

class LinearLayoutAdapter(private val context: Context,
                          private val liste: ArrayList<ArrayList<Prices>>,
                           private val asgariUcret: Int) : BaseAdapter() {
    val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.linearlayoutcolumn, parent, false)
        val information = rowView.findViewById<TextView>(R.id.information)

        val name = rowView.findViewById<TextView>(R.id.textName)

        val price = rowView.findViewById<TextView>(R.id.textPrice)
        val asgari = rowView.findViewById<TextView>(R.id.textAsgari)

        var nameTemp = ""
        var priceTemp = ""
        var asgariTemp = ""

        if(liste.get(position).size > 0) {
            information.text = liste.get(position).get(0).item_name.split(",").last()

            nameTemp = ""
            priceTemp = ""
            asgariTemp = ""
            var tempN = false
            var tempSize = 0
            for (item in liste[position]) {
                if (tempN) {
                    priceTemp += "\n"
                    asgariTemp += "\n"
                    tempN = false
                }
                var tempI = item.item_name.split(",").dropLast(1)
                nameTemp += tempI.joinToString(",") + "\n"

                if(tempI.joinToString(",").length > 38){
                    tempN = true
                }

                if (tempSize == liste[position].size - 1) {
                    priceTemp += String.format("%.1f", item.average_price)
                    if (asgariUcret > item.average_price){
                        asgariTemp += String.format("%.0f", asgariUcret/(item.average_price))
                    }else{
                        asgariTemp += String.format("%.0f",(item.average_price)/asgariUcret)
                    }


                } else {
                    priceTemp += String.format("%.1f", item.average_price) + "\n"
                    if (asgariUcret > item.average_price){
                        asgariTemp += String.format("%.0f", asgariUcret/(item.average_price)) + "\n"
                    }else{
                        asgariTemp += String.format("%.0f",(item.average_price)/asgariUcret) + "\n"
                    }

                }
                tempSize += 1
            }

            name.text = nameTemp
            price.text = priceTemp
            asgari.text = asgariTemp
        }
        return rowView

    }

    override fun getItem(position: Int): Any {
        return liste[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return liste.size
    }

}