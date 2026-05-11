import { useUsuarios } from "../../../hooks/useUsuarios";

export function UsuariosPage() {
  const { data: usuarios, isLoading, isError } = useUsuarios();

  if (isLoading) return <p style={{ padding: "2rem" }}>Cargando usuarios...</p>;
  if (isError)   return <p style={{ padding: "2rem", color: "red" }}>Error al cargar usuarios.</p>;

  return (
    <div style={{ padding: "2rem" }}>
      <h1 style={{ marginBottom: "1.5rem" }}>Usuarios</h1>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ borderBottom: "2px solid #e5e7eb", textAlign: "left" }}>
            <th style={{ padding: "0.75rem 1rem" }}>Email</th>
            <th style={{ padding: "0.75rem 1rem" }}>Registrado</th>
            <th style={{ padding: "0.75rem 1rem" }}>Último acceso</th>
          </tr>
        </thead>
        <tbody>
          {usuarios?.map((u) => (
            <tr key={u.id} style={{ borderBottom: "1px solid #f3f4f6" }}>
              <td style={{ padding: "0.75rem 1rem" }}>{u.email ?? "—"}</td>
              <td style={{ padding: "0.75rem 1rem" }}>
                {new Date(u.created_at).toLocaleDateString("es-CL")}
              </td>
              <td style={{ padding: "0.75rem 1rem" }}>
                {u.last_sign_in_at
                  ? new Date(u.last_sign_in_at).toLocaleDateString("es-CL")
                  : "Nunca"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
