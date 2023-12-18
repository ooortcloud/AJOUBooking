import React, { useEffect, useRef } from 'react';
import './Map.css';

const Map = ({ mapData }) => {
  const canvasRef = useRef(null);

  useEffect(() => {
    const drawRedDotOnCanvas = (img, coordinates) => {
      const canvas = canvasRef.current;
      const context = canvas.getContext('2d');

      context.clearRect(0, 0, canvas.width, canvas.height);

      if (img.width && img.height) {
        const ratioX = canvas.width / img.width;
        const ratioY = canvas.height / img.height;

        // 그림 그리기
        context.fillStyle = 'red';
        context.beginPath();
        context.arc(coordinates.x * ratioX, coordinates.y * ratioY, 5, 0, 2 * Math.PI);
        context.fill();

        // 붉은색 고리 추가
        context.strokeStyle = 'red';
        context.lineWidth = 20;
        context.beginPath();
        context.arc(coordinates.x * ratioX, coordinates.y * ratioY, 100, 0, 2 * Math.PI);
        context.stroke();

        const x = coordinates.x * ratioX;
        const y = coordinates.y * ratioY;

        context.fillStyle = 'black';
        context.fillText(`(x: ${x}, y: ${y})`, x, y);
      }
    };

    if (mapData) {
      const img = new Image();
      img.src = '/map.png';

      img.onload = () => {
        const canvas = canvasRef.current;
        canvas.width = img.width;
        canvas.height = img.height;

        console.log('Image Width:', img.width, 'Image Height:', img.height);

        const coordinates = getBookshelfCoordinates(mapData.category, mapData.bookshelfNum, mapData.columnNum, img.width, img.height);

        console.log('Computed Coordinates:', coordinates);

        drawRedDotOnCanvas(img, coordinates);
        // magnific-popup 관련 코드 제외
      };
    }
  }, [mapData]);

  const getBookshelfCoordinates = (category, bookshelfNum, columnNum, imageWidth, imageHeight) => {
    if (category >= 0 && category < 100) {
      const bookshelfStartX = 1168 + (bookshelfNum - 1) * 64.5;
      const bookshelfStartY = 45;
      const bookshelfEndY = 165;

      if (columnNum <= 6) {
        const x = bookshelfStartX;
        const y = bookshelfStartY + (columnNum - 1) * 24;
        return { x, y };
      }
  
      const x = bookshelfStartX + 21;
      const y = bookshelfEndY - (columnNum - 7) * 24;
      return { x, y };
    } else if (category >= 100 && category < 200) { // 100 섹션에서의 좌표 찍기 알고리즘. 
      if (category == 100 && bookshelfNum == 1) { // 000 섹션과 맞닺은 구간의 좌표 처리. 
      const bookshelfStartX = 1898.5;
      const bookshelfEndY = 165;
      
      const x = bookshelfStartX;
      const y = bookshelfEndY - (columnNum - 7) * 24;
      return { x, y};
      } else if (category == 110 && bookshelfNum == 1) { // 000 섹션 단락의 마지막 13책장의 처리
        const bookshelfStartX = 1942;
        const bookshelfStartY = 45;
        const bookshelfEndY = 165;

        if (columnNum <= 6) {
          const x = bookshelfStartX;
          const y = bookshelfStartY + (columnNum - 1) * 24;
          return { x, y };
        }
    
        const x = bookshelfStartX + 21;
        const y = bookshelfEndY - (columnNum - 7) * 24;
        return { x, y };
      } else if (bookshelfNum > 1) { // 중앙 가로로 책장이 위치할 경우의 코드 진행, 책장 2번부터 진행
        const bookshelfStartX = 1384;
        const bookshelfEndX = 1264;
        const bookshelfStartY = 332 + (bookshelfNum - 2) * 65.2;

        if (bookshelfNum == 4) { // 4번째 책장의 경우 책장이 기존열과 달라서 예외 처리. 
          const bookshelfStartX = 1336;
          const bookshelfEndX = 1264;
          const bookshelfStartY = 332 + (bookshelfNum - 2) * 65.2;

          if (columnNum <= 4) {
            const x = bookshelfStartX - (columnNum - 1) * 24;
            const y = bookshelfStartY;
            return { x, y };
          }
            const x = bookshelfEndX + (columnNum - 5) * 24;
            const y = bookshelfStartY + 21;
            return { x, y };
          }  
        if (columnNum <= 6) {
        const x = bookshelfStartX - (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX + (columnNum - 7) * 24;
        const y = bookshelfStartY + 21;
        return { x, y };
      }  
    } else if (category >= 200 && category < 300) { // 200 섹션에서의 좌표 찍기 알고리즘. 
      const bookshelfStartX = 1384;
      const bookshelfEndX = 1264;
      const bookshelfStartY = 918 + (bookshelfNum - 1) * 65.2;

      if (bookshelfNum == 3) { // 구간 이동시의 경우를 표기
        const bookshelfStartX = 1384;
        const bookshelfEndX = 1264;
        const bookshelfStartY = 1168;

        if (columnNum <= 6) {
          const x = bookshelfStartX - (columnNum - 1) * 24;
          const y = bookshelfStartY;
          return { x, y };
        }
          const x = bookshelfEndX + (columnNum - 7) * 24;
          const y = bookshelfStartY + 21;
          return { x, y };
        }  

      if (columnNum <= 6) {
        const x = bookshelfStartX - (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX + (columnNum - 7) * 24;
        const y = bookshelfStartY + 21;
        return { x, y };
    } else if (category >= 300 && category < 400) { // 300 섹션에서의 좌표 찍기 알고리즘.
      if (bookshelfNum < 13) { // 책장 1~13번까지의 해당 사항.
      const bookshelfStartX = 1384;
      const bookshelfEndX = 1264;
      const bookshelfStartY = 1233 + (bookshelfNum - 1) * 65.2;

      if (bookshelfNum == 4) { // 4번째 책장의 경우 책장이 기존열과 달라서 예외 처리. 
        const bookshelfStartX = 1336;
        const bookshelfEndX = 1264;
        const bookshelfStartY = 1233 + (bookshelfNum - 1) * 65.2;

        if (columnNum <= 4) {
          const x = bookshelfStartX - (columnNum - 1) * 24;
          const y = bookshelfStartY;
          return { x, y };
        }
          const x = bookshelfEndX + (columnNum - 5) * 24;
          const y = bookshelfStartY + 21;
          return { x, y };
        }  
      if (bookshelfNum > 8) { // 화장실 옆 4층 지도 우측 하단부
        const bookshelfStartX = 1397;
        const bookshelfEndX = 1278;
        const bookshelfStartY = 1932 + (bookshelfNum - 9) * 57;

        if (columnNum <= 6) {
          const x = bookshelfStartX - (columnNum - 1) * 24;
          const y = bookshelfStartY;
          return { x, y };
        }
          const x = bookshelfEndX + (columnNum - 7) * 24;
          const y = bookshelfStartY + 21;
          return { x, y };
      }
      
      if (columnNum <= 6) {
        const x = bookshelfStartX - (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX + (columnNum - 7) * 24;
        const y = bookshelfStartY + 21;
        return { x, y };
        
    } else if (bookshelfNum >= 13 && bookshelfNum < 17) { // 4층 지도 우측하단부 좌측 섹션
        const bookshelfStartX = 1088;
        const bookshelfEndX = 1208;
        const bookshelfStartY = 2124 - (bookshelfNum - 13) * 57;

      if (columnNum <= 6) {
        const x = bookshelfStartX + (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX - (columnNum - 7) * 24;
        const y = bookshelfStartY - 21;
        return { x, y };
    } else if (bookshelfNum >= 17 && bookshelfNum < 21) { // 4층 지도 중앙부 섹션
      if (bookshelfNum == 17) { // 벽면에 바로 붙어있는 책장의 경우 한쪽만 책이 꽂혀있기에.
      const bookshelfStartX = 967.5;
      const bookshelfEndX = 1135;
      const bookshelfStartY = 1622;

      const x = bookshelfStartX + (columnNum - 1) * 24;
      const y = bookshelfStartY;
      return { x, y };
      } 
      const bookshelfStartX = 967.5;
      const bookshelfEndX = 1135;
      const bookshelfStartY = 1579 - (bookshelfNum - 18) * 65;
      
      if (columnNum <= 8) {
        const x = bookshelfStartX + (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX - (columnNum - 9) * 24;
        const y = bookshelfStartY - 21;
        return { x, y };
    } else if (bookshelfNum >= 21 && bookshelfNum < 25) { // 4층 지도 6책장 하단부
      const bookshelfStartX = 1016;
      const bookshelfEndX = 1135;
      const bookshelfStartY = 1385 - (bookshelfNum - 21) * 65;

      if (bookshelfNum == 22) { // 기둥
        const bookshelfEndX = 1087;
        if (columnNum <= 4) {
          const x = bookshelfStartX + (columnNum - 1) * 24;
          const y = bookshelfStartY;
          return { x, y };
        }
          const x = bookshelfEndX - (columnNum - 5) * 24;
          const y = bookshelfStartY - 21;
          return { x, y };
      }
      if (columnNum <= 6) {
        const x = bookshelfStartX + (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX - (columnNum - 7) * 24;
        const y = bookshelfStartY - 21;
        return { x, y };
    } else if (bookshelfNum >= 25 && bookshelfNum < 33) { // 4층 지도 중앙 
      const bookshelfStartX = 1005;
      const bookshelfEndX = 1125;
      const bookshelfStartY = 1004 - (bookshelfNum - 25) * 65;

      if (bookshelfNum == 28) { // 기둥 
        const bookshelfEndX = 1077;
        if (columnNum <= 4) {
          const x = bookshelfStartX + (columnNum - 1) * 24;
          const y = bookshelfStartY;
          return { x, y };
        }
          const x = bookshelfEndX - (columnNum - 5) * 24;
          const y = bookshelfStartY - 21;
          return { x, y };
      }
      if (columnNum <= 6) {
        const x = bookshelfStartX + (columnNum - 1) * 24;
        const y = bookshelfStartY;
        return { x, y };
      }
        const x = bookshelfEndX - (columnNum - 7) * 24;
        const y = bookshelfStartY - 21;
        return { x, y };
    }
    } else if (category >= 400 && category < 500) { // 400 섹션에서의 좌표 찍기 알고리즘. 

    } else if (category >= 500 && category < 600) { // 500 섹션에서의 좌표 찍기 알고리즘. 

    }

    
  };

  return (
    <div style={{ position: 'relative' }}>
        <img id="book-map" src="/map.png" alt="Map" />

      {mapData && (
        <div>
          <p>책의 위치는 지도의 🔴 을 봐주세요!</p>
          <p>
             책 분야 : "{mapData.category >= 0 && mapData.category < 100
               ? '총류 및 컴퓨터'
               : mapData.category >= 100 && mapData.category < 200
               ? '철학'
               : mapData.category >= 200 && mapData.category < 300
               ? '종교'
               : mapData.category >= 300 && mapData.category < 400
               ? '사회과학'
               : mapData.category >= 400 && mapData.category < 500
               ? '언어'
               : mapData.category >= 500 && mapData.category < 600
               ? '자연과학'
               : '알 수 없는 분야'}"
           </p>
          <p>책장 번호 : "{mapData.bookshelfNum}번"</p>
          <p>칼럼 번호 : "{mapData.columnNum}구간"</p>
        </div>
      )}
      <canvas ref={canvasRef} style={{ position: 'absolute', top: 0, left: 0 }}></canvas>
    </div>
  );
};

export default Map;
