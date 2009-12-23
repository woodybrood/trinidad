mvn deploy:deploy-file -Durl=sftp://neuri.com/home/webadmin/maven.neuri.com/html -Dfile=/tmp/fitnesse.jar -DgroupId=org.fitnesse -DartifactId=fitnesse -Dversion=20091221-SNAPSHOT -Dpackaging=jar -DrepositoryId=neuri-maven
#mvn install:install-file -Dfile=/tmp/fitnesse.jar -DgroupId=org.fitnesse -DartifactId=fitnesse -Dversion=20091221-SNAPSHOT -Dpackaging=jar
