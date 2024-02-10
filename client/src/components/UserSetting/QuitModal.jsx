import React, { useState, useRef, useEffect } from "react";
import styled from "styled-components";
import { authtokenApi } from "../../util/http-commons";
import { useNavigate } from "react-router-dom"

const ModalOverlay = styled.div`
  display: ${(props) => (props.isOpen ? "flex" : "none")};
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  justify-content: center;
  align-items: center;
  z-index: 100;
`;

// 모달 전체의 정렬 설정
const ModalContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

// 모달 전체
const ModalContent = styled.div`
  background-color: #fffdfb;
  border: 1px solid black;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  width: 523px;
  height: 229px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

// 모달 제목
const ModalTitle = styled.div`
  font-family: "NanumSquareRound";
  font-size: 18px;
  color: black;
`;

// 텍스트 엔터 처리
export const EnterArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  align-self: flex-start;
  margin-left: 36px;
  margin-bottom: 33px;
  margin-top: 39px;
`;

const QuitTitle = styled.div`
  font-family: "NYJ Gothic B";
  font-size: 18px;
  color: black;
  margin-bottom: 5px;
  margin-left: 36px;
  align-self: flex-start;
`;

// 탈퇴 시 필요한 비밀번호
const QuitInput = styled.input`
  width: 448px;
  height: 46px;
  border: 1px solid black;
  border-radius: 3px;
  padding-left: 10px;

  &:focus {
    outline: 2px solid #e23e59;
    border: none;
  }
`;

const ModalButtonContainer = styled.div`
  width: 100%;
  display: flex;
  justify-content: center;
`;

const ModalButton = styled.button`
  border: 1px solid black;
  border-top: none;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
  background-color: #f9405e;
  width: 523px;
  height: 66px;
  color: white;
  font-family: "NYJ Gothic B";
  font-size: 20px;
`;

const QuitModal = ({ isOpen, onClose }) => {
  const navigate = useNavigate();

  const handleOverlayClick = (e) => {
    // 모달 배경 클릭 시 모달을 닫음
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const unresister = () => {
    authtokenApi.delete('/auth')
    .then(() => {
      onClose();
    })
    .then (() => {
      navigate('/')
    })
    .catch(error => {
      const errorType = error.response.status;

      if (errorType === 401) {
        alert("요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.")
      } else if(errorType === 403) {
        alert("기존 비밀번호가 일치하지 않습니다.")
      }
    })
  }

  return (
    <ModalOverlay isOpen={isOpen} onClick={handleOverlayClick}>
      <ModalContainer>
        <ModalContent>
          <EnterArea>
            <ModalTitle>정말 탈퇴하시겠습니까?</ModalTitle>
            <ModalTitle>회원 탈퇴를 위해 비밀번호를 입력해주십시오.</ModalTitle>
          </EnterArea>
          <QuitTitle>비밀번호</QuitTitle>
          <QuitInput type="password" />
        </ModalContent>
        <ModalButtonContainer>
          <ModalButton onClick={unresister}>탈퇴하기</ModalButton>
        </ModalButtonContainer>
      </ModalContainer>
    </ModalOverlay>
  );
};

export default QuitModal;