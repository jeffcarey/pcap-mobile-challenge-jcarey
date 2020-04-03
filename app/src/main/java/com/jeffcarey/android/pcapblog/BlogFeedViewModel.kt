package com.jeffcarey.android.pcapblog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.jeffcarey.android.pcapblog.models.BlogFeed

class BlogFeedViewModel : ViewModel() {
    private var feed: LiveData<BlogFeed>
    val mediator = MediatorLiveData<BlogFeed>()
    init {
        feed = BlogRepository.getFeed()
        mediator.addSource(feed, { mediator.value = it})
    }

    fun refresh() {
        mediator.removeSource(feed)
        feed = BlogRepository.getFeed()
        mediator.addSource(feed, { mediator.value = it})
    }

    fun cancelJobs() {
        BlogRepository.cancelJobs()
    }
}
