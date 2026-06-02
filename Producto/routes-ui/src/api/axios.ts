import axios from "axios";
import { supabase } from "../app/lib/supabase";

export const api = axios.create({
  baseURL: "http://localhost:8080/api", // Gateway service URL
});

api.interceptors.request.use(async (config) => 
{
  const { data: { session } } = await supabase.auth.getSession();

    if (!session) 
      throw new Error("No autenticado");

    const token = session.access_token;

    if (token) 
    {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});