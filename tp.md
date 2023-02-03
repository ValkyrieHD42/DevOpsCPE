# TP2: GitHub Action

The first step was to prepare a .yml file to describe the actions to perform when you push code to github.
We first tried a simple clean previous build then build the new one with :
` mvn clean verify -X --file ./java-app/simpleapi/` 
The `-X` allows to run the build in debug mode.

Result:
![Screen of the result of the command above](./Capture du 2023-01-31 16-06-52.png)

Then we tried to create a quality gate using SonarCloud.
This allows to ensure that we push only clean code and ease the learning and using of best practice.

We first created an organization in SonarCloud.
We linked Martin's github's in order to test this deposit.
Once we added secrets and keys, we were able to link our SonarCloud tests to our deposit changing the build and test step of our .yml to :
`run: mvn -B verify sonar:sonar -Dsonar.projectKey=ValkyrieHD42_DevOpsCPE -Dsonar.organization=valkyriehd42 -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${{ secrets.SONAR_TOKEN }}  --file simple-api-student-main/pom.xml`