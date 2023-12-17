import React, { useState } from 'react';
import './App.css'; // 스타일 파일 추가
import Map from './Map'; // Map 컴포넌트 추가

function App() {
  const [data, setData] = useState('');
  const [result, setResult] = useState(null);
  const [mapData, setMapData] = useState(null);  // Map 컴포넌트 데이터 전달

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 검색창에 아무 데이터가 없는 것 방지
    if (!data.trim()) {
      alert('찾고자 하는 책의 청구기호를 입력해주세요!');
      return;
    }

    try {
      // 수정된 부분: GET 메소드 사용, 데이터는 URL에 쿼리 매개변수로 전달
      const response = await fetch(`http://3.142.20.149:8080?callNumber=${encodeURIComponent(data)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('서버 응답이 실패했습니다.');
      }

      const resultData = await response.json();
      setResult(resultData);

      // 수정된 부분: Map 컴포넌트에 result 데이터 전달
      setMapData(resultData);
      
      // 수정된 부분: form-container의 style.display 속성을 none으로 설정 복사 문제 해결
      document.querySelector('.form-container').style.display = 'none';
    
    } catch (error) {
      console.error('Error:', error);
    }
    setData('');
  };

  return (
    <div className="app-container">
      <div className="white-box">
        <div>
          {/* 로고 이미지 */}
          <img id="logo-icon" src="/ajbk.png" alt="Search Icon" />
        </div>
        <div className="form-container">
          <p><b>내가 원하는 책과 만나다, Ajou Booking</b></p>
          <form onSubmit={handleSubmit} className="search-form">
            <input
              type="text"
              className="form-control"
              placeholder="'청구기호'를 입력하세요!"
              value={data}
              onChange={(e) => setData(e.target.value)}
            />
            <button type="submit" className="btn btn-primary" id="hidden-btn">위치 검색</button>
          </form>
          <div>
          <a href="https://library.ajou.ac.kr/#/">도서관 홈페이지에서 '청구기호'를 확인하세요!</a>
        </div>
        </div>
        <div>
          {/* 결과 출력 */}
          {result && (
            <Map mapData={result} />
          )}
        </div>
      </div>
    </div>
  );
}

export default App;

