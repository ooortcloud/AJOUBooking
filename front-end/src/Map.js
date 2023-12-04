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

        const coordinates = getBookshelfCoordinates(mapData.bookshelfNum, mapData.columnNum, img.width, img.height);

        console.log('Computed Coordinates:', coordinates);

        drawRedDotOnCanvas(img, coordinates);
        // magnific-popup 관련 코드 제외
      };
    }
  }, [mapData]);

  const getBookshelfCoordinates = (bookshelfNum, columnNum, imageWidth, imageHeight) => {
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
  };

  return (
    <div style={{ position: 'relative' }}>
        <img id="book-map" src="/map.png" alt="Map" />

      {mapData && (
        <div>
          <p>책의 위치는 지도의 🔴 을 봐주세요!</p>
          <p>
            책 분야 : "{mapData.category === 0
              ? '총류 및 컴퓨터'
              : mapData.category === 1
              ? '철학'
              : mapData.category === 2
              ? '종교'
              : mapData.category === 3
              ? '사회과학'
              : mapData.category === 4
              ? '언어'
              : mapData.category === 5
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
