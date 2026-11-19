package com.example.myapplication.data.model.response

import com.google.gson.annotations.SerializedName

data class LearningResourceTopicDto(
    @SerializedName("slug") val slug: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String
)

data class LearningResourceTopicsData(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("categories") val categories: List<String> = emptyList(),
    @SerializedName("topics") val topics: List<LearningResourceTopicDto> = emptyList()
)

data class LearningResourceDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("topic_slug") val topicSlug: String = "",
    @SerializedName("topic") val topic: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("headings") val headings: List<String> = emptyList(),
    @SerializedName("content") val content: List<String> = emptyList(),
    @SerializedName("important_points") val importantPoints: List<String> = emptyList(),
    @SerializedName("how_it_works") val howItWorks: List<String> = emptyList(),
    @SerializedName("step_by_step") val stepByStep: List<String> = emptyList(),
    @SerializedName("terminology") val terminology: List<String> = emptyList(),
    @SerializedName("pseudocode") val pseudocode: String = "",
    @SerializedName("time_complexity") val timeComplexity: String = "",
    @SerializedName("space_complexity") val spaceComplexity: String = "",
    @SerializedName("advantages") val advantages: List<String> = emptyList(),
    @SerializedName("limitations") val limitations: List<String> = emptyList(),
    @SerializedName("source_name") val sourceName: String = "",
    @SerializedName("source_url") val sourceUrl: String = "",
    @SerializedName("scraped_at") val scrapedAt: String? = null,
    @SerializedName("from_cache") val fromCache: Boolean = false
)

data class LearningResourceData(
    @SerializedName("cached") val cached: Boolean = false,
    @SerializedName("resource") val resource: LearningResourceDto
)
