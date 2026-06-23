#!/bin/bash
cd /Users/graffiti/Desktop/呢喃

echo "📦 初始化 Git 仓库..."
git init

echo "📝 添加所有文件..."
git add .

echo "💾 首次提交..."
git commit -m "呢喃 - 汉化版私人日记应用"

echo "🔗 关联远程仓库..."
git remote add origin https://github.com/DenserMeerkat/ninan.git

echo "🚀 推送到 GitHub..."
git branch -M main
git push -u origin main

echo "✅ 完成！"
