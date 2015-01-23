@echo off

SET /P ANSWER=Are you sure you want to run this script? (Y/N)?
if /i {%ANSWER%}=={y} (goto :yes)
if /i {%ANSWER%}=={yes} (goto :yes)
goto :no

:yes

IF NOT EXIST build GOTO :nobuild
rd build /s /q
:nobuild

IF NOT EXIST app\build GOTO :noappbuild
rd app\build /s /q
:noappbuild

echo Operation completed successfully.
pause
exit /b 0

:no
exit /b 1 
