import "@supabase/functions-js/edge-runtime.d.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!
const SERVICE_ROLE_KEY = Deno.env.get("SERVICE_ROLE_KEY")!

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, content-type",
}

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders })
  }

  if (req.method !== "GET") {
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

  // Gestión de usuarios: solo Super Administrador ("super_admin" o el rol
  // legado "admin"). El Administrador Operacional no accede a este módulo.
  if (!["super_admin", "admin"].includes(user.app_metadata?.role)) {
    return new Response("Forbidden", { status: 403, headers: corsHeaders })
  }

  const adminClient = createClient(SUPABASE_URL, SERVICE_ROLE_KEY, {
    auth: { autoRefreshToken: false, persistSession: false },
  })

  const { data: usersData, error: usersError } = await adminClient.auth.admin.listUsers()
  if (usersError) {
    return new Response(JSON.stringify({ error: usersError.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    })
  }

  const userIds = usersData.users.map((u) => u.id)
  const { data: vehiculos, error: vehErr } = await adminClient
    .from("vehiculos")
    .select("*")
    .in("user_id", userIds)

  if (vehErr) {
    return new Response(JSON.stringify({ error: vehErr.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    })
  }

  const vehiculosByUser = new Map<string, typeof vehiculos>()
  for (const v of vehiculos ?? []) {
    const list = vehiculosByUser.get(v.user_id) ?? []
    list.push(v)
    vehiculosByUser.set(v.user_id, list)
  }

  const now = Date.now()
  const enriched = usersData.users.map((u) => {
    const bannedUntilStr = (u as { banned_until?: string | null }).banned_until ?? null
    const bannedUntil = bannedUntilStr ? new Date(bannedUntilStr).getTime() : null
    const activo = !bannedUntil || bannedUntil <= now
    return {
      ...u,
      activo,
      banned_until: bannedUntilStr,
      vehiculos: vehiculosByUser.get(u.id) ?? [],
    }
  })

  return new Response(JSON.stringify(enriched), {
    status: 200,
    headers: { ...corsHeaders, "Content-Type": "application/json" },
  })
})
