package com.example.circlecut

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest

class supabaseinit {
    fun getsupa(url:String,key:String):SupabaseClient{
        val supabase = createSupabaseClient(
            supabaseUrl = "https://rjvdnzsgaqfdpblvqqpq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJqdmRuenNnYXFmZHBibHZxcXBxIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY5OTg3MzAzMiwiZXhwIjoyMDE1NDQ5MDMyfQ.DBYuPVveWMHXx70cpWkYaGliQftnW_v4PBHhp1kbcp4"
        ) {
            install(GoTrue)
            install(Postgrest)
            //install other modules
        }
        return supabase
    }
}