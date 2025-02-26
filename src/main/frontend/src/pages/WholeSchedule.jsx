import React from "react";

const WholeSchedule = () => {
  return (
    <div style={styles.container}>
      <h1>📅 전체 일정 페이지</h1>
      <p>로그인에 성공하셨습니다! 여기에 일정을 표시하세요.</p>
    </div>
  );
};

const styles = {
  container: {
    textAlign: "center",
    marginTop: "50px",
  }
};

export default WholeSchedule;
