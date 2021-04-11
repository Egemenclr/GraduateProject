package com.egemeninceler.donempro.Model

import com.google.gson.annotations.SerializedName

data class Numbeo(
    @SerializedName("name") val name: String,
    @SerializedName("prices") val prices: List<Prices>
)
data class Healtcare(
    @SerializedName("speed") val speed: Double,
    @SerializedName("cost") val cost: Double,
    @SerializedName("location") val location: Double,
    @SerializedName("modern_equipment") val modern_equipment: Double
)

data class Pollution(
    @SerializedName("air_quality") val air_quality: Double,
    @SerializedName("noise_and_light_pollution") val noise: Double,
    @SerializedName("clean_and_tidy") val clean_and_tidy: Double,
    @SerializedName("water_pollution") val water_pollution: Double,
    @SerializedName("green_and_parks_quality") val parks: Double,
    @SerializedName("city_iddddd") val id: Int
)

data class Crime(
    @SerializedName("worried_skin_ethnic_religion") val racist: Double,
    @SerializedName("safe_alone_night") val safe_alone_night: Double,
    @SerializedName("level_of_crime") val level_of_crime: Double,
    @SerializedName("worried_attacked") val worried_attacked: Double,
    @SerializedName("worried_home_broken") val worried_home_broken: Double,
)

data class CPI(
    @SerializedName("items") val items: List<CPIModel>
)
data class CPIModel(
    @SerializedName("cpi_factor") val cpi_factor: Double,
    @SerializedName("item_id") val item_id: Int,
    @SerializedName("category") val category: String,
)