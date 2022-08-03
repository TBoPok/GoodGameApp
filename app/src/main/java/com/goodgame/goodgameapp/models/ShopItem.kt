package com.goodgame.goodgameapp.models

data class ShopItem(
    val id: Int,
    val cost: Int,
    val title: String,
    var isAvailable: Boolean,
)

data class ShopItemResponse(
    val shop_items: List<ShopItem>
)

data class ShopBuyResponse(
    val status: Boolean,
    val info: String,
)
