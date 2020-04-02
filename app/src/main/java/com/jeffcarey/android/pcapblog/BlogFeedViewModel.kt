package com.jeffcarey.android.pcapblog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jeffcarey.android.pcapblog.models.BlogPost

class BlogFeedViewModel : ViewModel() {
    val posts: LiveData<List<BlogPost>>
    init {
        posts = BlogRepository.getPosts()
    }

    fun cancelJobs() {
        BlogRepository.cancelJobs()
    }
}
