import { Link } from "react-router-dom";

export function Home()
{
    return(
        <>
            Hola soy el hogar
            <button>
                <Link to="/mapa">Ir al mapa</Link>
            </button>
        </>
    );
}