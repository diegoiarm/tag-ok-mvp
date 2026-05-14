import { useState } from "react";

export function LoadFiles() 
{
    const [files, setFiles] = useState([]);

    const handleChange = (e) => 
    {
        setFiles(Array.from(e.target.files));
    };

    const handleUpload = async () => 
        {
        for (const file of files) 
        {
            try 
            {
                const text = await file.text();

                const jsonData = JSON.parse(text);

                const response = await fetch(
                    "http://localhost:8000/autopistas",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify(jsonData),
                    }
                );

                const data = await response.json();

                console.log(`Archivo ${file.name} enviado`, data);

            } catch (error) {
                console.error(`Error con ${file.name}`, error);
            }
        }
    };

    return (
        <div>
            <h2>Sistema de carga JSON</h2>

            <input
                type="file"
                multiple
                accept=".json"
                onChange={handleChange}
            />

            <button onClick={handleUpload}>
                Enviar JSONs
            </button>
        </div>
    );
}