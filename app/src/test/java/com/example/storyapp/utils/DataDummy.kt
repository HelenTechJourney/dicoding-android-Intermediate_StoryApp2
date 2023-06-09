package com.example.storyapp.utils

import com.example.storyapp.remote.response.*


object DataDummy {
    fun generateDummyStoryEntity(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..5) {
            val stories = ListStoryItem(
                "Title $i",
                "https://story-api.dicoding.dev/images/stories/photos-1684857638787_PwM0yZf6.jpg",
                "2022-02-22T22:22:22Z",
                "This is name",
                "This is description",
                null,
                null
            )
            storyList.add(stories)
        }
        return storyList
    }

    fun generateDummyRequestLogin(): RequestLogin {
        return RequestLogin("123@gmail.com", "123456")
    }

    fun generateDummyResponseLogin(): LoginResponse {
        val newLoginResult = LoginResult("lkjhgf", "helen", "test-token")
        return LoginResponse(newLoginResult, false, "Login Successfully")
    }

    fun generateDummyRequestSignUp(): RequestSignup {
        return RequestSignup("Helen Dj", "kuwul@kuwul.com", "01928374")
    }
}