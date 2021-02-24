import logging
import sys

import src.celery.celery_tasks as celery_tasks
from src import create_app
from src.scheduler.scheduler_tasks import start_scheduler_tasks

if __name__ == '__main__':
    app = create_app(sys.argv)
    start_scheduler_tasks(app, celery_tasks)
    log = logging.getLogger('pod.start_pod')

    while True:
        print("I am running!")
