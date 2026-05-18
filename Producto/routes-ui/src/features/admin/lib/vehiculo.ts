import {
  Car,
  Bike,
  Bus,
  Truck,
  type LucideIcon,
} from "lucide-react";

const TIPO_LABEL: Record<string, string> = {
  AUTO: "Automóvil",
  MOTO: "Motocicleta",
  CAMIONETA: "Camioneta",
  BUS: "Bus",
  CAMION: "Camión",
  CAMION_REMOLQUE: "Camión con remolque",
};

const TIPO_ICON: Record<string, LucideIcon> = {
  AUTO: Car,
  MOTO: Bike,
  CAMIONETA: Truck,
  BUS: Bus,
  CAMION: Truck,
  CAMION_REMOLQUE: Truck,
};

export function tipoVehiculoLabel(tipo: string): string {
  return TIPO_LABEL[tipo] ?? tipo;
}

export function tipoVehiculoIcon(tipo: string): LucideIcon {
  return TIPO_ICON[tipo] ?? Car;
}

export function categoriaLabel(categoria: number): string {
  switch (categoria) {
    case 1: return "Cat. 1 — Motos";
    case 2: return "Cat. 2 — Autos/Camionetas";
    case 3: return "Cat. 3 — Buses";
    case 4: return "Cat. 4 — Camiones 2 ejes";
    case 5: return "Cat. 5 — Camiones 3 ejes";
    case 6: return "Cat. 6 — Camiones 4+ ejes";
    default: return `Categoría ${categoria}`;
  }
}
