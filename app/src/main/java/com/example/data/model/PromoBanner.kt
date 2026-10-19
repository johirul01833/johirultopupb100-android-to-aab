package com.example.data.model

data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String = "", // Network image or empty for gradient/artwork
    val badge: String = "Special Offer",
    val actionUrl: String = "", // Web link, YouTube, TikTok video, Ads link
    val serviceTarget: String = "TIKTOK", // TIKTOK, CHAMET, FREEFIRE, EPAY, EXCHANGE, EXTERNAL
    val gradientStartHex: String = "#FF1E0B36",
    val gradientEndHex: String = "#FF6B0B99",
    val isEnabled: Boolean = true
)
