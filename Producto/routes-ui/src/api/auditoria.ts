import { api } from "./axios";

export type TipoAccion =
  | "CREAR"
  | "ACTUALIZAR"
  | "ACTIVAR"
  | "DESACTIVAR"
  | "ELIMINAR"
  | "CONFIGURAR_TARIFA"
  | "CARGA_MASIVA";

export interface RegistroAuditoria {
  id: number;
  accion: TipoAccion;
  entidad: string;
  entidadId: string | null;
  descripcion: string | null;
  usuarioId: string | null;
  usuarioEmail: string | null;
  fecha: string;
}

export const getAuditoria = async (): Promise<RegistroAuditoria[]> => {
  const { data } = await api.get<RegistroAuditoria[]>("/routes/v1/auditoria");
  return data;
};
