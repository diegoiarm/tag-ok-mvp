import "../../components/home.css";
import { Navbar } from "../../components/Navbar";

export function Home() {
    return (
        <div className="home-container">

            <Navbar />

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