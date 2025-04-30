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
            "happy"   to R.drawable.dog_happy,
            "thirsty" to R.drawable.dog_thirsty,
            "sleepy"  to R.drawable.dog_sleep
        )
    )

    /** 从资源 id 推断 petType，再映射 gif **/
    fun get(petResId: Int?, status: String): Int {
        val petKey = when (petResId) {
            R.drawable.pet_dog    -> "dog"
            R.drawable.pet_cat    -> "cat"
            R.drawable.pet_rabbit -> "rabbit"
            else                  -> "dog"
        }
        return map[petKey]?.get(status)
            ?: error("Missing gif for $petKey + $status")
    }
}
