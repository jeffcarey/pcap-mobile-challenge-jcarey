package com.jeffcarey.android.pcapblog.models

data class BlogFeed(var feedTitle: String = "", var posts: MutableList<BlogPost> = mutableListOf<BlogPost>()) {
}