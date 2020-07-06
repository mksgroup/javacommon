set inputFilepath=D:/Documents/TouchAI20Days_PDFS/eBook_Cham toi AI trong 10 ngay_20200706_.pdf
set isResource=false
set curPasswd=_default
set outputFilepath='D:/Documents/TouchAI20Days_PDFS/Bia+Noidung/official-sales/eBook_Cham toi AI trong 10 ngay_20200706_ABC.pdf'
set ownerPassword=Nguyen Van A
set userPassword=ABC
mvn spring-boot:run -Dspring-boot.run.arguments="--inputFilepath='%inputFilepath%' --isResource=%isResource% --curPasswd=%curPasswd% --outputFilepath=%outputFilepath% --ownerPassword=%ownerPassword% --userPassword=%userPassword%"