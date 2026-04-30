import { createClient } from "@supabase/supabase-js";

export const supabase = createClient(
    "https://ibafvqmoqeabmziyzifk.supabase.co",
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImliYWZ2cW1vcWVhYm16aXl6aWZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3ODc5NTIsImV4cCI6MjA5MjM2Mzk1Mn0.ZYYd0xW69sq1CyT6DqsMj23zFSfedrGaed35AhE-GEs"
);