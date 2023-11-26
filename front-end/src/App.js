// App.js
import React, { useState } from 'react';
import './App.css'; // 스타일 파일 추가
import Map from './Map.js'; // Map 컴포넌트 추가

function App() {
  const [data, setData] = useState('');
  const [result, setResult] = useState(null);
  const [showMap, setShowMap] = useState(false);  // Map 컴포넌트 노출 유무

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 검색창에 아무 데이터가 없는 것 방지
    if (!data.trim()) {
      alert('찾고자 하는 책의 청구기호를 입력해주세요!');
      return;
    }

    try {
      // 수정된 부분: GET 메소드 사용, 데이터는 URL에 쿼리 매개변수로 전달
      const response = await fetch(`http://localhost:8080?callNumber=${encodeURIComponent(data)}`, {
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
      setShowMap(true); // Map 컴포넌트를 표시
      
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
        </div>
       
        <div>
          {/* 결과 출력 */}
          {result && (
            <div>
              <p>"category" : "{result.category}"</p>
              <p>"bookshelfNum" : "{result.bookshelfNum}"</p>
              <p>"columnNum" : "{result.columnNum}"</p>
            </div>
          )}
        </div>
        <div>
          <a href="https://library.ajou.ac.kr/#/">도서관 홈페이지에서 '청구기호'를 확인하세요!</a>
        </div>
      </div>
    </div>
  );
}

export default App;
