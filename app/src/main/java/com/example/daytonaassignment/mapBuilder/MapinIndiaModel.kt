package com.example.daytonaassignment.mapBuilder

data class MapinIndiaModel (
    val suggestedLocations : ArrayList<SuggestedLocations>
    )

data class SuggestedLocations(
    val type : String,
    val typeX : Int,
    val placeAddress : String,
    val latitude : Double,
    val longitude : Double,
    val eLoc : String,
    val entryLatitude : String,
    val entryLongitude : String,
    val placeName : String,
    val alternateName : String,
    val addressTokens : AddressToken
)

data class AddressToken(
    val houseNumber : String,
    val houseName : String,
    val poi : String,
    val street : String,
    val subSubLocality : String,
    val subLocality : String,
    val locality : String,
    val village : String,
    val subDistrict : String,
    val district : String,
    val city : String,
    val state : String,
    val pincode : String
    )
/*
*
* "type": "POI",
			"typeX": 7,
			"placeAddress": "Bahai Temple Road, Bahapur, Kalkaji, New Delhi, Delhi, 110019",
			"latitude": 28.553302,
			"longitude": 77.258677,
			"eLoc": "JQ5QN8",
			"entryLatitude": 28.552913,
			"entryLongitude": 77.261556,
			"placeName": "Lotus Temple",
			"alternateName": "Bahai House Of Worship,bahai Temple",
			"keywords": [
				"RCNTST"
			],
			"addressTokens": {
				"houseNumber": "",
				"houseName": "",
				"poi": "Lotus Temple",
				"street": "Bahai Temple Road",
				"subSubLocality": "",
				"subLocality": "Bahapur",
				"locality": "Kalkaji",
				"village": "",
				"subDistrict": "Kalkaji",
				"district": "South East Delhi District",
				"city": "New Delhi",
				"state": "Delhi",
				"pincode": "110019"
			},
			"p": 3,
			"orderIndex": 1 */