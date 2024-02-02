import { styled } from "styled-components";
import DancyImg from "../../assets/join/BigLogo.png";
import * as JF from "./SettingForm.style";
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


export const EnterArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  margin-right: ${(props) => props.margin || "0px"};
`;

export const SubmitArea = styled.div`
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 4rem;
  margin-bottom: 4rem;
`;

export default function SettingForm() {
  return (
    <JoinArea>
      <AlignArea>
        <LogoArea>
          <CenterContainer>
            <JF.JoinLogo>
              <img src={DancyImg}></img>
            </JF.JoinLogo>
            <JF.FormTitle>회원 상세정보</JF.FormTitle>
          </CenterContainer>
        </LogoArea>
        <ContextArea>
          <CenterContainer>
           <FormHeader/>
            <Form />
          </CenterContainer>
        </ContextArea>
      </AlignArea>
      <SubmitArea>
        <JF.RegisterBtn>완료</JF.RegisterBtn>
      </SubmitArea>
    </JoinArea>
  );
}
