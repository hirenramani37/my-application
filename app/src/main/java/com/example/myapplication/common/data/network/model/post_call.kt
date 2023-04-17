package com.example.myapplication.common.data.network.model

data class post_call(
    val limit: Int, // 30
    val posts: List<Post>,
    val skip: Int, // 0
    val total: Int // 150
)

data class Post(
    val body: String, // His mother had always taught him not to ever think of himself as better than others. He'd tried to live by this motto. He never looked down on those who were less fortunate or who had less money than him. But the stupidity of the group of people he was talking to made him change his mind.
    val id: Int, // 1
    val reactions: Int, // 2
    val tags: List<String>,
    val title: String, // His mother had always taught him
    val userId: Int // 9
)
