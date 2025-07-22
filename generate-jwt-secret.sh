#!/bin/bash

# 랜덤 32바이트 키 생성 후 Base64 인코딩
SECRET=$(openssl rand -base64 32)

echo "✅ 생성된 Base64 인코딩된 JWT 시크릿 키:"
echo "$SECRET"

# 환경변수로 설정 (현재 세션에만 적용)
export JWT_SECRET=$SECRET
echo "✅ JWT_SECRET 환경변수 설정 완료 (현재 세션)"

# ~/.bash_profile에 추가할지 물어보기
read -p "📌 이 키를 ~/.bash_profile에 영구 저장할까요? (y/N): " answer
if [[ "$answer" =~ ^[Yy]$ ]]; then
  echo "export JWT_SECRET=$SECRET" >> ~/.bash_profile
  source ~/.bash_profile
  echo "✅ ~/.bash_profile에 저장 완료"
else
  echo "❗ ~/.bash_profile에는 저장하지 않았습니다. EC2 재부팅 후 유지하려면 직접 추가하세요."
fi
