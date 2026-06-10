import client from "./client";

export const getAllUrls = async () => {
  const res = await client.get("/urls");
  return res.data;
};

export const createUrl = async (longUrl, description) => {
  const res = await client.post("/urls", { longUrl, description });
  return res.data;
};

export const updateUrl = async (code, longUrl, description) => {
  const res = await client.put(`/urls/${code}`, { longUrl, description });
  return res.data;
};

export const deleteUrl = async (code) => {
  await client.delete(`/urls/${code}`);
};
