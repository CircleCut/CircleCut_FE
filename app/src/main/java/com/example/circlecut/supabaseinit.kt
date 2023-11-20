package com.example.circlecut

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest

class supabaseinit {
    fun getsupa(url:String,key:String):SupabaseClient{
        val supabase = createSupabaseClient(
            supabaseUrl = "",
            supabaseKey = ""
        ) {
            install(GoTrue)
            install(Postgrest)
            //install other modules
        }
        return supabase
    }
}