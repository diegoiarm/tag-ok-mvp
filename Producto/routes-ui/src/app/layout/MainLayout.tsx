import { Outlet, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { supabase } from "../lib/supabase";

export function MainLayout() {
  const { user } = useAuth();

  const logout = async () => {
    await supabase.auth.signOut();
  };

  return (
    <>
      <header>
        <nav>
          <Link to="/">Home</Link> |{" "}
          <Link to="/mapa">Mapa</Link>

          <span style={{ marginLeft: 10 }}>
            {user ? (
              <>
                👤 {user.email}
                <button onClick={logout}>Logout</button>
              </>
            ) : (
              <Link to="/login">Login</Link>
            )}
          </span>
        </nav>
      </header>

      <main>
        <Outlet />
      </main>
    </>
  );
}