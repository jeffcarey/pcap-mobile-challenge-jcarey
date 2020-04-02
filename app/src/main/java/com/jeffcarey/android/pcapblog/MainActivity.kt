package com.jeffcarey.android.pcapblog

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeffcarey.android.pcapblog.net.PhotoDownloader
import com.jeffcarey.android.pcapblog.models.BlogPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val LOADER_WIDTH: Int = 200
const val LOADER_HEIGHT: Int = 200

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BlogFeedViewModel
    private lateinit var mainView: LinearLayout
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var blogPostAdapter: BlogPostAdapter
    private lateinit var loaderContainer: RelativeLayout
    private lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = LinearLayout(this)
        blogPostAdapter = BlogPostAdapter()

        articlesRecyclerView = RecyclerView(this)
        val spanCount = if (resources.getBoolean(R.bool.isTablet)) { 3 } else { 2 }
        val recyclerLayoutManager = GridLayoutManager(this, spanCount)
        recyclerLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) { spanCount } else { 1 }
            }
        }
        articlesRecyclerView.apply {
            layoutManager = recyclerLayoutManager
            adapter = blogPostAdapter
        }

        val loaderLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        loaderContainer = RelativeLayout(this)
        loader = ProgressBar(this)
        val layoutParams = RelativeLayout.LayoutParams(LOADER_WIDTH, LOADER_HEIGHT)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        loaderContainer.addView(loader, layoutParams)
        mainView.addView(loaderContainer, loaderLayoutParams)
        setContentView(mainView)

        // Get data via ViewModel
        viewModel = ViewModelProvider(this).get(BlogFeedViewModel::class.java)
        viewModel.posts.observe(this, Observer { posts ->
            blogPostAdapter.setData(posts)
            blogPostAdapter.notifyDataSetChanged()
            mainView.removeView(loaderContainer)
            mainView.addView(articlesRecyclerView)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val refreshId = R.id.menu_item_refresh

        if (menu.findItem(refreshId) == null) {
            val item = menu.add(Menu.NONE, refreshId, 0, R.string.menu_item_refresh)
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_refresh -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    inner class BlogPostHolder constructor(itemView: LinearLayout) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var blogPost: BlogPost
        val blogTitle = itemView.findViewById<TextView>(R.id.blog_post_title)
        val blogImage  = itemView.findViewById<ImageView>(R.id.blog_post_image)
        val blogDescription = itemView.findViewById<TextView>(R.id.blog_post_description)
        init {
            itemView.setOnClickListener(this)
        }

        fun bindBlogPost(item: BlogPost, isTopArticle: Boolean) {
            blogPost = item
            blogTitle.text = Html.fromHtml(blogPost.title, FROM_HTML_MODE_LEGACY)

            if (isTopArticle) {
                blogTitle.maxLines = 1
                blogDescription.text = Html.fromHtml(blogPost.description, FROM_HTML_MODE_LEGACY)
            }
        }

        val bindDrawable: (Drawable) -> Unit = blogImage::setImageDrawable

        override fun onClick(view: View) {
            val targetUri = Uri.parse(blogPost.link + "?displayMobileNavigation=0")
            val intent = WebPageActivity.newIntent(this@MainActivity, targetUri)
            startActivity(intent)
        }
    }

    inner class BlogPostAdapter() : RecyclerView.Adapter<BlogPostHolder>() {
        private var blogPosts: List<BlogPost> = mutableListOf()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogPostHolder {
            val itemView = LinearLayout(parent.context)
            val scale = resources.displayMetrics.density;
            // convert to dip
            val horizontalPadding = (20 * scale * 0.5f).toInt();
            itemView.orientation = LinearLayout.VERTICAL
            itemView.setPadding(horizontalPadding, 0, horizontalPadding, 0)

            val titleView = createTitleView(parent.context, 2)
            val descriptionView = createDescriptionView(parent.context)
            val imageView = ImageView(parent.context)
            val imageViewLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300)
            imageView.id = R.id.blog_post_image

            itemView.addView(imageView, imageViewLayout)
            itemView.addView(titleView)
            itemView.addView(descriptionView)

            val border = GradientDrawable()
            border.setColor(Color.WHITE)
            border.setStroke(2, Color.LTGRAY)
            itemView.background = border

            return BlogPostHolder(itemView)
        }

        override fun getItemCount(): Int {
            return blogPosts.size
        }

        override fun onBindViewHolder(holder: BlogPostHolder, position: Int) {
            val post = blogPosts[position]

            holder.bindBlogPost(post, position == 0)

            val placeholder: Drawable = ColorDrawable()
            holder.bindDrawable(placeholder)
            CoroutineScope(IO).launch {
                val photo = PhotoDownloader.getPhoto(post.imageURL)
                withContext(Main) {
                    val photoDrawable: Drawable = BitmapDrawable(resources, photo)
                    holder.bindDrawable(photoDrawable)
                }
            }
        }

        fun setData(posts: List<BlogPost>) {
            blogPosts = posts
        }
    }
}

fun createTitleView(context: Context, maxTitleLines: Int, fontSize: Float = 14.0f): TextView {
    val titleView: TextView = TextView(context)
    titleView.apply {
        id = R.id.blog_post_title
        ellipsize = TextUtils.TruncateAt.END
        maxLines = maxTitleLines
        textSize = fontSize
        gravity = Gravity.CENTER
        setTypeface(null, Typeface.BOLD)
    }


    return titleView
}

fun createDescriptionView(context: Context): TextView {
    val descriptionView: TextView = TextView(context)
    descriptionView.apply {
        id = R.id.blog_post_description
        ellipsize = TextUtils.TruncateAt.END
        maxLines = 2
    }

    return descriptionView
}
