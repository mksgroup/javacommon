set inputFilepath=D:/Documents/TouchAI20Days_PDFS/eBook_Cham toi AI trong 10 ngay_20200706_.pdf
set isResource=false
set curPasswd=_default
set outputFilepath='D:/Documents/TouchAI20Days_PDFS/Bia+Noidung/official-sales/eBook_Cham toi AI trong 10 ngay_20200706_%1_%2.pdf'
set ownerPassword=%1
set userPassword=%2
mvn spring-boot:run -Dspring-boot.run.arguments="--inputFilepath='%inputFilepath%' --isResource=%isResource% --curPasswd=%curPasswd% --outputFilepath=%outputFilepath% --ownerPassword=%ownerPassword% --userPassword=%userPassword%"