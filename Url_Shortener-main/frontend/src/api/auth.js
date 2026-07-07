import client from "./client";

export const register = (username, password) =>
  client.post("/register", { username, password });

export const login = async (username, password) => {
  const res = await client.post("/login", { username, password });
  return res.data;
};
