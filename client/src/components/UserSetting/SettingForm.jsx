import { styled } from "styled-components";
import DancyImg from "../../assets/join/BigLogo.png";
import * as SF from "./SettingForm.style";
import Form from "./Form";
import FormHeader from "./FormHeader";

export const JoinArea = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  align-content: center;
  flex-direction: column;
`;

export const AlignArea = styled.div`
  display: flex;
  margin-top: 60px;
  width: 100%;
`;

// 전체 폼 구성
export const JoinFormArea = styled.div`
  display: flex;
  text-align: center;
  justify-content: center;
  align-items: center;
  flex-direction: column;
`;

export const LogoArea = styled.div`
  flex: 2;
  display: flex;
  margin-left: 20px;
  justify-content: end;
`;

export const ContextArea = styled.div`
  flex: 8;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const CenterContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: start;
`;

export default function SettingForm() {
  return (
    <JoinArea>
      <AlignArea>
        <LogoArea>
          <CenterContainer>
            <SF.JoinLogo>
              <img src={DancyImg}></img>
            </SF.JoinLogo>
            <SF.FormTitle>회원 상세정보</SF.FormTitle>
          </CenterContainer>
        </LogoArea>
        <ContextArea>
          <CenterContainer>
            <FormHeader />
            <Form />
          </CenterContainer>
        </ContextArea>
      </AlignArea>
    </JoinArea>
  );
}
