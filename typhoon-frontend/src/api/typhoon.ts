import request from "../utils/request";

// 获取台风列表
export function getTyphoonList() {
  return request.get("/typhoon/list");
}

// 获取路径
export function getTyphoonPath(id: number) {
  return request.get(`/typhoon/path/${id}`);
}