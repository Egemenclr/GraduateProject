package com.egemeninceler.donempro.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.egemeninceler.donempro.Model.Crime
import com.egemeninceler.donempro.Model.Healtcare
import com.egemeninceler.donempro.Model.Pollution
import com.egemeninceler.donempro.Model.Prices
import com.egemeninceler.donempro.R

class InfoLinearLayoutAdapter (context: Context,
                               private val infoListe: ArrayList<Any>) : BaseAdapter(){
    val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.infolinearlayout, parent, false)
        val infoStatics = rowView.findViewById<TextView>(R.id.informationStatics)

        val infoS = rowView.findViewById<TextView>(R.id.infoS)
        val infoV = rowView.findViewById<TextView>(R.id.infoValue)
        if(infoListe.size >0){
            val infoBaslik = infoListe.get(position).javaClass.simpleName
            infoStatics.text = infoBaslik
            println("item: "+ infoListe.get(position))


            var infoSTemp = ""
            var infoVTemp = ""
            when{
                infoBaslik == "Healtcare" -> {

                    infoSTemp = "Speed \n" + "Cost \n" + "Location \n" + "Modern Equipment"
                    var speedValue = "%"+String.format("%.1f",((((infoListe.get(position) as Healtcare).speed)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Healtcare).cost)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Healtcare).location)+2)/4)*100) + "\n"+
                            "%"+String.format("%.1f",((((infoListe.get(position) as Healtcare).modern_equipment)+2)/4)*100)

                    infoVTemp =  speedValue

                    infoS.text = infoSTemp
                    infoV.text = infoVTemp

                }
                infoBaslik == "Pollution" -> {

                    infoSTemp = "Air Quality \n" + "Clean And Tidy \n" + "Noise Pollution \n" + "Green And Parks Quality \n" + "Water Pollution"
                    var speedValue = "%"+String.format("%.1f",((((infoListe.get(position) as Pollution).air_quality)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Pollution).clean_and_tidy)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Pollution).noise)+2)/4)*100) + "\n"+
                            "%"+String.format("%.1f",((((infoListe.get(position) as Pollution).parks)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Pollution).water_pollution)+2)/4)*100)

                    infoVTemp =  speedValue

                    infoS.text = infoSTemp
                    infoV.text = infoVTemp

                }
                infoBaslik == "Crime" -> {

                    infoSTemp = "Level Of Crime \n" + "Racism \n" + "Safe Alone Night \n" + "Worried Attacked \n" + "Worried Home Broken"
                    var speedValue = "%"+String.format("%.1f",((((infoListe.get(position) as Crime).level_of_crime)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Crime).racist)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Crime).safe_alone_night)+2)/4)*100) + "\n"+
                            "%"+String.format("%.1f",((((infoListe.get(position) as Crime).worried_attacked)+2)/4)*100) + "\n" +
                            "%"+String.format("%.1f",((((infoListe.get(position) as Crime).worried_home_broken)+2)/4)*100)

                    infoVTemp =  speedValue

                    infoS.text = infoSTemp
                    infoV.text = infoVTemp

                }
            }


        }
        return rowView
    }

    override fun getItem(position: Int): Any {
        return infoListe[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return infoListe.size
    }

}