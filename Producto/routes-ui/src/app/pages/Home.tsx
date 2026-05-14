import "../../components/home.css";
import { Navbar } from "../../components/Navbar";
import { useRef, useState } from "react";

export function Home()
{
    const videoRef = useRef<HTMLVideoElement>(null);
    const [mostrarHover, setMostrarHover] = useState(false);

    const reproducirVideo = () =>
    {
        videoRef.current?.play();
        setMostrarHover(true)
    };

    return (
        <div className="home-container">

            <Navbar />

            {/* Botón invisible */}
            <button
                onClick={reproducirVideo}
                style={{
                    position: "fixed",
                    top: 0,
                    left: 0,
                    width: "40px",
                    height: "40px",
                    opacity: 0,
                    border: "none",
                    background: "transparent",
                    cursor: "default",
                    zIndex: 9999,
                }}
            />

            {/* no lo borren pls */}
            {mostrarHover && (
                <div
                    style={{
                        position: "fixed",
                        inset: 0,
                        background: "rgba(0,0,0,0.85)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        zIndex: 10000,
                    }}
                >
                    <video
                        ref={videoRef}
                        src="/ssstik.io_@minionfan532_1777578739485.mp4"
                        autoPlay
                        onEnded={() => setMostrarHover(false)}
                        style={{
                            maxWidth: "90%",
                            maxHeight: "90%",
                            borderRadius: "12px",
                        }}
                    />
                </div>
            )}

            {/* Header */}
            <header className="header">
                <h1>Dashboard</h1>
                <p>Bienvenido al panel de administración de peajes TAG</p>
            </header>

            {/* Cards */}
            <section className="cards-grid">

                <div className="card">
                    <h3>Autopistas</h3>
                    <h1>5</h1>
                    <p>Autopistas registradas</p>
                </div>

                <div className="card">
                    <h3>Pórticos</h3>
                    <h1>8</h1>
                    <p>Puntos de cobro configurados</p>
                </div>

                <div className="card">
                    <h3>Tarifas</h3>
                    <h1>9</h1>
                    <p>Tarifas activas</p>
                </div>

                <div className="card">
                    <h3>Usuarios</h3>
                    <h1>5</h1>
                    <p>Usuarios registrados</p>
                </div>

            </section>
        </div>
    );
}