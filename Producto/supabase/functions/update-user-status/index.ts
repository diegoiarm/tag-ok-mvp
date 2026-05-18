import "@supabase/functions-js/edge-runtime.d.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!
const SERVICE_ROLE_KEY = Deno.env.get("SERVICE_ROLE_KEY")!

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, content-type",
}

interface UpdateBody {
  userId: string
  activo: boolean
}

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders })
  }

  if (req.method !== "POST") {
    return new Response("Method not allowed", { status: 405, headers: corsHeaders })
  }

  const authHeader = req.headers.get("Authorization")
  if (!authHeader) {
    return new Response("Unauthorized", { status: 401, headers: corsHeaders })
  }

  const anonClient = createClient(SUPABASE_URL, Deno.env.get("SUPABASE_ANON_KEY")!)
  const { data: { user }, error: authError } = await anonClient.auth.getUser(
    authHeader.replace("Bearer ", "")
  )

  if (authError || !user) {
    return new Response("Unauthorized", { status: 401, headers: corsHeaders })
  }

  if (user.app_metadata?.role !== "admin") {
    return new Response("Forbidden", { status: 403, headers: corsHeaders })
  }

  let body: UpdateBody
  try {
    body = await req.json()
  } catch {
    return new Response(JSON.stringify({ error: "Invalid JSON body" }), {
      status: 400,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    })
  }

  if (!body.userId || typeof body.activo !== "boolean") {
    return new Response(
      JSON.stringify({ error: "Missing fields: userId (string), activo (boolean)" }),
      { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    )
  }

  if (body.userId === user.id) {
    return new Response(
      JSON.stringify({ error: "No puedes desactivarte a ti mismo" }),
      { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    )
  }

  const adminClient = createClient(SUPABASE_URL, SERVICE_ROLE_KEY, {
    auth: { autoRefreshToken: false, persistSession: false },
  })

  const ban_duration = body.activo ? "none" : "876000h"
  const { data, error } = await adminClient.auth.admin.updateUserById(body.userId, {
    ban_duration,
  })

  if (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    })
  }

  return new Response(JSON.stringify({ ok: true, user: data.user }), {
    status: 200,
    headers: { ...corsHeaders, "Content-Type": "application/json" },
  })
})
