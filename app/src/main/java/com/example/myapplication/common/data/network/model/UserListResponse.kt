package com.example.myapplication.common.data.network.model

data class UserListResponse(
    val data: List<Data>,
    val totalPages: Int, // 75
    val totalPassengers: Int // 75
) {
    data class Data(
        val __v: Int, // 0
        val _id: String, // 6446157725e93e7e1d435c16
        val airline: List<Airline>,
        val name: String, // Vũ
        val trips: Int // 250
    ) {
        data class Airline(
            val __v: Int, // 0
            val _id: String, // 644614fc25e93ea10c435bda
            val country: String, // Sri Lanka
            val established: String, // 1990
            val head_quaters: String, // Katunayake, Sri Lanka
            val id: Int, // 1
            val logo: String, // https://upload.wikimedia.org/wikipedia/en/thumb/9/9b/Qatar_Airways_Logo.svg/sri_lanka.png
            val name: String, // Sri Lankan Airways
            val slogan: String, // From Sri Lanka
            val website: String // www.srilankaairways.com
        )
    }
}