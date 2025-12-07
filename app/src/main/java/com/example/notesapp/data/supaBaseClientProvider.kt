package com.example.notesapp.data
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object supaBaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = com.example.notesapp.BuildConfig.SUPA_URL,
        supabaseKey = com.example.notesapp.BuildConfig.SUPA_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}