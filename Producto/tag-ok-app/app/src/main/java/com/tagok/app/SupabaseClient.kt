package com.tagok.app

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

// TODO: reemplaza estos valores con los de tu proyecto en Supabase Dashboard → Settings → API
val supabase = createSupabaseClient(
    supabaseUrl = "YOUR_SUPABASE_URL",
    supabaseKey = "YOUR_SUPABASE_ANON_KEY"
) {
    install(Auth)
}
