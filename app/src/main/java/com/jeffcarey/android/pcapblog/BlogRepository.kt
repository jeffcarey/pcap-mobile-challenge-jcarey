package com.jeffcarey.android.pcapblog

import androidx.lifecycle.LiveData
import com.jeffcarey.android.pcapblog.models.BlogFeed
import com.jeffcarey.android.pcapblog.net.FeedReader
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

const val FEED_URL = "https://www.personalcapital.com/blog/feed/"

object BlogRepository {
    var job: CompletableJob? = null

    fun getFeed(): LiveData<BlogFeed> {
        job = Job()
        return object: LiveData<BlogFeed>() {
            override fun onActive() {
                super.onActive()
                job?.let {
                   CoroutineScope(IO + it).launch {
                       val feed = FeedReader.read(FEED_URL)
                       withContext(Main) {
                           value = feed
                           it.complete()
                       }
                   }
                }
            }
        }
    }

    fun cancelJobs() {
        job?.cancel()
    }
}
