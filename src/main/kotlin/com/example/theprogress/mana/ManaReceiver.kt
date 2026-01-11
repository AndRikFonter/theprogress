package com.example.theprogress.mana


class ManaReceiver<T : IMana>(private val mana: T) : IManaReceiver {
    override val currentMana: Int
        get() = mana.currentMana

    override val baseMaxMana: Int
        get() = mana.baseMaxMana

    override val maxTransfer: Int
        get() = baseMaxMana - currentMana

    override fun transfer(points: Int) {
        mana.fill((points))
    }
}
