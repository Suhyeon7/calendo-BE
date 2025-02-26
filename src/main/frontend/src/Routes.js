import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import Login from "./pages/Login"; // Login 페이지 import
import WholeSchedule from "./pages/WholeSchedule"; // 로그인 성공 시 이동할 페이지 import
import Home from "./pages/home/Home"; // 기존 Home 페이지

const Router = () => {
    return (
        <BrowserRouter>
            <div>
                <Routes>
                    <Route path="/" element={<Login />} /> {/* 기본 경로는 로그인 페이지 */}
                    <Route path="/whole-schedule" element={<WholeSchedule />} /> {/* 로그인 성공 시 이동할 페이지 */}
                    <Route path="/home" element={<Home />} /> {/* 기존 Home 페이지는 /home 경로로 이동 */}
                </Routes>
            </div>
        </BrowserRouter>
    );
};

export default Router;
