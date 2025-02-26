import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import '../styles/Login.css'; // 스타일 적용

const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // 비밀번호 보이기/숨기기 상태
  const emailRef = useRef(null);

  useEffect(() => {
    emailRef.current.focus(); // 이메일 입력 필드에 자동 포커스
  }, []);

  const handleLogin = () => {
    if (email.trim() === "admin" && password.trim() === "1234") {
      navigate("/whole-schedule"); // 로그인 성공 시 이동
    } else {
      alert("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
  };

  const handleGoogleLogin = () => {
      window.location.href = '/oauth2/authorization/google';
  };
  return (
    <div className="login-container">
      <h2 className="login-title">당신의 일정을 한눈에<br />캘린두 🗓️</h2>
      <p className="login-subtitle">
        일정과 to-do list를 한번에 관리하는 편리함 <br />
        캘린두에서는 유캔두잇 <br />
        환영해요!
      </p>

      <input
        ref={emailRef}
        type="text"
        placeholder="덕성여대 웹메일"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        className="login-input"
      />

      <div className="password-input-container">
        <input
          type={showPassword ? "text" : "password"}
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="login-input"
        />
        <span
          className="toggle-password"
          onClick={() => setShowPassword(!showPassword)}
        >
          {showPassword ? "" : ""} 
        </span>
      </div>

      <div className="forgot-password">
        <a href="#">비밀번호를 잊으셨나요?</a>
      </div>

      <button onClick={handleLogin} className="login-button">
        Login
      </button>

      <div className="register-container">
        <span>아직 가입 전이신가요?</span>
        <a href="#" className="register-link">
          회원가입하기
        </a>
      </div>

      <hr className="divider" />

      <button className="google-login-button" onClick={handleGoogleLogin}>G</button>

      <p className="google-login-text">덕성 웹메일로 자동 로그인</p>
    </div>
  );
};

export default Login;

