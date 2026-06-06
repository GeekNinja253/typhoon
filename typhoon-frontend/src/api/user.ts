import request from "../utils/request";

// 登录
export function login(data: any) {
  return request.post("/user/login", data);
}

// 注册
export function register(data: any) {
  return request.post("/user/register", data);
}