package com.plumcoop.plum.models



data class UserProfile(
    var name : String = "",
    var places : ArrayList<String> = arrayListOf<String>()
){
    constructor() : this(name = "")

}