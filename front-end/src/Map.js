import React, { useEffect } from 'react';
import $ from 'jquery';
import 'magnific-popup/dist/magnific-popup.css'; // Magnific Popup의 CSS 파일을 추가
import './Map.css';

const Map = ({ mapData }) => {
  useEffect(() => {
    const $ = window.jQuery;

    if ($) {
      // Magnific Popup 초기화
      $('.popup-link').magnificPopup({
        type: 'image',
        gallery: {
          enabled: true,
        },
        // 추가: 팝업에 Close 버튼 추가
        closeBtnInside: false,
        // 추가: 팝업 이미지 화면 중앙에 고정
        mainClass: 'mfp-fade mfp-align-center-horizontally mfp-align-center-vertically',
        // 추가: 팝업 이미지가 나올 때 팝업 이미지를 제외한 뒷배경을 어둡게
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
            // 팝업 이미지를 제외한 뒷배경을 어둡게
            $('body').css('background-color', 'rgba(0, 0, 0, 0.5)');
          },
          afterClose: () => {
            // 뒷배경을 원래대로
            $('body').css('background-color', 'white');
          },
        },
      });
    } else {
      console.error('jQuery is not available.');
    }
  }, []);

  return (
    <div>
      <p>찾으시는 책의 위치는 지도에 표시되어 있습니다. </p>
      {/* 여기에 이미지 파일 경로와 alt 텍스트를 넣어주세요 */}
      <a href="/map.png" className="popup-link">
        <img id="book-map" src="/map.png" alt="Map" />
      </a>
      {mapData && (
        <div>
          <p>책 분야 : "{mapData.category}"</p>
          <p>책장 번호 : "{mapData.bookshelfNum}"</p>
          <p>칼럼 번호 : "{mapData.columnNum}"</p>
        </div>
      )}
    </div>
  );
};

export default Map;
