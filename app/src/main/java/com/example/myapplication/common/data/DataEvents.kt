package com.example.myapplication.common.data

sealed class Event<T>
sealed class ApiEvent<T> : Event<T>()

class ApiSuccess<T>(val response: T?) : ApiEvent<T>()
class ApiError<T>(val error: Exception) : ApiEvent<T>()

