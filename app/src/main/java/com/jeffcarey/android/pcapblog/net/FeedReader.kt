package com.jeffcarey.android.pcapblog.net

import com.jeffcarey.android.pcapblog.models.BlogPost
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.URL

object FeedReader {
   suspend fun read(feedUrl: String): List<BlogPost> {
      val url = URL(feedUrl)
      val factory = XmlPullParserFactory.newInstance()
      val xpp = factory.newPullParser()
      xpp.setInput(getInputStream(url), "UTF_8")
      val posts: MutableList<BlogPost> = mutableListOf<BlogPost>()
      var eventType = xpp.getEventType()
      var insideItem: Boolean = false
      var title: String = ""
      var link: String = ""
      var imageURL: String = ""
      var description: String = ""
      while (eventType != XmlPullParser.END_DOCUMENT) {
         if (eventType == XmlPullParser.START_TAG) {
            if (xpp.name == "item") {
               insideItem = true
            } else if (xpp.name == "title") {
               if (insideItem) {
                  title = xpp.nextText()
               }
            } else if (xpp.name == "link") {
               if (insideItem) {
                  link = xpp.nextText()
               }
            } else if (xpp.name == "media:content") {
               if (insideItem) {
                  imageURL = xpp.getAttributeValue(null, "url")
               }
            } else if (xpp.name == "description") {
               if (insideItem) {
                  description = xpp.nextText()
               }
            }
         } else if (eventType == XmlPullParser.END_TAG && xpp.name == "item") {
            insideItem = false
            posts.add(BlogPost(title, description, link, imageURL))
         }

         eventType = xpp.next()
      }

      return posts
   }

   fun getInputStream(url: URL): InputStream {
     return url.openConnection().getInputStream()
   }
}