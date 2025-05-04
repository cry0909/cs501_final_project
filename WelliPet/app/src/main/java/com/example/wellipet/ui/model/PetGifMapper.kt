// File: com/example/wellipet/ui/model/PetGifMapper.kt
package com.example.wellipet.ui.model

import com.example.wellipet.R

object PetGifMapper {
    private val map = mapOf(
        "dog" to mapOf(
            "happy"   to R.drawable.dog_happy,
            "thirsty" to R.drawable.dog_thirsty,
            "sleepy"  to R.drawable.dog_sleep
        ),
        "cat" to mapOf(
            "happy"   to R.drawable.cat_happy,
            "thirsty" to R.drawable.cat_thirsty,
            "sleepy"  to R.drawable.cat_sleep
        ),
        "rabbit" to mapOf(
            "happy"   to R.drawable.rabbit_happy,
            "thirsty" to R.drawable.rabbit_thirsty,
            "sleepy"  to R.drawable.rabbit_sleep
        )
    )

    /**
     * Given a petKey (e.g. "dog", "cat", "rabbit")
     * and a status ("happy", "thirsty", "sleepy"),
     * return the corresponding GIF resource ID.
     */
    fun get(petKey: String?, status: String): Int {
        val key = petKey?.takeIf { map.containsKey(it) } ?: "dog"
        return map[key]?.get(status)
            ?: error("Missing gif for $key + $status")
    }
}
