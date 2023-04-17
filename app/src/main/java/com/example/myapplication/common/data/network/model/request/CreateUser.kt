package com.example.myapplication.common.data.network.model.request

import java.io.Serializable

class CreateUser(val fullName: String, val age: Int, val email: String) : Serializable