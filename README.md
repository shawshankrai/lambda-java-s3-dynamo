# lambda-java-s3-dynamo

For Maven Deployment

    RUN 1-cleanBuild.sh for downloading dependencies

    RUN 1-deploy.sh to trigger AWS SAM deployment

For Gradle Deployment

    RUN 2-cleanBuild-gradle.sh for downloading dependencies

    RUN 2-build-layer-gradle.sh for creating layer

    RUN 2-deploy-gradle.sh to trigger AWS SAM deployment



