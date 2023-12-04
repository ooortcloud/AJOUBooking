import React, { useEffect, useRef } from 'react';
import $ from 'jquery';
import 'magnific-popup/dist/magnific-popup.css';
import './Map.css';

const Map = ({ mapData }) => {
  const canvasRef = useRef(null);
  const initializeMagnificPopup = () => {
    // jQuery 대신 $ 사용
    if (window.jQuery) {
      window.jQuery.magnificPopup.open({
        items: {
          src: '/map.png',
          type: 'image',
        },
        closeBtnInside: false,
        mainClass: 'mfp-fade mfp-align-center-horizontally mfp-align-center-vertically',
        closeOnBgClick: false,
        closeOnContentClick: false,
        closeOnEsc: false,
        fixedContentPos: false,
        overflowY: 'auto',
        scrollOutside: false,
        zoom: {
          enabled: true,
          easing: 'ease-in-out',
          duration: 250,
        },
        callbacks: {
          beforeOpen: () => {
            $('body').css('background-color', 'rgba(0, 0, 0, 0.5)');
          },
          afterClose: () => {
            $('body').css('background-color', 'white');
            $('body').css('overflow', 'auto');
          },
        },
      });
    } else {
      console.error('jQuery is not available.');
    }
  };
  useEffect(() => {
    

    const drawRedDotOnCanvas = (img, coordinates) => {
      const canvas = canvasRef.current;
      const context = canvas.getContext('2d');

      context.clearRect(0, 0, canvas.width, canvas.height);

      if (img.width && img.height) {
        const ratioX = canvas.width / img.width;
        const ratioY = canvas.height / img.height;

        context.fillStyle = 'red';
        context.beginPath();
        context.arc(coordinates.x * ratioX, coordinates.y * ratioY, 5, 0, 2 * Math.PI);
        context.fill();

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
        initializeMagnificPopup(); // 이미지가 로드된 후 팝업 열기
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
      {/* 팝업 열기 위한 커스텀 링크 */}
      <div className="popup-link" onClick={() => initializeMagnificPopup()}>
        <img id="book-map" src="/map.png" alt="Map" />
      </div>

      {/* 버튼을 클릭하여 팝업 열기 */}
      <button className="open-popup-button" onClick={() => initializeMagnificPopup()}>
        Open Map
      </button>

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
