import "@supabase/functions-js/edge-runtime.d.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!
const SERVICE_ROLE_KEY = Deno.env.get("SERVICE_ROLE_KEY")!

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, content-type",
}

// Roles internos del panel que pueden asignarse. `null`/"" quita el acceso
// al panel (deja al usuario como conductor sin privilegios).
const ROLES_VALIDOS = ["super_admin", "admin_operacional"] as const
type RolPanel = (typeof ROLES_VALIDOS)[number]

// Roles con privilegio para gestionar usuarios y asignar roles.
// "admin" es el rol legado y se trata como super administrador.
const ROLES_SUPER = new Set(["super_admin", "admin"])

interface UpdateBody {
  userId: string
  role: RolPanel | "" | null
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

  // Solo el Super Administrador puede cambiar roles.
  if (!ROLES_SUPER.has(user.app_metadata?.role)) {
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

  if (!body.userId || typeof body.userId !== "string") {
    return new Response(
      JSON.stringify({ error: "Missing field: userId (string)" }),
      { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    )
  }

  const nuevoRol = body.role ? body.role : null
  if (nuevoRol !== null && !ROLES_VALIDOS.includes(nuevoRol as RolPanel)) {
    return new Response(
      JSON.stringify({
        error: `Rol inválido. Valores: ${ROLES_VALIDOS.join(", ")} o vacío`,
      }),
      { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    )
  }

  // Evita que un super admin se quite a sí mismo el rol y quede sin acceso.
  if (body.userId === user.id) {
    return new Response(
      JSON.stringify({ error: "No puedes cambiar tu propio rol" }),
      { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    )
  }

  const adminClient = createClient(SUPABASE_URL, SERVICE_ROLE_KEY, {
    auth: { autoRefreshToken: false, persistSession: false },
  })

  // Conserva el resto del app_metadata; solo toca `role`.
  const { data: target, error: getErr } = await adminClient.auth.admin.getUserById(
    body.userId,
  )
  if (getErr || !target.user) {
    return new Response(
      JSON.stringify({ error: getErr?.message ?? "Usuario no encontrado" }),
      { status: 404, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    )
  }

  const appMetadata = { ...(target.user.app_metadata ?? {}) } as Record<string, unknown>
  if (nuevoRol) {
    appMetadata.role = nuevoRol
  } else {
    delete appMetadata.role
  }

  const { data, error } = await adminClient.auth.admin.updateUserById(body.userId, {
    app_metadata: appMetadata,
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
