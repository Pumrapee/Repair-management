import router from "@/router";
export const fetchWithAuth = async (url, options = {}) => {
  const token = localStorage.getItem("accessToken");

  const headers = {
    ...options.headers,
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  if (options.body && options.body instanceof FormData) {
    delete headers["Content-Type"];
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    alert("Session expired. Please log in again.");
    localStorage.removeItem("accessToken");
    router.push("/login");
  }

  return response;
};
