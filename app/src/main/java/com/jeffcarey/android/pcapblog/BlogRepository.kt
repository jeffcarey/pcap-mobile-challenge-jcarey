package com.jeffcarey.android.pcapblog

import androidx.lifecycle.LiveData
import com.jeffcarey.android.pcapblog.net.FeedReader
import com.jeffcarey.android.pcapblog.models.BlogPost
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

const val FEED_URL = "https://www.personalcapital.com/blog/feed/"

object BlogRepository {
    var job: CompletableJob? = null

    fun getPosts(): LiveData<List<BlogPost>> {
        job = Job()
        return object: LiveData<List<BlogPost>>() {
            override fun onActive() {
                super.onActive()
                job?.let {
                   CoroutineScope(IO + it).launch {
                       val posts = FeedReader.read(FEED_URL)
                       withContext(Main) {
                           value = posts
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
