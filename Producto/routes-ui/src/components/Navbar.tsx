import { Link } from "react-router-dom";
import "./Navbar.css";


export function Navbar() {
    return (
        <nav className="navbar">

            <div className="navbar-logo">
                TAG Admin
            </div>

            <div className="navbar-links">

                <Link to="/">
                    Inicio
                </Link>

                <Link to="/mapa">
                    Mapa
                </Link>

                <Link to="/usuarios">
                    Usuarios
                </Link>

                <Link to="/porticos">
                    Pórticos
                </Link>

            </div>

        </nav>
    );
}