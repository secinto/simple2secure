echo Installing python libraries
pip install -r requirements.txt
echo Starting python script for retrieving license
python /src/__init_docker.py
echo Creating new simple2secure pod docker image
docker-compose build
echo Starting new simple2secure pod docker container
docker-compose up -d