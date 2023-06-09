package com.example.storyapp.database

import androidx.paging.PagingSource
import androidx.room.*
import com.example.storyapp.remote.response.ListStoryItem

@Dao
interface StoryDao {

    @Query("SELECT* FROM stories")
    fun getAllListStories(): PagingSource<Int, ListStoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<ListStoryItem>)

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}