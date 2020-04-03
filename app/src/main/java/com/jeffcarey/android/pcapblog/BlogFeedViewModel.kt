package com.jeffcarey.android.pcapblog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jeffcarey.android.pcapblog.models.BlogFeed

class BlogFeedViewModel : ViewModel() {
    val feed: LiveData<BlogFeed>
    init {
        feed = BlogRepository.getFeed()
    }

    fun cancelJobs() {
        BlogRepository.cancelJobs()
    }
}
