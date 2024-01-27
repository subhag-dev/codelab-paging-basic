package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.max

private const val LOAD_DELAY_MILLIS = 3_000L
private val firstArticleCreatedTime = LocalDateTime.now()

const val STARTING_KEY = 0

class ArticlePagingSource : PagingSource<Int, Article>() {

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(article.id - state.config.pageSize / 2)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val start = params.key ?: STARTING_KEY
        val range = start.until(start + params.loadSize)
        if (start != STARTING_KEY) delay(LOAD_DELAY_MILLIS)
        return LoadResult.Page(
            data = range.map { number ->
                Article(
                    id = number,
                    title = "Article $number",
                    description = "This is article $number description",
                    created = firstArticleCreatedTime.minusDays(number.toLong())
                )
            },
            prevKey = when (start) {
                STARTING_KEY -> null
                else -> ensureValidKey(range.first - params.loadSize)
            },
            nextKey = range.last + 1
        )
    }

}