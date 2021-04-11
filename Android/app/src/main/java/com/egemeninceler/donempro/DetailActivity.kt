package com.egemeninceler.donempro

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.egemeninceler.donempro.Adapter.GridAdapter
import com.egemeninceler.donempro.Adapter.InfoLinearLayoutAdapter
import com.egemeninceler.donempro.Adapter.LinearLayoutAdapter
import com.egemeninceler.donempro.Model.*
import com.egemeninceler.donempro.Network.CountryService
import com.egemeninceler.donempro.Network.ServiceBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {


    private lateinit var gridView: GridView
    private lateinit var listView: ListView
    private lateinit var infoListView: ListView
    lateinit var adapter: GridAdapter
    lateinit var linearAdapter: LinearLayoutAdapter
    lateinit var infoLinearLayoutAdapter: InfoLinearLayoutAdapter
    var cityStatictics: ArrayList<Any> = ArrayList<Any>()
    val items: ArrayList<Cities> = ArrayList<Cities>()
    var progressBar: ProgressBar? = null

    var averagePrices: ArrayList<String> = ArrayList<String>()
    var itemNames: ArrayList<String> = ArrayList<String>()
    var restaurantItems: ArrayList<Prices> = ArrayList<Prices>()
    var marketItems: ArrayList<Prices> = ArrayList<Prices>()
    var transportItems: ArrayList<Prices> = ArrayList<Prices>()
    var utilitiesItems: ArrayList<Prices> = ArrayList<Prices>()
    var sportItems: ArrayList<Prices> = ArrayList<Prices>()
    var childCareItems: ArrayList<Prices> = ArrayList<Prices>()
    var buyApartmentItems: ArrayList<Prices> = ArrayList<Prices>()
    var clothingItems: ArrayList<Prices> = ArrayList<Prices>()
    var rentItems: ArrayList<Prices> = ArrayList<Prices>()
    var salaryItems: ArrayList<Prices> = ArrayList<Prices>()
    var cpiItems: ArrayList<CPI> = ArrayList<CPI>()

    var allPriceLists: ArrayList<ArrayList<Prices>> = ArrayList<ArrayList<Prices>>()
    var pieChart: PieChart? = null
    lateinit var view: WebView


    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        var cityMap = mutableMapOf<String, Int>()
        cityMap.put("Istanbul", 2800)
        cityMap.put("Amsterdam", 1680)
        cityMap.put("Manchester", 1580)
        cityMap.put("Beijing", 250)
        cityMap.put("Sao Paulo", 150)
        cityMap.put("Moscow", 122)
        cityMap.put("New York", 1260)
        cityMap.put("Paris", 1530)
        cityMap.put("Berlin", 1580)
        setItems(items)

        pieChart = findViewById(R.id.piechart)
        view = findViewById<WebView>(R.id.webViewnumbeo)
        progressBar = findViewById(R.id.progressBar)

        val setTime = Handler(Looper.getMainLooper())
        setTime.post(object : Runnable {
            override fun run() {
                var currentHour = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                var currentDate =
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                val day: String = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                    1 -> {
                        "Pazar"
                    }
                    2 -> {
                        "Pazartesi"
                    }
                    3 -> {
                        "Salı"
                    }
                    4 -> {
                        "Çarşamba"
                    }
                    5 -> {
                        "Perşembe"
                    }
                    6 -> {
                        "Cuma"
                    }
                    else -> "Cumartesi"
                }
                detailHour.text = currentHour
                detailDate.text = currentDate + " " + day
                setTime.postDelayed(this, 2000)
            }


        })





        adapter = GridAdapter(this, items)
        gridView = findViewById<GridView>(R.id.gridView)
        listView = findViewById<ListView>(R.id.listView)
        infoListView = findViewById(R.id.infoListView)

        adapter.notifyDataSetChanged()
        gridView.adapter = adapter

        var bottomSheet = findViewById<View>(R.id.bottomSheetTable)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object:
            BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                println("state: "+ newState)
                if (newState == 4){
                    bottomSheet.visibility = View.INVISIBLE
                }
            }
        })
        gridView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                getCityStatisticsRate(items[position].cityName)

                loadCountries(
                    items[position].cityName,
                    items[position].currency,
                    cityMap.get(items[position].cityName)!!
                )
                if(progressBar != null){

                    progressBar!!.visibility = View.VISIBLE

                }
            }
        }



        numbeoButton.setOnClickListener {
            view.visibility = View.VISIBLE
            view.getSettings().setJavaScriptEnabled(true)
            view.loadUrl("https://www.numbeo.com/cost-of-living/")

        }
    }

    override fun onBackPressed() {

        view.visibility = View.GONE


    }


    private fun getCPI(cpiItems: ArrayList<CPI>, itemID: Int): Double {

        val iterator = cpiItems.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            for (i in 0..(item.items.size - 1)) {
                if (itemID == item.items[i].item_id) {
                    return item.items[i].cpi_factor
                }
            }
        }

        return 1.0
    }

    fun setData(allPriceList: ArrayList<ArrayList<Prices>>, cpiItems: ArrayList<CPI>) {
        var liste = hashMapOf<String, Double>()
        for (item in allPriceList) {
            var total = 0.0
            for (i in item) {
                var cpi_factor = getCPI(cpiItems, i.item_id)
                total += i.average_price*cpi_factor

            }
            //item.item_name.split(",").dropLast(1)

            liste.put(item[0].item_name.split(",").last(), total)



        }
        print("a")
        pieChart?.clearChart()
        // Set the data and color to the pie chart
        pieChart?.addPieSlice(
            PieModel(
                "Transportation",
                liste.get(" Transportation")!!.toFloat(),
                Color.parseColor("#3366CC")
            )

        )
        pieChart?.addPieSlice(
            PieModel(
                "Clothing And Shoes",
                liste.get(" Clothing And Shoes")!!.toFloat(),
                Color.parseColor("#F83028")
            )
        )
        pieChart?.addPieSlice(
            PieModel(
                "Sports And Leisure",
                liste.get(" Sports And Leisure")!!.toFloat(),
                Color.parseColor("#673AB7")
            )
        )
        pieChart?.addPieSlice(
            PieModel(
                "Markets",
                liste.get(" Markets")!!.toFloat(),
                Color.parseColor("#007600")
            )
        )
        pieChart?.addPieSlice(
            PieModel(
                "Utilities",
                liste.get(" Utilities (Monthly)")!!.toFloat(),
                Color.parseColor("#AD3DA4")
            )
        )
        pieChart?.addPieSlice(
            PieModel(
                "Restaurants",
                liste.get(" Restaurants")!!.toFloat(),
                Color.parseColor("#FF9800")
            )
        )

        // To animate the pie chart
        pieChart?.startAnimation()
    }


    private fun setItems(items: ArrayList<Cities>) {
        items.add(Cities("Amsterdam", "EUR", R.mipmap.amsterdam))
        items.add(Cities("Berlin", "EUR", R.mipmap.berlin))
        items.add(Cities("Sao Paulo", "EUR", R.mipmap.brazil))
        items.add(Cities("Beijing", "EUR", R.mipmap.cin))
        items.add(Cities("New York", "EUR", R.mipmap.goldengate))
        items.add(Cities("Istanbul", "TRY", R.mipmap.istanbul))
        items.add(Cities("Moscow", "EUR", R.mipmap.moscow))
        items.add(Cities("Paris", "EUR", R.mipmap.paris))
        items.add(Cities("Manchester", "EUR", R.mipmap.london))
    }

    private fun getCityStatisticsRate(cityName: String) {

        cityStatictics.clear()
        val destinationService = ServiceBuilder.buildService(CountryService::class.java)
        val requestPollution = destinationService.getPollution(cityName)

        requestPollution.enqueue(object : Callback<Pollution> {
            override fun onResponse(call: Call<Pollution>, response: Response<Pollution>) {
                if (response.isSuccessful) {
                    cityStatictics.add(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Pollution>, t: Throwable) {
                Log.d("Response", "countrylist : fail $t")

                Toast.makeText(applicationContext, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        val requestCrime = destinationService.getCrime(cityName)
        requestCrime.enqueue(object : Callback<Crime> {

            override fun onResponse(call: Call<Crime>, response: Response<Crime>) {
                if (response.isSuccessful) {
                    cityStatictics.add(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Crime>, t: Throwable) {
                Log.d("Response", "countrylist : fail $t")

                Toast.makeText(applicationContext, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })


        val requestHealthcare = destinationService.getHealtcare(cityName)
        requestHealthcare.enqueue(object : Callback<Healtcare> {

            override fun onResponse(call: Call<Healtcare>, response: Response<Healtcare>) {
                if (response.isSuccessful) {
                    cityStatictics.add(response.body()!!)

                }
            }

            override fun onFailure(call: Call<Healtcare>, t: Throwable) {
                Log.d("Response", "countrylist : fail $t")

                Toast.makeText(applicationContext, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        val requestCPI = destinationService.getCPI(cityName)
        requestCPI.enqueue(object : Callback<CPI> {

            override fun onResponse(call: Call<CPI>, response: Response<CPI>) {
                if (response.isSuccessful) {
                    cpiItems.add(response.body()!!)

                }
            }

            override fun onFailure(call: Call<CPI>, t: Throwable) {
                Log.d("Response", "countrylist : fail $t")

                Toast.makeText(applicationContext, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })


    }

    private fun loadCountries(countryName: String, currency: String, asgari: Int) {
        clearAllLists()
        //initiate the service
        val destinationService = ServiceBuilder.buildService(CountryService::class.java)
        val requestCall = destinationService.getNumbeo(countryName, currency)
        //make network call asynchronously
        requestCall.enqueue(object : Callback<Numbeo> {
            override fun onResponse(call: Call<Numbeo>, response: Response<Numbeo>) {
                //Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    val countryList = response.body()!!
                    cityName.text = countryList.name
                    val currencyText = findViewById<TextView>(R.id.currency)
                    currencyText.text = currency
                    for (item in countryList.prices) {

                        when {
                            item.item_name.contains("Restaurants") -> {
                                restaurantItems.add(item)
                            }
                            item.item_name.contains("Markets") -> {
                                marketItems.add(item)
                            }
                            item.item_name.contains("Transportation") -> {
                                transportItems.add(item)
                            }
                            item.item_name.contains("Rent Per") -> {
                                rentItems.add(item)
                            }
                            item.item_name.contains("Sports") -> {
                                sportItems.add(item)
                            }
                            item.item_name.contains("Clothing") -> {
                                clothingItems.add(item)
                            }
                            item.item_name.contains("Buy Apartment") -> {
                                buyApartmentItems.add(item)
                            }
                            item.item_name.contains("Salaries") -> {
                                salaryItems.add(item)
                            }
                            item.item_name.contains("Childcare") -> {
                                childCareItems.add(item)
                            }
                            item.item_name.contains("Utilities") -> {
                                utilitiesItems.add(item)
                            }
                        }
                        averagePrices.add(item.average_price.toString())
                        itemNames.add(item.item_name)
                    }

                    allPriceLists.add(restaurantItems)
                    allPriceLists.add(marketItems)
                    allPriceLists.add(transportItems)
                    allPriceLists.add(utilitiesItems)
                    allPriceLists.add(sportItems)
                    allPriceLists.add(childCareItems)
                    allPriceLists.add(clothingItems)
                    allPriceLists.add(rentItems)
                    allPriceLists.add(buyApartmentItems)
                    allPriceLists.add(salaryItems)
                    val iterator = allPriceLists.iterator()
                    while (iterator.hasNext()) {
                        val item = iterator.next()
                        if (item.size == 0) {
                            iterator.remove()
                        }
                    }
                    setData(allPriceLists, cpiItems)

                    infoLinearLayoutAdapter =
                        InfoLinearLayoutAdapter(applicationContext, cityStatictics)
                    infoLinearLayoutAdapter.notifyDataSetChanged()
                    infoListView.adapter = infoLinearLayoutAdapter

                    linearAdapter = LinearLayoutAdapter(applicationContext, allPriceLists, asgari)
                    linearAdapter.notifyDataSetChanged()
                    listView.adapter = linearAdapter

                    setListViewHeightBasedOnChildren(infoListView)
                    setListViewHeightBasedOnChildren(listView)

                    bottomSheetTable.visibility = View.VISIBLE
                    if(progressBar != null){
                        progressBar!!.visibility = View.INVISIBLE


                    }
                } else {
                    Log.d("Response", "countrylist : hata")
                    Toast.makeText(
                        applicationContext,
                        "Something went wrong ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Numbeo>, t: Throwable) {
                Log.d("Response", "countrylist : fail $t")

                Toast.makeText(applicationContext, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {
        /**
         * Birden fazla listview tek view olarak scroll edebilmek için.
         */
        val listAdapter = listView.adapter ?: return
        val desiredWidth =
            MeasureSpec.makeMeasureSpec(listView.width, MeasureSpec.UNSPECIFIED)
        var totalHeight = 0
        var view: View? = null
        for (i in 0 until listAdapter.count) {
            view = listAdapter.getView(i, view, listView)
            if (i == 0) view.layoutParams = ViewGroup.LayoutParams(
                desiredWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            totalHeight += view.measuredHeight
        }
        val params = listView.layoutParams
        params.height = (totalHeight).toInt() + listView.dividerHeight * listAdapter.count
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun clearAllLists() {
        allPriceLists.clear()
        restaurantItems.clear()
        marketItems.clear()
        transportItems.clear()
        rentItems.clear()
        sportItems.clear()
        clothingItems.clear()
        buyApartmentItems.clear()
        salaryItems.clear()
        childCareItems.clear()
        utilitiesItems.clear()
        cpiItems.clear()
    }
}
