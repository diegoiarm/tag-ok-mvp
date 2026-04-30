import { useState } from "react";
import { supabase } from "../lib/supabase";

export function Login() 
{
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const login = async () => {
    await supabase.auth.signInWithPassword({
      email,
      password,
    });
  };

  const register = async () => {
    await supabase.auth.signUp({
      email,
      password,
    });
  };

  return (
    <>
      <input placeholder="email" onChange={(e) => setEmail(e.target.value)} />
      <input placeholder="password" type="password" onChange={(e) => setPassword(e.target.value)} />

      <button onClick={login}>Login</button>
      <button onClick={register}>Registro</button>
    </>
  );
}