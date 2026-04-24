package com.tagok.app

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

val supabase = createSupabaseClient(
    supabaseUrl = "https://ibafvqmoqeabmziyzifk.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImliYWZ2cW1vcWVhYm16aXl6aWZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3ODc5NTIsImV4cCI6MjA5MjM2Mzk1Mn0.ZYYd0xW69sq1CyT6DqsMj23zFSfedrGaed35AhE-GEs"
) {
    install(Auth)
}
