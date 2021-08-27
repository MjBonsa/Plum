package com.plumcoop.plum.models



class PlaceInfo(
    val name : String = "",
    val address : String= "",
    val url : String = "",
    val point : String = ""
){
    constructor() : this(name = "")
}