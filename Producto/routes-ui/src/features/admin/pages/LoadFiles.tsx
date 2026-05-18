import { useRef, useState, type ChangeEvent, type DragEvent } from "react";
import {
  CheckCircle2,
  CloudUpload,
  FileJson,
  Loader2,
  Trash2,
  XCircle,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { cn } from "@/lib/utils";

type FileStatus = "pending" | "uploading" | "success" | "error";

interface FileItem {
  id: string;
  file: File;
  status: FileStatus;
  message?: string;
}

const API_ENDPOINT = "http://localhost:8000/autopistas";

export function LoadFiles() {
  const [files, setFiles] = useState<FileItem[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const addFiles = (incoming: FileList | File[]) => {
    const incomingArr = Array.from(incoming).filter((f) =>
      f.name.toLowerCase().endsWith(".json"),
    );
    if (incomingArr.length === 0) return;
    setFiles((prev) => [
      ...prev,
      ...incomingArr.map((file) => ({
        id: `${file.name}-${file.lastModified}-${file.size}`,
        file,
        status: "pending" as FileStatus,
      })),
    ]);
  };

  const handleSelect = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) addFiles(e.target.files);
    e.target.value = "";
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files) addFiles(e.dataTransfer.files);
  };

  const removeFile = (id: string) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
  };

  const clearCompleted = () => {
    setFiles((prev) => prev.filter((f) => f.status !== "success"));
  };

  const updateFile = (id: string, patch: Partial<FileItem>) => {
    setFiles((prev) => prev.map((f) => (f.id === id ? { ...f, ...patch } : f)));
  };

  const uploadOne = async (item: FileItem) => {
    updateFile(item.id, { status: "uploading", message: undefined });
    try {
      const text = await item.file.text();
      const json = JSON.parse(text);
      const res = await fetch(API_ENDPOINT, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(json),
      });
      if (!res.ok) {
        const errText = await res.text().catch(() => "");
        throw new Error(`HTTP ${res.status}${errText ? `: ${errText.slice(0, 120)}` : ""}`);
      }
      updateFile(item.id, { status: "success" });
    } catch (err) {
      updateFile(item.id, {
        status: "error",
        message: err instanceof Error ? err.message : "Error desconocido",
      });
    }
  };

  const handleUpload = async () => {
    const pendientes = files.filter(
      (f) => f.status === "pending" || f.status === "error",
    );
    if (pendientes.length === 0) return;
    setIsUploading(true);
    for (const item of pendientes) {
      await uploadOne(item);
    }
    setIsUploading(false);
  };

  const stats = {
    total: files.length,
    pending: files.filter((f) => f.status === "pending").length,
    success: files.filter((f) => f.status === "success").length,
    error: files.filter((f) => f.status === "error").length,
  };

  const hayPendientes = stats.pending > 0 || stats.error > 0;

  return (
    <div>
      <div className="mx-auto max-w-4xl px-6 py-8 space-y-6">
        <header>
          <h1 className="text-2xl font-semibold tracking-tight">Subir JSONs</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Carga archivos JSON de autopistas para crear o actualizar pórticos y
            tarifas.
          </p>
        </header>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Archivos</CardTitle>
            <CardDescription>
              Solo se aceptan archivos <code className="font-mono">.json</code>{" "}
              con el formato de Autopista del backend.
            </CardDescription>
          </CardHeader>

          <CardContent className="space-y-4">
            <div
              role="button"
              tabIndex={0}
              onClick={() => inputRef.current?.click()}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  e.preventDefault();
                  inputRef.current?.click();
                }
              }}
              onDragOver={(e) => {
                e.preventDefault();
                setIsDragging(true);
              }}
              onDragLeave={() => setIsDragging(false)}
              onDrop={handleDrop}
              className={cn(
                "flex flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed p-8 text-center cursor-pointer transition-colors",
                isDragging
                  ? "border-brand bg-brand-soft/60"
                  : "border-border hover:border-brand/40 hover:bg-muted/40",
              )}
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-brand-soft text-brand">
                <CloudUpload className="h-5 w-5" />
              </div>
              <p className="text-sm font-medium">
                Arrastra archivos aquí o haz click para seleccionar
              </p>
              <p className="text-xs text-muted-foreground">
                JSON · múltiples archivos permitidos
              </p>
              <input
                ref={inputRef}
                type="file"
                multiple
                accept="application/json,.json"
                onChange={handleSelect}
                className="hidden"
              />
            </div>

            {files.length > 0 && (
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2 text-xs text-muted-foreground">
                    <span>{stats.total} archivos</span>
                    {stats.success > 0 && (
                      <Badge variant="outline" className="text-emerald-600 dark:text-emerald-400 border-emerald-200 dark:border-emerald-900">
                        {stats.success} subidos
                      </Badge>
                    )}
                    {stats.error > 0 && (
                      <Badge variant="destructive">{stats.error} con error</Badge>
                    )}
                  </div>
                  {stats.success > 0 && (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={clearCompleted}
                      disabled={isUploading}
                    >
                      Limpiar subidos
                    </Button>
                  )}
                </div>

                <ul className="space-y-1.5">
                  {files.map((item) => (
                    <FileRow
                      key={item.id}
                      item={item}
                      onRemove={removeFile}
                      disabled={isUploading}
                    />
                  ))}
                </ul>
              </div>
            )}
          </CardContent>
        </Card>

        <div className="flex items-center justify-end gap-2">
          {files.length > 0 && !isUploading && (
            <Button
              variant="outline"
              onClick={() => setFiles([])}
              disabled={isUploading}
            >
              Vaciar lista
            </Button>
          )}
          <Button onClick={handleUpload} disabled={isUploading || !hayPendientes}>
            {isUploading ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                Subiendo...
              </>
            ) : (
              <>
                <CloudUpload className="h-4 w-4" />
                {stats.error > 0 ? "Reintentar errores" : "Subir archivos"}
              </>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

interface FileRowProps {
  item: FileItem;
  onRemove: (id: string) => void;
  disabled: boolean;
}

function FileRow({ item, onRemove, disabled }: FileRowProps) {
  return (
    <li className="flex items-center gap-3 rounded-md border border-border bg-card p-2.5">
      <div className="flex h-9 w-9 items-center justify-center rounded-md bg-muted shrink-0">
        <FileJson className="h-4 w-4 text-muted-foreground" />
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium truncate">{item.file.name}</p>
        <p className="text-xs text-muted-foreground">
          {formatBytes(item.file.size)}
          {item.message && (
            <span className="text-destructive ml-2">· {item.message}</span>
          )}
        </p>
      </div>
      <StatusBadge status={item.status} />
      <Button
        variant="ghost"
        size="icon-sm"
        onClick={() => onRemove(item.id)}
        disabled={disabled || item.status === "uploading"}
        title="Quitar de la lista"
      >
        <Trash2 className="h-4 w-4 text-muted-foreground" />
      </Button>
    </li>
  );
}

function StatusBadge({ status }: { status: FileStatus }) {
  switch (status) {
    case "uploading":
      return (
        <Badge variant="outline" className="gap-1">
          <Loader2 className="h-3 w-3 animate-spin" />
          Subiendo
        </Badge>
      );
    case "success":
      return (
        <Badge
          variant="outline"
          className="gap-1 text-emerald-600 dark:text-emerald-400 border-emerald-200 dark:border-emerald-900"
        >
          <CheckCircle2 className="h-3 w-3" />
          Subido
        </Badge>
      );
    case "error":
      return (
        <Badge variant="destructive" className="gap-1">
          <XCircle className="h-3 w-3" />
          Error
        </Badge>
      );
    default:
      return <Badge variant="secondary">Pendiente</Badge>;
  }
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}
