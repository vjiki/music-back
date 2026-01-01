package com.vjiki.music.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FirebaseAuthRequest(
    @JsonProperty("idToken")
    val idToken: String
)


