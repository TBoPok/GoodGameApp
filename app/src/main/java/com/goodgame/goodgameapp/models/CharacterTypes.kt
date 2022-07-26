package com.goodgame.goodgameapp.models

import com.goodgame.goodgameapp.R

val characterTypes = listOf (
    CharacterModel(
        name = "ЛИДЕР",
        id_name = "charisma",
        description = "Описание",
        card_bg = R.drawable.leader2,
        power = 5,
        intelligence = 5,
        charisma = 7,
        luck = 5,
    ),
    CharacterModel(
        name = "БОЕЦ",
        id_name = "power",
        description = "Описание",
        card_bg = R.drawable.warrior2,
        power = 7,
        intelligence = 5,
        charisma = 5,
        luck = 5,
    ),
    CharacterModel(
        name = "ИГРОК",
        id_name = "fortune",
        description = "Описание",
        card_bg = R.drawable.gambler2,
        power = 5,
        intelligence = 5,
        charisma = 5,
        luck = 7,
    ),
    CharacterModel(
        name = "УЧЕНЫЙ",
        id_name = "intellect",
        description = "Описание",
        card_bg = R.drawable.scientist2,
        power = 5,
        intelligence = 7,
        charisma = 5,
        luck = 5,
    ),
)