@echo off
chcp 65001 >nul 2>&1

echo ============================================
echo   フリマ社内システム 起動中...
echo ============================================
echo.
echo   起動完了後、ブラウザが自動で開きます
echo   手動で開く場合: http://localhost:8080/
echo.
echo   ログイン情報:
echo     メール: user0@sample.com
echo     パスワード: pass
echo.
echo   停止するには Ctrl+C を押してください
echo ============================================
echo.

start "" cmd /c "timeout /t 30 /nobreak >nul && start http://localhost:8080/"

call "%~dp0gradlew.bat" bootRun
pause
