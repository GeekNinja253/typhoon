import axios from "axios";

const request = axios.create({
  baseURL: "/",
  timeout: 10000
});

// 请求拦截（以后放token）
request.interceptors.request.use(config => {
  const user = localStorage.getItem("user");
  if (user) {
    // 可扩展 token
    // config.headers.Authorization = JSON.parse(user).token;
  }
  return config;
});

export default request;